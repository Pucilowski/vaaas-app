package io.vaaas

/*import aecor.data._
import aecor.distributedprocessing.DistributedProcessing
import aecor.journal.postgres.{Offset, PostgresEventJournalQueries}
import akka.actor.ActorSystem
import cats.Parallel
import cats.effect.{Clock, ConcurrentEffect, Timer}
import cats.implicits._
import io.vaaas.auction.AuctionKey
import io.vaaas.auction.eventsourced.{AuctionEvent, EventsourcedAuction}
import io.vaaas.auction.eventsourced.EventMetadata
import io.vaaas.auction.process.{AuctionExpirationProcess, AuctionExpirationProcessWiring}
import io.vaaas.auction.view.AuctionViewProjectionWiring

import scala.concurrent.duration._

final class ProcessWirings[F[_] : Timer : ConcurrentEffect : Parallel](system: ActorSystem,
                                                                       clock: Clock[F],
                                                                       postgresWirings: PostgresWirings[F],
                                                                       //                                                               kafkaWirings: KafkaWirings[F],
                                                                       //                                                               serviceWirings: ServiceWirings[F],
                                                                       entityWirings: EntityWirings[F]) {

  //  import serviceWirings._

  import postgresWirings._
  //  import kafkaWirings._
  import entityWirings._

  val distributedProcessing = DistributedProcessing(system)

  val bookingQueries: PostgresEventJournalQueries.WithOffsetStore[F, F, AuctionKey, Enriched[EventMetadata, AuctionEvent]] =
    auctionsJournal.queries(journals.auction.pollingInterval).withOffsetStore(offsetStore)

  def bookingEvents(
                     eventTag: EventTag,
                     consumerId: ConsumerId
                   ): fs2.Stream[F, Committable[F,
    (Offset,
      EntityEvent[AuctionKey, Enriched[EventMetadata, AuctionEvent]])]] =
    fs2.Stream.force(bookingQueries.eventsByTag(eventTag, consumerId))

  val auctionViewProjection = new AuctionViewProjectionWiring(
    auctionViewRepo,
    bookingEvents(_, _).map(_.map(_._2)),
    EventsourcedAuction.tagging
  )

  //  val bookingConfirmationProcess =
  //    new AuctionConfirmationProcess(
  //      bookings,
  //      confirmationService,
  //      Slf4jLogger.unsafeFromName("AuctionConfirmationProcess")
  //    )

  //  val bookingConfirmationProcessWiring =
  //    new AuctionConfirmationProcessWiring(
  //      bookingEvents(_, _).map(_.map(_._2.map(_.event))),
  //      EventsourcedAuction.tagging,
  //      bookingConfirmationProcess
  //    )

  val auctionExpirationProcess = new AuctionExpirationProcess(auctions, auctionViewRepo)

  val auctionExpirationProcessWiring =
    new AuctionExpirationProcessWiring(clock, frequency = 30.seconds, auctionExpirationProcess)

  //  val bookingPaymentProcess =
  //    new AuctionPaymentProcess(bookings, Slf4jLogger.unsafeFromName("AuctionPaymentProcess"))

  //  val bookingPaymentProcessWiring =
  //    new AuctionPaymentProcessWiring(paymentReceivedEventStream, bookingPaymentProcess)

  // Launcher

  val launchProcesses: F[List[DistributedProcessing.KillSwitch[F]]] =
    List(
      "AuctionViewProjectionProcessing" -> auctionViewProjection.processes,
      "AuctionExpirationProcessing" -> auctionExpirationProcessWiring.processes,
    ).parTraverse {
      case (name, processes) => distributedProcessing.start(name, processes)
    }

}*/
