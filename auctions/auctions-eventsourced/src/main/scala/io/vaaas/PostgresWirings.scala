package io.vaaas


import aecor.data.{Enriched, TagConsumer}
import aecor.journal.postgres.{Offset, PostgresEventJournal, PostgresOffsetStore}
import aecor.runtime.KeyValueStore
import cats.Parallel
import cats.effect.{Async, ContextShift, Resource, Timer}
import cats.implicits._
import cats.syntax._
import doobie.implicits._
import doobie.util.transactor.Transactor
import io.vaaas.auction.AuctionKey
import io.vaaas.auction.eventsourced.EventsourcedAuction._
import io.vaaas.auction.eventsourced.{AuctionEvent, AuctionEventSerializer, EventMetadata, EventsourcedAuction}
import io.vaaas.auction.view.PgAuctionViewRepository
import squants.market.MoneyContext


final class PostgresWirings[F[_] : Async : Timer : Parallel] private(val transactor: Transactor[F],
                                                                     val journals: PostgresJournals)
                                                                    (implicit fxContext: MoneyContext) {

  val offsetStoreCIO = PostgresOffsetStore("consumer_offset")
  val offsetStore: KeyValueStore[F, TagConsumer, Offset] = offsetStoreCIO.mapK(transactor.trans)

  val auctionsJournal =
    new PostgresEventJournal[F, AuctionKey, Enriched[EventMetadata, AuctionEvent]](
      transactor,
      journals.auction.tableName,
      EventsourcedAuction.tagging,
      new AuctionEventSerializer()
    )

  // views

  val auctionViewRepo = new PgAuctionViewRepository[F](transactor)
}

object PostgresWirings {
  def apply[F[_] : Async : Timer : Parallel : ContextShift](
                                                             settings: AuctionsConfig
                                                           )(implicit fxContext: MoneyContext): Resource[F, PostgresWirings[F]] =
    for {
      transactor <- PostgresTransactor.transactor[F](settings.postgres)
      wirings = new PostgresWirings(transactor, settings.postgresJournals)
      _ <- Resource.liftF(
        List(
          wirings.offsetStoreCIO.createTable.transact(transactor),
          wirings.auctionViewRepo.createTable,
          wirings.auctionsJournal.createTable,
        ).parSequence
      )
    } yield wirings
}
