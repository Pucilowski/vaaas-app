package io.vaaas.auction.model

import io.vaaas.auction.BidTarget
import squants.Money

object models {

  case class AuctionDef
  (
    reserve: Money,
    targets: List[BidTarget]
  )

  case class BidSplit[K](value: Map[K, Int]) extends AnyVal {
    def split: Seq[Money] = Seq.empty
  }

}
