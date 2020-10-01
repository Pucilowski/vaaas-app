package io.vaaas.auction.eventsourced.state

import cats.data.ValidatedNec
import cats.implicits._
import io.vaaas.auction.BidTarget

case class BidTargetList private(value: List[BidTarget]) extends AnyVal

object BidTargetList {
  def apply(value: List[BidTarget]): ValidatedNec[String, BidTargetList] =
    if (value.map(_.min).sum >= 100)
      new BidTargetList(value).validNec
    else
      "".invalidNec
}
