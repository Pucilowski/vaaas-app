package io.vaaas.user.auth.command

import io.circe.Codec
import io.vaaas.tapir.TapirDsl
import sttp.tapir.EndpointIO

case class Login(
  email: String,
  password: String,
  hoursValid: Option[Int] = None
)

object Login extends TapirDsl {
  implicit val codec: Codec[Login] = io.circe.generic.semiauto.deriveCodec

  val body: EndpointIO.Body[String, Login] = jsonBody[Login]
}
