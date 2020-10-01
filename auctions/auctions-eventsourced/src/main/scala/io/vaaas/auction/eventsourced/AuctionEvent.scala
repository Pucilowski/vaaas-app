package io.vaaas.auction.eventsourced

import java.time.Instant

import io.vaaas.auction.BidTarget
import io.vaaas.auction.eventsourced.state.Bid
import io.vaaas.user.UserId
import squants.Money

sealed trait AuctionEvent extends Product with Serializable

case class AuctionStarted(userId: UserId, reserve: Money, targets: List[BidTarget], endsAt: Instant) extends AuctionEvent

case class BidPlaced(userId: UserId, bid: Bid) extends AuctionEvent

case class BidWithdrawn(userId: UserId) extends AuctionEvent

case object AuctionEnded extends AuctionEvent

case class BidSettled(userId: UserId, at: Instant) extends AuctionEvent
