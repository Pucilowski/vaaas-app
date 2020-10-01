package io.vaaas.auction.view

import io.vaaas.auction.AuctionKey
import zio.Task

trait AuctionViewService {
  def findOne(key: AuctionKey): Task[AuctionView]

  def findAll(): fs2.Stream[Task, AuctionView]
}
