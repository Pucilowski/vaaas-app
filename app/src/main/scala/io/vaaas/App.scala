package io.vaaas

import io.vaaas.http.Server
import zio.console.putStrLn
import zio.interop.catz.CatsApp
import zio.{ ExitCode, URIO, ZEnv, ZIO }

object App extends CatsApp {

  Globals.moneyContext = squants.market.defaultMoneyContext

  override def run(args: List[String]): URIO[ZEnv, ExitCode] = {
    app.tapError(err => putStrLn(s"Execution failed with: $err")).exitCode
  }

  def app: ZIO[ZEnv, Throwable, Unit] = {
    val cfg = AppConfig.load

    val saas = SaasServices.live(cfg.saas)
    val auctions = AuctionServices.live(cfg.auctions)
    val services = saas ++ auctions

    val apis = services >>> Apis()

    Server(cfg.http).provideSomeLayer[ZEnv](apis)
  }

}
