package io.vaaas

import squants.market.MoneyContext

object Globals {
  implicit var moneyContext: MoneyContext = squants.market.defaultMoneyContext
}
