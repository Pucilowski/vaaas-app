package io.vaaas.auction

case class BidTarget
(
  id: String,
  name: String,
  min: BigDecimal,
  suggested: BigDecimal
)
