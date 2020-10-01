package io.vaaas.user.auth.command

import io.circe.Codec
import io.vaaas.tapir.TapirDsl
import sttp.tapir.EndpointIO

case class Signup(
                   email: String,
                   password: String,
                   firstName: String,
                   lastName: String
                 )

object Signup extends TapirDsl {
  implicit val codec: Codec[Signup] = io.circe.generic.semiauto.deriveCodec

  val body: EndpointIO.Body[String, Signup] = jsonBody[Signup]
}
