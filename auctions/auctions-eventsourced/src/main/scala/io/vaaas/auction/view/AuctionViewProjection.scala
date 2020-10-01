package io.vaaas.auction.view

import aecor.data.Folded.syntax._
import aecor.data.{Enriched, EntityEvent, Folded}
import cats.Functor
import cats.implicits._
import com.softwaremill.quicklens._
import io.vaaas.auction.eventsourced.{AuctionEnded, AuctionEvent, AuctionStarted, BidPlaced, BidWithdrawn, EventMetadata}
import io.vaaas.auction.{AuctionKey, AuctionStatus, BidTarget}
import io.vaaas.common.view.Projection
import io.vaaas.common.view.Projection.Version

class AuctionViewProjection[F[_] : Functor](repo: AuctionViewRepository[F])
  extends Projection[F, EntityEvent[AuctionKey, Enriched[EventMetadata, AuctionEvent]], AuctionView] {

  def fetchVersionAndState(
                            event: EntityEvent[AuctionKey, Enriched[EventMetadata, AuctionEvent]]
                          ): F[(Version, Option[AuctionView])] =
    repo
      .get(event.entityKey)
      .map(v => v.fold(Projection.initialVersion)(v => Version(v.version)) -> v)

  def saveNewVersion(s: AuctionView, version: Version): F[Unit] =
    repo.set(s.copy(version = version.value))

  def applyEvent(
                  s: Option[AuctionView]
                )(event: EntityEvent[AuctionKey, Enriched[EventMetadata, AuctionEvent]]): Folded[Option[AuctionView]] = s match {
    case None =>
      event.payload.event match {
        case e: AuctionStarted =>

          val targets = e.targets.map { target =>

            val otherMinSum =
              e.targets.collect {
                case BidTarget(id, _, min, _) if id != target.id =>
                  min
              }.sum

            BidTargetView(
              id = target.id,
              name = target.name,
              min = target.min,
              suggested = target.suggested,
              max = 1 - otherMinSum
            )
          }

          Some(
            AuctionView(
              event.entityKey,
              e.userId,
              e.endsAt,
              e.reserve.amount,
              e.reserve.currency.code,
              targets,
              AuctionStatus.Live,
              Map.empty,
              Projection.initialVersion.value
            )
          ).next
        case _ => impossible
      }

    case Some(state) =>
      event.payload.event match {
        case BidPlaced(userId, bid) =>
          val newBid = userId -> UserBidView(bid.size, bid.split, None)

          state
            .modify(_.bids)
            .using(_ + newBid)
            .some.next
        case BidWithdrawn(userId) =>
          state
            .modify(_.bids)
            .using(_ - userId)
            .some.next
        case AuctionEnded =>
          state
            .modify(_.status)
            .setTo(AuctionStatus.Ended)
            .some.next
        case _ => impossible
      }
  }
}
