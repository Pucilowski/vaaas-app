package io.vaaas.auction

import cats.data.ValidatedNec
import cats.implicits._
import io.circe.Codec
import squants.market.{ Money, MoneyContext }

import scala.util.{ Failure, Success, Try }

case class RawMoney(amount: BigDecimal, currency: String) {
  def toMoney(implicit fxContext: MoneyContext): Try[Money] =
    Money(amount, currency)(fxContext)

  def parseUnsafe(implicit fxContext: MoneyContext): Money =
    toMoney.get

  def parse(implicit fxContext: MoneyContext): ValidatedNec[String, Money] =
    toMoney match {
      case Success(money)     => money.validNec
      case Failure(exception) => exception.getMessage.invalidNec
    }
}

object RawMoney {
  implicit val codec: Codec[RawMoney] = io.circe.generic.semiauto.deriveCodec
}
