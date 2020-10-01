import java.time.Instant

import enumeratum.{ CirceEnum, Enum, EnumEntry }
import squants.Money
import vickrey.AuctionStatus.{ Ended, Live, findValues }

import scala.collection.immutable

package object vickrey {
  case class UserId(value: String)

  sealed trait AuctionStatus extends EnumEntry

  object AuctionStatus extends Enum[AuctionStatus] with CirceEnum[AuctionStatus] {
    case object Live extends AuctionStatus
    case object Ended extends AuctionStatus

    def values: immutable.IndexedSeq[AuctionStatus] = findValues
  }

  case class AuctionState(
    ownerId: UserId,
    endsAt: Instant,
    reserve: Money,
    status: AuctionStatus,
    bids: Map[UserId, Money]
  ) {
    def isLive: Boolean = status == Live
    def hasEnded: Boolean = status == Ended
  }

  type State = Option[AuctionState]

  sealed trait Command
  object Command {
    case class StartAuction(userId: UserId, reserve: Money, endsAt: Instant) extends Command
    case class PlaceBid(userId: UserId, bid: Money) extends Command
    case class WithdrawBid(userId: UserId) extends Command
    case class EndAuction(now: Instant) extends Command
  }

  sealed trait Event
  object Event {
    case class AuctionStarted(userId: UserId, reserve: Money, endsAt: Instant) extends Event
    case class BidPlaced(userId: UserId, bid: Money) extends Event
    case class BidWithdrawn(userId: UserId) extends Event
    case class AuctionEnded(now: Instant) extends Event
    case class BidSettled(userId: UserId, at: Instant) extends Event
  }

  sealed trait Rejection
  object Rejection {
    case object AuctionExists extends Rejection
    case object AuctionNotFound extends Rejection
    case object AuctionHasEnded extends Rejection
    case object TooEarlyToExpire extends Rejection

    case object BidBelowReserve extends Rejection

    case class Other(reason: String) extends Rejection
  }

  trait CommandHandler[C, S, E, R] {
    def handle(command: C, state: S): Either[R, List[E]]
  }
}
