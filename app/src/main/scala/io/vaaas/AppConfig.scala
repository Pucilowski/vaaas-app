package io.vaaas

import io.vaaas.http.HttpConfig
import pureconfig.{ConfigReader, ConfigSource}
import pureconfig.generic.semiauto.deriveReader

case class AppConfig
(
  http: HttpConfig,
  saas: SaasConfig,
  auctions: AuctionsConfig
)

object AppConfig {
  import pureconfig.generic.auto._

  implicit val reader: ConfigReader[AppConfig] = deriveReader

  val load: AppConfig = {
    ConfigSource.default.loadOrThrow[AppConfig]
  }
}
