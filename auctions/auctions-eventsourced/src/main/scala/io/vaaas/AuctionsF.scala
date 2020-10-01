package io.vaaas

import akka.actor.ActorSystem
import cats.Parallel
import cats.effect._
import cats.implicits._
import com.typesafe.config.{ Config, ConfigFactory }
import squants.market.MoneyContext

import scala.concurrent.ExecutionContext

class AuctionsF[F[_]: Timer: ContextShift: Parallel: LiftIO](appConfig: AuctionsConfig)(implicit
  F: ConcurrentEffect[F]
) {

  implicit val fxContext: MoneyContext = squants.market.defaultMoneyContext

  def run: Resource[F, EntityWirings[F]] = for {
    r <- resources
    s <- Resource.liftF(wirings(r))
  } yield s

  case class Resources(appConfig: AuctionsConfig, system: ActorSystem, postgresWirings: PostgresWirings[F])

  def actorSystem(appConfig: AuctionsConfig, config: Config): Resource[F, ActorSystem] = {
    val systemF = F.delay(ActorSystem(appConfig.cluster.systemName, config))

    Resource.make(systemF) { s =>
      implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)
      LiftIO[F].liftIO(IO.fromFuture(IO(s.terminate()))).void
    }
  }

  def resources: Resource[F, Resources] =
    for {
      config <- Resource.liftF(F.delay(ConfigFactory.load()))
      system <- actorSystem(appConfig, config)
      postgresWirings <- PostgresWirings[F](appConfig)
      _ <- Resource.make(F.unit)(_ => F.delay(println("Releasing application resources")))
    } yield Resources(appConfig, system, postgresWirings)

  def wirings(r: Resources): F[EntityWirings[F]] = {
    import r._

    val clock = Clock.create[F]

    for {
      entityWirings <- EntityWirings(system, clock, postgresWirings)

      processWirings = new ProcessWirings(
        system,
        clock,
        postgresWirings,
        entityWirings
      )

      _ <- processWirings.launchProcesses.void
    } yield entityWirings
  }

}
