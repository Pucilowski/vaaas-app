package io.vaaas.user

import io.vaaas.tapir.TapirDsl
import sttp.tapir.{Codec, CodecFormat, EndpointInput}

trait UsersDsl extends TapirDsl {

  implicit val userIdTC: Codec[String, UserId, CodecFormat.TextPlain] =
    Codec.long.map(UserId(_))(_.value)

  val userId: EndpointInput.PathCapture[UserId] = path[UserId]("userId")

}
