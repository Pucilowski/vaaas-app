package io.vaaas.auction

import java.time.Instant

import io.vaaas.auction.eventsourced.{AuctionCommandRejection, Auctions}
import io.vaaas.user.context
import io.vaaas.{BadRequest, UserTask}
import squants.Money
import zio.Task

final class LiveAuctionService
(
  auctions: Auctions[Task],
  //  auctionViews: AuctionViewRepository[Task]
) extends AuctionService {

  private def runCommand[A](response: Task[Either[AuctionCommandRejection, A]]): Task[A] = {
    response.absolve[Throwable, A]
      .mapError {
        r => BadRequest(s"Bad Request: $r")
      }
  }

  def start(
             auctionKey: Option[AuctionKey],
             endsAt: Instant,
             reserve: RawMoney,
             targets: List[BidTarget]
           ): UserTask[Unit] = {
    val key = auctionKey.getOrElse(AuctionKey())
    for {
      userId <- context.userId
      result <- runCommand(auctions(key).start(userId, endsAt, reserve, targets))
    } yield result
  }

  def placeBid(
                key: AuctionKey,
                size: Money,
                split: Seq[Int]
              ): UserTask[Unit] = for {
    userId <- context.userId
    _ <- runCommand(auctions(key).placeBid(userId, size, split))
  } yield ()


  def withdrawBid(key: AuctionKey): UserTask[Unit] = for {
    userId <- context.userId
    _ <- runCommand(auctions(key).withdrawBid(userId))
  } yield ()


  //  def state(key: AuctionKey): F[Either[AuctionCommandRejection, AuctionState]] =
  //    auctions(key).stateExists

  //  def findOne(key: AuctionKey): F[Option[AuctionView]] =
  //    auctionViews.get(key)

  //  def findAll(): fs2.Stream[F, AuctionView] =
  //    auctionViews.all()
}

object LiveAuctionService {
  def apply(auctions: Auctions[Task]): AuctionService = {
    new LiveAuctionService(auctions)
  }
}
