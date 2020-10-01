package io.vaaas

import doobie.Transactor
import zio.blocking.Blocking
import zio.{Task, UIO, ZManaged}

case class DatabaseConfig(
                           driver: String,
                           url: String,
                           schema: String,
                           user: String,
                           pass: String
                         ) {
  val initDb: UIO[Unit] =
    Flyway.initDb(this).orDie

  val xa: ZManaged[Blocking, Nothing, Transactor[Task]] =
    Doobie.mkTransactor(this).orDie
}
