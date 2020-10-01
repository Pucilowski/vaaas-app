package io.vaaas.auction.eventsourced

import java.time.Instant

import aecor.macros.boopickleWireProtocol
import cats.tagless.autoFunctorK
import boopickle.Default._
import AuctionWireCodecs._
import io.vaaas.auction.{BidTarget, RawMoney}
import io.vaaas.user.UserId
import squants.Money

@autoFunctorK(false)
@boopickleWireProtocol
trait Auction[F[_]] {
  def start(userId: UserId, endsAt: Instant, reserve: RawMoney, targets: List[BidTarget]): F[Unit]

  def placeBid(userId: UserId, size: Money, split: Seq[Int]): F[Unit]

  def withdrawBid(userId: UserId): F[Unit]

  def expire: F[Unit]
}

object Auction {
//  import boopickle.Default._
//  import AuctionWireCodecs._

//  implicit val functorK: FunctorK[Auction] = Derive.functorK
//  implicit val wireProtocol: WireProtocol[Auction] = BoopickleWireProtocol.derive
}
