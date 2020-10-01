package io.vaaas

import java.time.Instant
import java.util.concurrent.TimeUnit

import aecor.data.{EitherK, Enriched, EventsourcedBehavior}
import aecor.runtime.{EventJournal, Eventsourced}
import aecor.runtime.akkageneric.{GenericAkkaRuntime, GenericAkkaRuntimeSettings}
import akka.actor.ActorSystem
import cats.effect._
import cats.implicits._
import cats.syntax._
import doobie.ConnectionIO
import io.vaaas.auction.AuctionKey
import io.vaaas.auction.eventsourced._
import io.vaaas.auction.eventsourced.EventsourcedAuction._
import io.vaaas.common.effect.TimedOutBehaviour
import squants.market.MoneyContext
import io.vaaas.auction.eventsourced.AuctionWireCodecs._

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

    val j = postgresWirings.auctionsJournal

    val createBehavior: AuctionKey => F[EitherK[Auction, AuctionCommandRejection, F]] = Eventsourced.apply(
      entityBehavior = auctionsBehavior,
      journal = j
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

}
