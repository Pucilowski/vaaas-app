package io.vaaas.user.view

import io.circe.Codec
import io.scalaland.chimney.dsl._
import io.vaaas.Meta
import io.vaaas.tapir.TapirDsl
import io.vaaas.user.{Email, User, UserId}
import sttp.tapir.EndpointIO

case class UserProfileView
(
  id: UserId,
  email: Email,
  firstName: String,
  lastName: String,
  meta: Meta = Map.empty
)

object UserProfileView extends TapirDsl {
  implicit val codec: Codec[UserProfileView] = io.circe.generic.semiauto.deriveCodec

  val body: EndpointIO.Body[String, UserProfileView] = jsonBody[UserProfileView]

  def apply(user: User): UserProfileView =
    user.into[UserProfileView]
      .transform
}
