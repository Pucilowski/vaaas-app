package io.vaaas

//import cats.effect.Blocker
import com.zaxxer.hikari.HikariConfig
import doobie.hikari.HikariTransactor
import doobie.util.transactor.Transactor
import zio._
import zio.blocking.Blocking
import zio.clock.Clock
import zio.interop.catz._

object Doobie {

  def mkTransactor(config: DatabaseConfig): RManaged[Blocking, Transactor[Task]] =
    ZIO.runtime[Blocking].toManaged_.flatMap { implicit rt =>
      val transactEC = rt.environment.get.blockingExecutor.asEC
      val connectEC = rt.platform.executor.asEC

      HikariTransactor
        .newHikariTransactor[Task](
          config.driver,
          config.url,
          config.user,
          config.pass,
          connectEC,
          transactEC
        )
        .toManaged
    }

  private def hikari(cfg: DatabaseConfig): HikariConfig = {
    val config = new com.zaxxer.hikari.HikariConfig

    config.setJdbcUrl(cfg.url)
    config.setSchema(cfg.schema)
    config.setUsername(cfg.user)
    config.setPassword(cfg.pass)

    config
  }
}
