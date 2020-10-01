package io.vaaas.auction.eventsourced.state

import java.time.Instant

case class UserBid
(
  bid: Bid,
  paid: Option[Instant]
) {
  def settled(at: Instant): UserBid = copy(
    paid = Some(at)
  )
}
