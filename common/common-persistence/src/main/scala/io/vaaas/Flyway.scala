package io.vaaas

import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import zio.Task

object Flyway {

  def initDb(cfg: DatabaseConfig): Task[Unit] = Task {
    println(s"Running Flyway for: $cfg")

    org.flywaydb.core.Flyway
      .configure()
      .schemas(cfg.schema)
      .locations(s"db/migration/${cfg.schema}")
      .dataSource(new HikariDataSource(hikari(cfg)))
      .load()
      .migrate()
  }.unit

  private def hikari(cfg: DatabaseConfig): HikariConfig = {
    import com.zaxxer.hikari.HikariConfig

    val config = new HikariConfig

    config.setJdbcUrl(cfg.url + s"?current_schema=${cfg.schema}")
    config.setSchema(cfg.schema)
    config.setUsername(cfg.user)
    config.setPassword(cfg.pass)

    config
  }

}
