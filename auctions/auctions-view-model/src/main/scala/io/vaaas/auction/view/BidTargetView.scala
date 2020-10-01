package io.vaaas.auction.view

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec

case class BidTargetView
(
  id: String,
  name: String,
  min: BigDecimal,
  suggested: BigDecimal,
  max: BigDecimal
)

object BidTargetView {
  implicit val bidTargetCodec: Codec[BidTargetView] = deriveCodec
}
