package io.vaaas.auction.eventsourced

import java.time.Instant

import boopickle.Default.{compositePickler, _}
import scodec.Codec
import squants.Money
import squants.market.Currency

object AuctionWireCodecs {

  implicit val instantPickler: boopickle.Pickler[Instant] =
    boopickle.DefaultBasic.longPickler.xmap(Instant.ofEpochMilli)(_.toEpochMilli)

  implicit val rejectionPickler: boopickle.Pickler[AuctionCommandRejection] =
    compositePickler[AuctionCommandRejection]
      .addConcreteType[ValidationError]
      .addConcreteType[AuctionExists.type]
      .addConcreteType[AuctionNotFound.type]
      .addConcreteType[AuctionHasEnded.type]
      .addConcreteType[TooEarlyToExpire.type]
      .addConcreteType[BidBelowReserve.type]
      .addConcreteType[BidRejected]

  implicit val rejectionCodec: Codec[AuctionCommandRejection] =
    aecor.macros.boopickle.BoopickleCodec.codec[AuctionCommandRejection]

  implicit val moneyPickler: boopickle.Pickler[Money] =
    boopickle.DefaultBasic.stringPickler.xmap(str => {
      val Array(amount, currency) = str.split(" ")
      val curr = Currency.apply(currency)(squants.market.defaultMoneyContext)

      curr.get(BigDecimal(amount))
    })(m => f"${m.amount} ${m.currency}")

}
