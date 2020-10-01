package io.vaaas

import io.vaaas.auction.LiveAuctionService
import zio.Task
import zio.interop.catz._

import scala.concurrent.duration.{Duration => _}

final class EndpointWirings
(
  postgresWirings: PostgresWirings[Task],
  entityWirings: EntityWirings[Task]
) {

  import entityWirings._

  val auctionService = new LiveAuctionService(auctions)

}
