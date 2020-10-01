package io.vaaas.auction.eventsourced.state

import cats.data.ValidatedNec
import cats.implicits._
import io.vaaas.Globals._
import io.vaaas.auction.RawMoney
import squants.Money

case class Bid private(
                        size: Money,
                        split: Seq[Int]
                      ) {
  //  def amounts: Seq[Money] = {
  //    val amounts0 = split.dropRight(1).map { s => amount * (s / 100D) }
  //    val last = amount - amounts0.reduce(_ + _)
  //
  //    amounts0 :+ last
  //  }
}

object Bid {

  def parseUnsafe(raw: Bid.Raw): Bid = Bid(raw.size.parseUnsafe, raw.split)

  private def require(fn: => Boolean, error: String): ValidatedNec[String, Unit] =
    if (fn) ().validNec
    else error.invalidNec

  case class Raw(size: RawMoney, split: Seq[Int]) {

    def parseUnsafe: Bid =
      Bid(size.parseUnsafe, split)

    def parse: ValidatedNec[String, Bid] = {
      (size.parse,
        require(size.amount > 0, "amount must be positive"),
        require(split.sum == 100, "amount must sum to 100"))
        .mapN((amount, _, _) => Bid(amount, split))
    }

  }

}



