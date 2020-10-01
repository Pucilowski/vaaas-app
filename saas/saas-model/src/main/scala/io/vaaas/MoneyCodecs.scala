package io.vaaas

import io.circe.syntax._
import io.circe._
import io.vaaas.Globals._
import squants.Money
import squants.market.Currency

trait MoneyCodecs {

  implicit val moneyCodec: Codec[Money] = new Codec[Money] {
    def apply(a: Money): Json = JsonObject(
      "amount" -> a.amount.asJson,
      "currency" -> a.currency.code.asJson
    ).asJson

    def apply(c: HCursor): Either[DecodingFailure, Money] = for {
      amount <- c.downField("amount").as[BigDecimal]
      currencyCode <- c.downField("currency").as[String]
      currency <- Currency(currencyCode).toEither.left.map(_ => DecodingFailure("", List.empty))
    } yield currency(amount)
  }

}

object MoneyCodecs extends MoneyCodecs
