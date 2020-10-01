package io.vaaas

/*import akka.actor.ActorSystem
import cats.Parallel
import cats.effect._
import cats.implicits._
import cats.syntax._
import com.typesafe.config.{Config, ConfigFactory}
import io.vaaas.config.AppConfig
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import pureconfig.generic.auto._
import pureconfig.{loadConfigOrThrow, _}
import squants.market.MoneyContext*/

//import scala.concurrent.ExecutionContext

/*class AppF[F[_] : Timer : ContextShift : Parallel : LiftIO](implicit F: ConcurrentEffect[F]) {

  implicit val fxContext: MoneyContext = squants.market.defaultMoneyContext

  case class Resources(appConfig: AppConfig,
                       system: ActorSystem,
                       postgresWirings: PostgresWirings[F])

  def actorSystem(appConfig: AppConfig, config: Config): Resource[F, ActorSystem] = {
    val systemF = F.delay(ActorSystem(appConfig.cluster.systemName, config))

    Resource.make(systemF) { s =>
      implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)
      LiftIO[F].liftIO(IO.fromFuture(IO(s.terminate()))).void
    }
  }

  def resources: Resource[F, Resources] =
    for {
      config <- F.delay(ConfigFactory.load()).resource
      appConfig <- F.delay(pureconfig.loadConfigOrThrow[AppConfig](config)).resource
      system <- actorSystem(appConfig, config)
      postgresWirings <- PostgresWirings[F](appConfig)
      _ <- Resource.make(F.unit)(_ => F.delay(println("Releasing application resources")))
    } yield Resources(appConfig, system, postgresWirings)

  def wirings(r: Resources): F[EndpointWirings[F]] = {
    import r._

    val clock = Clock.create[F]

    for {
      entityWirings <- EntityWirings(system, clock, postgresWirings)
      //      serviceWirings <- ServiceWirings(clock)
      //      kafkaWirings = KafkaWirings[F]
      processWirings = new ProcessWirings(
        system,
        clock,
        postgresWirings,
        //        kafkaWirings,
        //        serviceWirings,
        entityWirings
      )
      endpointWirings = new EndpointWirings(postgresWirings, entityWirings)
      _ <- processWirings.launchProcesses.void
      //_ <- endpointWirings.launchHttpService
    } yield endpointWirings
  }

  //  def run: Resource[F, Unit] = resources.flatMap(r => launch(r).resource)
  def run: Resource[F, Unit] = for {
    r <- resources
    s: EndpointWirings[F] <- wirings(r).resource
    httpApp = Router(
//      "/auctions" -> AuctionsRoutes.endpoints[F](s.auctionService),
//      "/forex" -> ForexEndpoint.endpoints[F](s.forexService)
    ).orNotFound
    _ <- BlazeServerBuilder[F]
      .bindHttp(r.appConfig.httpServer.port, r.appConfig.httpServer.interface)
      .withHttpApp(httpApp)
      .resource
  } yield ()
}*/
