package io.vaaas.http

import cats.effect.ExitCode
import io.vaaas.Apis.Apis
import io.vaaas.SaasApi.SaasApi
import io.vaaas.auction.AuctionsApi.AuctionsApi
import org.http4s.HttpApp
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import zio.interop.catz._
import zio.interop.catz.implicits._
import zio.{Task, URIO, ZEnv, ZIO}


object Server {
  def apply(config: HttpConfig): ZIO[ZEnv with Apis, Throwable, Unit] = for {
    app <- app

    r <- ZIO.runtime[Any].flatMap { implicit rts =>
      BlazeServerBuilder[Task]
        .bindHttp(config.port, config.host)
        .withHttpApp(app)
        .serve
        .compile[Task, Task, ExitCode]
        .drain
    }
  } yield r

  private def app: URIO[ZEnv with Apis, HttpApp[Task]] = for {
    saasApi <- ZIO.access[SaasApi](_.get)
    auctionApi <- ZIO.access[AuctionsApi](_.get)

    endpoints = saasApi ::: auctionApi

    routes <- Routes(endpoints)
  } yield routes.orNotFound
}
