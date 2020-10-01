package vickrey

import cats.implicits.catsSyntaxEitherId
import squants.Money
import vickrey.AuctionStatus.Live
import vickrey.Command.{ EndAuction, PlaceBid, StartAuction }
import vickrey.Event.{ AuctionEnded, AuctionStarted, BidPlaced }
import vickrey.Rejection.{ AuctionExists, AuctionHasEnded, BidBelowReserve, Other, TooEarlyToExpire }

object behaviour {

  val commandHandler: CommandHandler[Command, State, Event, Rejection] = {
    case (StartAuction(userId, reserve, endsAt), None) =>
      append {
        AuctionStarted(userId, reserve, endsAt)
      }
    case (_: StartAuction, Some(_)) =>
      reject(AuctionExists)

    case (PlaceBid(userId, bid: Money), Some(auction)) =>
      if (auction.status == Live && bid > auction.reserve) {
        append {
          BidPlaced(userId, bid)
        }
      } else reject(BidBelowReserve)

    case (EndAuction(now), Some(auction)) if auction.isLive =>
      if (now.isAfter(auction.endsAt)) {
        append {
          AuctionEnded(now)
        }
      } else reject(TooEarlyToExpire)
    case (EndAuction(_), Some(_)) =>
      reject(AuctionHasEnded)

    case (command, _) =>
      reject(Other(s"Cannot apply $command against current state"))
  }

  private def append(e: Event, es: Event*): Either[Rejection, List[Event]] = {
    (e +: es).toList.asRight
  }

  private def reject(reason: Rejection): Either[Rejection, List[Event]] = {
    reason.asLeft
  }

}
