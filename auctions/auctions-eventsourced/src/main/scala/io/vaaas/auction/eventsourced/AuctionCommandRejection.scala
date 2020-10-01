package io.vaaas.auction.eventsourced

//import cats.data.NonEmptyChain

sealed trait AuctionCommandRejection extends Throwable

case class ValidationError(nec: List[String]) extends AuctionCommandRejection
case object AuctionExists extends AuctionCommandRejection
case object AuctionNotFound extends AuctionCommandRejection
case object AuctionHasEnded extends AuctionCommandRejection
case object TooEarlyToExpire extends AuctionCommandRejection

case object BidBelowReserve extends AuctionCommandRejection

case class BidRejected(reason: String) extends AuctionCommandRejection
