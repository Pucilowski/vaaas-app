package io.vaaas.auction.process

import java.time.Instant

import cats.effect.Sync
import cats.implicits._
import io.vaaas.auction.eventsourced.Auctions
import io.vaaas.auction.view.AuctionViewRepository

class AuctionExpirationProcess[F[_] : Sync](auctions: Auctions[F],
                                            auctionView: AuctionViewRepository[F]
                                           )
  extends (Instant => F[Unit]) {

  def apply(now: Instant): F[Unit] =
    auctionView
      .ended(now)
      .evalMap(k => auctions(k).expire.void)
      .compile
      .drain

}
