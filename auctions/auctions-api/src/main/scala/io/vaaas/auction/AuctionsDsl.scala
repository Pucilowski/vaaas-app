package io.vaaas.auction

import io.vaaas.auction.view.{ AuctionView, UserBidView }
import io.vaaas.tapir.TapirDsl
import io.vaaas.user.UserId
import sttp.tapir.{ Codec, CodecFormat, EndpointIO, EndpointInput, _ }

trait AuctionsDsl extends TapirDsl {

  implicit val auctionKeyTC: Codec[String, AuctionKey, CodecFormat.TextPlain] =
    Codec.string.map(AuctionKey(_))(_.value)

  implicit val userIdTC: Codec[String, UserId, CodecFormat.TextPlain] =
    Codec.long.map(UserId(_))(_.value)

  val auctionId: EndpointInput.PathCapture[AuctionKey] = path[AuctionKey]("auctionId")

  val userId: EndpointInput.PathCapture[UserId] = path[UserId]("userId")

  implicit val auctionIdSchema: Schema[AuctionKey] = schemaFor[AuctionKey]
  implicit val auctionStatusSchema: Schema[AuctionStatus] = schemaFor[AuctionStatus]
  implicit val bidsSchema: Schema[Map[UserId, UserBidView]] = schemaFor[Map[UserId, UserBidView]]

  val auctionViewBody: EndpointIO.Body[String, AuctionView] = jsonBody[AuctionView]

}
