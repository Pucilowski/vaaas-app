package io.vaaas.auction

import tsec.common.SecureRandomId

case class AuctionKey(value: String) extends AnyVal

object AuctionKey {
  def apply(): AuctionKey = AuctionKey(SecureRandomId.Interactive.generate)
}
