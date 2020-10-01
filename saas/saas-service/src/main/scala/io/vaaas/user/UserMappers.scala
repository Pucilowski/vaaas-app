package io.vaaas.user

import doobie.Meta
import io.circe.{Decoder, Encoder}
import io.vaaas.DoobieMappers
import io.vaaas.{Meta => MetaMap}
import tsec.passwordhashers.PasswordHash
import tsec.passwordhashers.jca.SCrypt

trait UserMappers extends DoobieMappers {

  implicit val idMeta: Meta[UserId] =
    Meta.LongMeta.timap(UserId.apply)(_.value)

  implicit val emailMeta: Meta[Email] =
    Meta.StringMeta.timap(Email.unsafe)(_.value)

  implicit val passwordHashMeta: Meta[PasswordHash[SCrypt]] =
    implicitly[Meta[String]].asInstanceOf[Meta[PasswordHash[SCrypt]]]

  implicit val metaMeta: Meta[MetaMap] = jsonMeta.timap { j =>
    val result = Decoder[MetaMap].decodeJson(j)

    result.right.get
  }(s => Encoder[MetaMap].apply(s))

}
