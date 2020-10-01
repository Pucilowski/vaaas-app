package io.vaaas.user

import cats.data.{ Validated, ValidatedNel }
import cats.implicits._
import io.vaaas.BadRequest.RequestError
import io.vaaas.user.auth.PasswordRule
import tsec.passwordhashers.PasswordHash
import tsec.passwordhashers.jca.SCrypt

case class Password private (hash: PasswordHash[SCrypt]) extends AnyVal

object Password {

  def apply(rawPassword: String)(rules: List[PasswordRule]): ValidatedNel[RequestError, Password] = {
    def applyRule(rule: PasswordRule): ValidatedNel[RequestError, Unit] =
      Validated.condNel(
        rule.pattern.r.findFirstIn(rawPassword).isDefined,
        (),
        RequestError("password", rule.message)
      )

    rules.map(applyRule).sequence.as(Password.unsafe(rawPassword))
  }

  def unsafe(rawPassword: String): Password = {
    new Password(User.hashPassword(rawPassword))
  }

}
