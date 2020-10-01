package io.vaaas

/*import java.time.Instant
import java.util.concurrent.TimeUnit

import aecor.data.EitherK
import aecor.runtime.Eventsourced
import aecor.runtime.akkageneric.{GenericAkkaRuntime, GenericAkkaRuntimeSettings}
import akka.actor.ActorSystem
import cats.effect._
import cats.implicits._
import io.vaaas.auction.AuctionKey
import io.vaaas.auction.eventsourced._
import io.vaaas.common.effect.TimedOutBehaviour
import squants.market.MoneyContext

import scala.concurrent.duration._

final class EntityWirings[F[_]](val auctions: Auctions[F])

object EntityWirings {
  def apply[F[_] : ConcurrentEffect : Timer](
                                              system: ActorSystem,
                                              clock: Clock[F],
                                              postgresWirings: PostgresWirings[F]
                                            )(implicit fxContext: MoneyContext): F[EntityWirings[F]] = {
    val genericAkkaRuntime = GenericAkkaRuntime(system)

    val generateTimestamp: F[EventMetadata] =
      clock.realTime(TimeUnit.MILLISECONDS).map(Instant.ofEpochMilli).map(EventMetadata.apply)

    val auctionsBehavior =
      TimedOutBehaviour(
        EventsourcedAuction.behavior[F](clock).enrich[EventMetadata](generateTimestamp)
      )(2.seconds)

    val createBehavior: AuctionKey => F[EitherK[Auction, AuctionCommandRejection, F]] =
      Eventsourced(
        auctionsBehavior,
        postgresWirings.auctionsJournal
      )

    val auctions: F[Auctions[F]] = {
      genericAkkaRuntime
        .runBehavior(
          typeName = EventsourcedAuction.entityName,
          createBehavior = createBehavior,
          settings = GenericAkkaRuntimeSettings.default(system)
        )
        .map(Eventsourced.Entities.fromEitherK(_))
    }

    auctions.map(new EntityWirings(_))
  }

}*/
