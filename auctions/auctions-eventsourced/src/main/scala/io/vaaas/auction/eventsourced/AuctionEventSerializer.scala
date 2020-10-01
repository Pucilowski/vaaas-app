package io.vaaas.auction.eventsourced

import java.time.Instant

import aecor.data.Enriched
import aecor.journal.postgres.PostgresEventJournal
import aecor.journal.postgres.PostgresEventJournal.Serializer.TypeHint
import io.circe.syntax._
import io.circe.{Codec, Decoder, Encoder}
import io.vaaas.auction.eventsourced
import io.vaaas.auction.json.AuctionJsonCodecs
import squants.market.{Money, MoneyContext}

import io.circe.generic.auto._

class AuctionEventJsonCodecs(implicit val fxContext: MoneyContext)
  extends AuctionJsonCodecs {

  import io.circe.generic.extras.Configuration
  import io.circe.generic.extras.semiauto._

  implicit val customConfig: Configuration = Configuration.default
    .withDefaults.withDiscriminator("type")

  //  import AnyValCoders._


  //  {
  //    implicit val moneyCodec: Encoder[Money] = MoneyCodec(fxContext)
  //    deriveConfiguredCodec[Bid]
  //  }

  //  implicit val instantCodec: Codec[Instant] = implicitly
  //  implicit val encoder2: Encoder[BidPlaced] = deriveConfiguredEncoder
  //  implicit val encoder3: Encoder[BidSettled] = deriveConfiguredEncoder


  type T = (
    String,
      String,
      Money,
      //      List[BidTarget],
      Instant
    )

  implicit val tCodec: Codec[T] = io.circe.generic.semiauto.deriveCodec

  implicit val t0Codec: Codec[AuctionStarted] = deriveConfiguredCodec
  //  implicit val t1Codec: Codec[BidPlaced] = deriveConfiguredCodec
  implicit val t2Codec: Codec[BidWithdrawn] = deriveConfiguredCodec
  implicit val t3Codec: Codec[AuctionEnded.type] = deriveConfiguredCodec
  implicit val t4Codec: Codec[BidSettled] = deriveConfiguredCodec

  //  implicit val tCodec: Codec[T] = deriveConfiguredCodec[T]

  implicit val encoder: Encoder[AuctionEvent] = deriveConfiguredEncoder
  implicit val decoder: Decoder[AuctionEvent] = deriveConfiguredDecoder

  implicit val metaEncoder: Encoder[EventMetadata] = deriveConfiguredEncoder
  implicit val metaDecoder: Decoder[EventMetadata] = deriveConfiguredDecoder

}

class AuctionEventSerializer(implicit fxContext: MoneyContext)
  extends PostgresEventJournal.Serializer[Enriched[EventMetadata, AuctionEvent]] {

  val codecs = new AuctionEventJsonCodecs

  import codecs._

  def serialize(a: Enriched[EventMetadata, AuctionEvent]): (TypeHint, Array[Byte]) = a match {
    case Enriched(metadata, event) =>
      //      val eventJson = event match {
      //        case e: AuctionStarted =>
      //          e.asJson
      //        case e: BidPlaced => e.asJson
      //        case e: BidWithdrawn => e.asJson
      //        case e@AuctionEnded => e.asJson
      //        case e: BidSettled => e.asJson
      //      }
      //      val eventJson = event.asJson
      //
      val data = Map(
        "event" -> event.asJson,
        "metadata" -> metadata.asJson
      )
      //      "" -> data.toString.getBytes
      event.getClass.getSimpleName -> data.asJson.toString.getBytes
  }

  def deserialize(typeHint: TypeHint,
                  bytes: Array[Byte]): Either[Throwable, Enriched[EventMetadata, AuctionEvent]] = {
    import io.circe.parser._

    for {
      json <- parse(new String(bytes))
      c = json.hcursor
      event <- c.downField("event").as[AuctionEvent]
    } yield Enriched(eventsourced.EventMetadata(Instant.now()), event)
  }
}
