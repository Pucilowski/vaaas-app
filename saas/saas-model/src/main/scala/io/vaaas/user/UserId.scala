package io.vaaas.user

import cats.Eq
import io.circe.generic.extras.AutoDerivation
import io.circe.generic.extras.semiauto.deriveUnwrappedCodec
import io.circe.{Codec, KeyDecoder, KeyEncoder}
import io.vaaas.Meta
import io.vaaas.user.context.UserContext.Context
import tsec.common.VerificationStatus
import tsec.passwordhashers.PasswordHash
import tsec.passwordhashers.jca.SCrypt

case class UserId(value: Long) extends AnyVal

object UserId extends AutoDerivation {
  implicit val eqInstance: Eq[UserId] = Eq.fromUniversalEquals

  val none: UserId = UserId(-1L)

  implicit val codec: Codec[UserId] = {
//    import io.circe.generic.extras.defaults.defaultGenericConfiguration
    deriveUnwrappedCodec[UserId]
  }

  implicit val userIdKeyDecoder: KeyDecoder[UserId] =
    KeyDecoder.decodeKeyLong.map(UserId.apply)

  implicit val userIdKeyEncoder: KeyEncoder[UserId] =
    key => KeyEncoder.encodeKeyLong(key.value)
}

case class User
(
  id: UserId,
  email: Email,
  passwordHash: PasswordHash[SCrypt],
  firstName: String,
  lastName: String,
  meta: Meta = Map.empty
) {
  def withPassword(newPassword: String): User = copy(
    passwordHash = User.hashPassword(newPassword)
  )

  def verifyPassword(password: String): VerificationStatus = {
    SCrypt.checkpw[cats.Id](password, passwordHash)
  }

  def toCtx: Context = Context(
    id
  )
}

object User {
  def hashPassword(password: String): PasswordHash[SCrypt] = SCrypt.hashpw[cats.Id](password)
}
