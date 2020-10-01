package io.vaaas

import io.vaaas.user.auth.{SessionConfig, SignupConfig}

case class SaasConfig
(
  signup: SignupConfig,
  session: SessionConfig,
  db: DatabaseConfig
)
