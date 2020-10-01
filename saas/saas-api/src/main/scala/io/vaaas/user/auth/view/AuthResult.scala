package io.vaaas.user.auth.view

import io.circe.Codec
import io.vaaas.tapir.TapirDsl
import io.vaaas.user.auth.ApiKeyId
import io.vaaas.user.view.UserProfileView
import sttp.tapir.EndpointIO

case class AuthResult(
                       token: ApiKeyId,
                       profile: UserProfileView
                     )

object AuthResult extends TapirDsl {
  implicit val codec: Codec[AuthResult] = io.circe.generic.semiauto.deriveCodec

  val body: EndpointIO.Body[String, AuthResult] = jsonBody[AuthResult]
}
