package io.vaaas.auction.command

import java.time.Instant

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec
import io.vaaas.auction.{ BidTarget, RawMoney }
import io.vaaas.tapir.TapirDsl
import sttp.tapir.EndpointIO
import io.circe.generic.auto._

case class StartAuction(
  endsAt: Instant,
  reserve: RawMoney,
  targets: List[BidTarget]
)

object StartAuction extends TapirDsl {
  implicit val codec: Codec[StartAuction] = deriveCodec

  val body: EndpointIO.Body[String, StartAuction] = jsonBody[StartAuction]
}
