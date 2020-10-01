package io.vaaas.auction.view

import java.time.Instant

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec
import io.vaaas.MoneyCodecs
import io.vaaas.auction.{ AuctionKey, AuctionStatus }
import io.vaaas.user.UserId

import io.circe.generic.auto._

case class AuctionView(
  auctionId: AuctionKey,
  sellerId: UserId,
  endsAt: Instant,
  reserve: BigDecimal,
  reserveCurrency: String,
  targets: List[BidTargetView],
  status: AuctionStatus,
  bids: Map[UserId, UserBidView],
  version: Long
)

object AuctionView extends MoneyCodecs {
  implicit val codec: Codec[AuctionView] = deriveCodec
}
