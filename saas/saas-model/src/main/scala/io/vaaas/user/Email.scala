package io.vaaas.user

import cats.data.{Validated, ValidatedNel}
import io.circe.{Codec, Decoder, Encoder}
import io.vaaas.BadRequest.RequestError

case class Email private (value: String) extends AnyVal

object Email {
  implicit val emailCodec: Codec[Email] = Codec.from(
    Decoder.decodeString.map { x =>
      println(s"decoding $x")
      Email.unsafe(x)
    },
    Encoder.encodeString.contramap(_.value)
  )

  private val emailRegex =
    """^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$""".r

  def apply(value: String): ValidatedNel[RequestError, Email] =
    Validated.condNel(
      emailRegex.findFirstMatchIn(value).isDefined,
      new Email(value),
      RequestError("email", "Invalid email")
    )

  def unsafe(value: String): Email = new Email(value)
}
