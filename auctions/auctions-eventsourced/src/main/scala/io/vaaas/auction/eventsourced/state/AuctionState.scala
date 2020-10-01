package io.vaaas.auction.eventsourced.state

import java.time.Instant

import aecor.data.Folded
import aecor.data.Folded.syntax._
import com.softwaremill.quicklens._
import io.vaaas.auction.eventsourced._
import io.vaaas.auction.AuctionStatus
import io.vaaas.auction.AuctionStatus.Ended
import io.vaaas.user.UserId
import squants.Money

case class AuctionState(
  ownerId: UserId,
  endsAt: Instant,
  reserve: Money,
  status: AuctionStatus,
  bids: Map[UserId, UserBid]
) {

  def hasEnded: Boolean = status == Ended

  def handleEvent(e: AuctionEvent): Folded[AuctionState] = e match {
    case _: AuctionStarted => impossible
    case BidPlaced(userId, bid) =>
      val newBid = userId -> UserBid(bid, None)

      this.modify(_.bids).using(_ + newBid).next

    case BidWithdrawn(userId) =>
      this.modify(_.bids).using(_ - userId).next

    case BidSettled(userId, at) =>
      this.modify(_.bids.at(userId)).using(_.settled(at)).next

    case AuctionEnded =>
      this.modify(_.status).setTo(AuctionStatus.Ended).next
  }

}

object AuctionState {
  def init(e: AuctionEvent): Folded[AuctionState] = e match {
    case e: AuctionStarted =>
      AuctionState(
        e.userId,
        e.endsAt,
        e.reserve,
        AuctionStatus.Live,
        Map.empty
      ).next
    case _ => impossible
  }

  /*val fold: Fold[Folded, Option[AuctionState], AuctionEvent] =
    Fold.optional(init)(_.handleEvent(_))*/
}
