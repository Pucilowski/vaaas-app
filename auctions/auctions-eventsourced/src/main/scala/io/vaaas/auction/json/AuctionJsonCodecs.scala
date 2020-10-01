package io.vaaas.auction.json

//import io.circe._
//import io.circe.generic.semiauto.{deriveCodec, deriveDecoder, deriveEncoder}
import io.vaaas.MoneyCodecs
//import io.vaaas.auction.eventsourced.state.Bid
//import io.vaaas.auction.{BidTarget, UserBid}
import io.vaaas.common.json.AnyValCoders

trait AuctionJsonCodecs extends AnyValCoders with MoneyCodecs {

//  implicit val bidTargetCodec: Codec[BidTarget] = deriveCodec

  /*implicit val bidCodec: Codec[Bid] = Codec.from(
    deriveDecoder[Bid.Raw].map(_.parseUnsafe),
    deriveEncoder[Bid]
  )*/

//  implicit val userBidEncoder: Encoder[UserBid] = deriveEncoder
//  implicit val userBidDecoder: Decoder[UserBid] = deriveDecoder


}
