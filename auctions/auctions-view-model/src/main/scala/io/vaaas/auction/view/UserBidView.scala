package io.vaaas.auction.view

import java.time.Instant

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec
import io.vaaas.MoneyCodecs
import squants.Money

case class UserBidView(
                        size: Money,
                        split: Seq[Int],
                        paid: Option[Instant]
                      )
object UserBidView extends MoneyCodecs {
  implicit val codec: Codec[UserBidView] = deriveCodec
}
