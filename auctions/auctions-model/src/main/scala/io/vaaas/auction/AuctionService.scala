package io.vaaas.auction

import java.time.Instant

import io.vaaas.UserTask
import squants.Money

trait AuctionService {

  def start(
             auctionKey: Option[AuctionKey],
             endsAt: Instant,
             reserve: RawMoney,
             targets: List[BidTarget]
             //           ): F[Either[AuctionCommandRejection, Unit]]
           ): UserTask[Unit]

  def placeBid(
                key: AuctionKey,
                size: Money,
                split: Seq[Int]
                //              ): F[Either[AuctionCommandRejection, Unit]]
              ): UserTask[Unit]

  def withdrawBid(
                   key: AuctionKey
                 ): UserTask[Unit]

  //  def state(key: AuctionKey): F[Either[AuctionCommandRejection, AuctionState]]
}
