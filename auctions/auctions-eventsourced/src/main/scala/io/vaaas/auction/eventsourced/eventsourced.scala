package io.vaaas.auction

import aecor.runtime.Eventsourced.Entities

package object eventsourced {
  type Auctions[F[_]] = Entities.Rejectable[AuctionKey, Auction, F, AuctionCommandRejection]
}
