package io.vaaas.auction.view

import aecor.data._
import aecor.distributedprocessing.DistributedProcessing
import cats.effect.ConcurrentEffect
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import io.vaaas.auction.AuctionKey
import io.vaaas.auction.eventsourced.{AuctionEvent, EventMetadata}
import io.vaaas.auction.view.AuctionViewProjectionWiring.EventSource
import io.vaaas.common.streaming.Fs2Process
import io.vaaas.common.view.ProjectionFlow

class AuctionViewProjectionWiring[F[_]](
                                         repo: AuctionViewRepository[F],
                                         eventSource: (EventTag, ConsumerId) => EventSource[F],
                                         tagging: Tagging[AuctionKey]
                                       )(implicit F: ConcurrentEffect[F]) {

  val consumerId = ConsumerId("AuctionViewProjection")

  val sink =
    ProjectionFlow(Slf4jLogger.unsafeCreate[F], new AuctionViewProjection[F](repo))

  def tagProcess(tag: EventTag): fs2.Stream[F, Unit] =
    eventSource(tag, consumerId).through(sink)

  def processes: List[DistributedProcessing.Process[F]] =
    tagging.tags.map(tag => Fs2Process(tagProcess(tag)))
}

object AuctionViewProjectionWiring {
  type EventSource[F[_]] =
    fs2.Stream[F, Committable[F, EntityEvent[AuctionKey, Enriched[EventMetadata, AuctionEvent]]]]

}
