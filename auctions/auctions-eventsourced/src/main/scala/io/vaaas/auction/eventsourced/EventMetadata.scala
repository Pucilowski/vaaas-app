package io.vaaas.auction.eventsourced

import java.time.Instant

case class EventMetadata(timestamp: Instant) extends AnyVal
