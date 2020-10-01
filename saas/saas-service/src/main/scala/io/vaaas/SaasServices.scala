package io.vaaas

import doobie.Transactor
import io.vaaas.user.auth.{ApiKeyRepository, ApiKeyService, AuthService, LiveAuthService}
import io.vaaas.user.{Auth, LiveUserService, UserRepository, UserService}
import zio.blocking.Blocking
import zio.clock.Clock
import zio._

object SaasServices {
  type SaasServices = Has[UserService] with Has[AuthService]

  private def apply(xa: Transactor[Task], clock: Clock.Service)(cfg: SaasConfig): SaasServices = {
    val userRepo = UserRepository(xa)
    val apiKeyRepo = ApiKeyRepository(xa)

    val apiKeyService = new ApiKeyService(apiKeyRepo, clock)(cfg.session)
    val apiKeyAuth = new Auth(apiKeyRepo, clock)

    val userService = LiveUserService(userRepo)
    val authService = LiveAuthService(userRepo, apiKeyService, apiKeyAuth)(cfg.signup)

    Has(userService) ++ Has(authService)
  }

  def live(cfg: SaasConfig): ZLayer[Blocking with Clock, Nothing, SaasServices] = {
    val xa = Blocking.live >>> Doobie.mkTransactor(cfg.db).orDie.toLayer
    val clock = ZLayer.requires[Clock]

    val wire = ZLayer.fromServicesMany[Transactor[Task], Clock.Service, SaasServices] {
      SaasServices(_, _)(cfg)
    }

    val wired = (xa ++ clock) >>> wire

    Flyway.initDb(cfg.db).orDie.toLayer ++ wired
  }

  //  private def initDb(cfg: DatabaseConfig): UIO[Unit] =
  //    Flyway.initDb(cfg).orDie

}
