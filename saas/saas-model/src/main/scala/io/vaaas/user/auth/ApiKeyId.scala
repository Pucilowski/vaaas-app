package io.vaaas.user.auth

import io.circe.Codec
import io.circe.generic.extras.AutoDerivation
import tsec.common.SecureRandomId
import io.circe.generic.extras.semiauto._

case class ApiKeyId(value: String) extends AnyVal

object ApiKeyId extends AutoDerivation {
  def apply(): ApiKeyId =
    ApiKeyId(SecureRandomId.Strong.generate)

  implicit val codec: Codec[ApiKeyId] = deriveUnwrappedCodec[ApiKeyId]
}
