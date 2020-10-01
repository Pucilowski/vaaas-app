package io.vaaas.auction.view

import java.time.Instant

import io.vaaas.auction.AuctionKey

trait AuctionViewRepository[F[_]] {
  def get(key: AuctionKey): F[Option[AuctionView]]

  def set(view: AuctionView): F[Unit]

  def all(): fs2.Stream[F, AuctionView]

  def ended(now: Instant): fs2.Stream[F, AuctionKey]

  //  def byOwner(userId: UserId): F[List[AuctionView]]
}
