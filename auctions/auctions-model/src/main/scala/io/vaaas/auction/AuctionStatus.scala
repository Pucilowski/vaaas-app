package io.vaaas.auction

import enumeratum._

import scala.collection.immutable

sealed trait AuctionStatus extends EnumEntry

object AuctionStatus extends Enum[AuctionStatus] with CirceEnum[AuctionStatus] {
  case object Live extends AuctionStatus
  case object Ended extends AuctionStatus

  def values: immutable.IndexedSeq[AuctionStatus] = findValues
}
