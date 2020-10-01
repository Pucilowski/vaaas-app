package io.vaaas.user.auth

case class SignupConfig(
  inviteOnly: Boolean,
  passwordRules: List[PasswordRule]
)

case class PasswordRule(message: String, pattern: String) {
  def apply(rawPassword: String): Boolean = {
    pattern.r.findFirstIn(rawPassword).isDefined
  }
}
