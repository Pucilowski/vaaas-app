package vickrey

import java.time.Instant

import aecor.MonadActionReject
import cats.data.OptionT
import cats.syntax.all._
import squants.Money
import squants.market.Money
import vickrey.AuctionStatus.{ Ended, Live }
import vickrey.Event.{ AuctionEnded, AuctionStarted, BidPlaced }
import vickrey.Rejection.{ AuctionExists, AuctionHasEnded, AuctionNotFound, BidBelowReserve, TooEarlyToExpire }

trait Auction[F[_]] {
  def start(userId: UserId, reserve: Money, endsAt: Instant): F[Unit]

  def placeBid(userId: UserId, size: Money): F[Unit]

  def expire(now: Instant): F[Unit]
}

class EventsourcedAuction[F[_]](implicit F: MonadActionReject[F, Option[AuctionState], Event, Rejection])
    extends Auction[F] {

  import F._

  val ignore: F[Unit] = unit

  def rejectIf(condition: => Boolean, reason: Rejection): F[Unit] =
    if (condition) pure() else reject(reason)

  def auctionExists: F[AuctionState] = OptionT(read).getOrElseF(reject(AuctionNotFound))

  def auctionIsLive: F[AuctionState] = for {
    auction <- auctionExists
    _ <- rejectIf(auction.hasEnded, reason = AuctionHasEnded)
  } yield auction

  override def start(sellerId: UserId, reserve: Money, endsAt: Instant): F[Unit] = for {
    state <- read
    _ <- rejectIf(state.isEmpty, AuctionExists)

    _ <- append(AuctionStarted(sellerId, reserve, endsAt))
  } yield ()

  override def placeBid(userId: UserId, size: Money): F[Unit] = for {
    auction <- auctionIsLive
    _ <- rejectIf(size < auction.reserve, BidBelowReserve)

    _ <- append(BidPlaced(userId, size))
  } yield ()

  override def expire(now: Instant): F[Unit] = for {
    auction <- auctionIsLive
    _ <- rejectIf(now.isBefore(auction.endsAt), reason = TooEarlyToExpire)
    _ <- append(AuctionEnded(now))
  } yield ()

}
