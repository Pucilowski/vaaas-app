package io.vaaas

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import io.vaaas.auction.{AuctionService, LiveAuctionService}
import io.vaaas.auction.view.AuctionViewService
import zio.{Has, IO, Task, ULayer, ZIO, ZLayer, ZManaged}
import zio.blocking.Blocking
import zio.clock.Clock
import zio.interop.catz._
import zio.interop.catz.implicits._
import io.vaaas.Globals.moneyContext

object AuctionServices {
  type AuctionServices = Has[AuctionService] //with Has[AuctionViewService]

  def live(cfg: AuctionsConfig): ZLayer[Any, Nothing, Has[AuctionService]] = {

    ZIO.runtime[Any].toManaged_.flatMap { implicit runtime: zio.Runtime[Any] =>
      val entityWirings = new AuctionsF[Task](cfg)

      entityWirings.run.toManaged.orDie.map(w => LiveAuctionService.apply(w.auctions))
    }.toLayer
  }

  /*def actorSystem(cluster: ActorSystemName): ZManaged[Any, Nothing, ActorSystem] = for {
    config <- ZIO.succeed(ConfigFactory.load()).toManaged_
    system <- IO.succeed {
      ActorSystem(cluster.systemName, config)
    }.toManaged { s =>
      ZIO.fromFuture { _ => s.terminate() }.orDie
    }
  } yield system*/
}
