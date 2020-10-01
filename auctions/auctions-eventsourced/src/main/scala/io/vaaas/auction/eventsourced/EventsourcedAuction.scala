package io.vaaas.auction.eventsourced

import java.time.Instant
import java.util.concurrent.TimeUnit

import aecor.MonadActionLiftReject
import aecor.data.{ EitherK, EventTag, EventsourcedBehavior, Tagging }
import aecor.encoding.{ KeyDecoder, KeyEncoder }
import cats.Monad
import cats.data.Validated.{ Invalid, Valid }
import cats.data.{ OptionT, ValidatedNec }
import cats.effect.Clock
import cats.syntax.all._
import io.vaaas.auction.AuctionStatus.{ Ended, Live }
import io.vaaas.auction.{ AuctionKey, AuctionStatus, BidTarget, RawMoney }
import io.vaaas.auction.eventsourced.state.{ AuctionState, Bid }
import io.vaaas.user.UserId
import squants.market.{ Money, MoneyContext }

class EventsourcedAuction[F[_], I[_]](clock: Clock[F])(implicit
  I: MonadActionLiftReject[I, F, Option[AuctionState], AuctionEvent, AuctionCommandRejection],
  fxContext: MoneyContext
) extends Auction[I] {

  import I._

  val ignore: I[Unit] = I.unit

  def currentTime: I[Instant] =
    liftF(clock.realTime(TimeUnit.MILLISECONDS)).map(Instant.ofEpochMilli)

  def assert(condition: => Boolean, reason: AuctionCommandRejection): I[Unit] =
    if (condition) ignore else reject(reason)

  def auctionExists: I[AuctionState] = OptionT(read).getOrElseF(reject(AuctionNotFound))

  def auctionIsLive: I[AuctionState] = for {
    auction <- auctionExists
    _ <- assert(auction.hasEnded, AuctionHasEnded)
  } yield auction

  def start(sellerId: UserId, expiry: Instant, rawReserve: RawMoney, targets: List[BidTarget]): I[Unit] =
    for {
      reserve <- liftValidated(rawReserve.parse)
      state <- read
      _ <- assert(state.isEmpty, AuctionExists)

      _ <- append(AuctionStarted(sellerId, reserve, targets, expiry))
    } yield ()

  def placeBid(userId: UserId, size: Money, split: Seq[Int]): I[Unit] = {
    val rawBid = Bid.Raw(RawMoney(size.amount, size.currency.code), split)

    for {
      bid <- liftValidated(rawBid.parse)
      auction <- auctionIsLive
      _ <- assert(bid.size >= auction.reserve, BidBelowReserve)

      _ <- append(BidPlaced(userId, bid))
    } yield ()
  }

  def withdrawBid(userId: UserId): I[Unit] = for {
      auction <- auctionIsLive
      _ <- assert(auction.bids.contains(userId), BidRejected("No bid for user"))

      _ <- append(BidWithdrawn(userId))
    } yield ()

  def end(): I[Unit] = status.flatMap {
    case Live  => append(AuctionEnded)
    case Ended => ignore
  }

  def expire: I[Unit] =
    for {
      now <- currentTime
      state <- auctionExists
      _ <- state.status match {
        case Live if now.isAfter(state.endsAt) => append(AuctionEnded)
        case Live                              => reject(TooEarlyToExpire)
        case Ended                             => reject(AuctionHasEnded)
      }
    } yield ()

  def reserve: I[Money] = read.flatMap {
    case Some(s) => pure(s.reserve)
    case _       => reject(AuctionNotFound)
  }

  def status: I[AuctionStatus] = read.flatMap {
    case Some(s) => pure(s.status)
    case _       => reject(AuctionNotFound)
  }

  def liftValidated[P](validated: ValidatedNec[String, P]): I[P] =
    validated match {
      case Valid(a)   => pure(a)
      case Invalid(e) => reject(ValidationError(e.toList))
    }

}

object EventsourcedAuction {

  implicit val keyEncoder: KeyEncoder[AuctionKey] = KeyEncoder.encodeKeyString.contramap(_.value)
  implicit val keyDecoder: KeyDecoder[AuctionKey] = KeyDecoder.decodeKeyString.map(AuctionKey.apply)

  def behavior[F[_]: Monad](clock: Clock[F])(implicit fxContext: MoneyContext): EventsourcedBehavior[EitherK[
    Auction,
    AuctionCommandRejection,
    ?[_]
  ], F, Option[AuctionState], AuctionEvent] =
    EventsourcedBehavior.optionalRejectable(new EventsourcedAuction(clock), AuctionState.init, _.handleEvent(_))
//      .rejectable(new EventsourcedAuction(clock), AuctionState.fold)

  val entityName: String = "Auction"
  val entityTag: EventTag = EventTag(entityName)
  val tagging: Tagging[AuctionKey] = Tagging.partitioned(20)(entityTag)

}
