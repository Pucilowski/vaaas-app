package io.vaaas.auction.command

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec
import io.vaaas.auction.RawMoney
import io.vaaas.tapir.TapirDsl
import sttp.tapir.EndpointIO

case class PlaceBid(
  size: RawMoney,
  split: Seq[Int]
)

object PlaceBid extends TapirDsl {
  implicit val codec: Codec[PlaceBid] = deriveCodec

  val body: EndpointIO.Body[String, PlaceBid] = jsonBody[PlaceBid]
}
