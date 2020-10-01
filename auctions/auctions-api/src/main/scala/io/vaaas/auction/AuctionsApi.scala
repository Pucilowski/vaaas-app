package io.vaaas.auction

import cats.data.NonEmptyList
import cats.implicits.catsSyntaxOptionId
import io.vaaas.Globals
import io.vaaas.auction.command.{PlaceBid, StartAuction}
import io.vaaas.auction.view.AuctionViewService
import io.vaaas.tapir.{ZEnvEndpoint, ZEnvEndpoints}
import io.vaaas.user.auth.{ApiKeyId, AuthService, Authenticator, TapirAuthDsl}
import sttp.tapir.ztapir._
import zio.Has

object AuctionsApi {
  type AuctionsApi = Has[ZEnvEndpoints]

  def apply(
             auctionService: AuctionService,
//             auctionViewService: AuctionViewService,
           )(implicit authService: AuthService): AuctionsApi = {
    implicit val authenticator: Authenticator = token => authService.auth(ApiKeyId(token))

    Has(new Api(auctionService/*, auctionViewService*/).endpoints)
  }

  class Api(
             auctionService: AuctionService,
//             auctionViewService: AuctionViewService
           )(implicit auth: Authenticator) extends AuctionsDsl with TapirAuthDsl {

    val auctions = "auctions"
    val bids = "bids"

    private val startAuction: ZEnvEndpoint =
      authedEndpoint.post.in(auctions).in(StartAuction.body).serverLogic {
        fromServiceCall {
          case StartAuction(endsAt, reserve, targets) =>
            auctionService.start(None, endsAt, reserve, targets)
        }
      }

    private val startAuctionWithId: ZEnvEndpoint =
      authedEndpoint.put.in(auctions / auctionId).in(StartAuction.body).serverLogic {
        fromServiceCall {
          case (auctionId, StartAuction(endsAt, reserve, targets)) =>
            auctionService
              .start(auctionId.some, endsAt, reserve, targets)
        }
      }

    private val placeBid: ZEnvEndpoint =
      authedEndpoint.post.in(auctions / auctionId / bids).in(PlaceBid.body).serverLogic {
        fromServiceCall {
          case (auctionKey, PlaceBid(size, split)) =>
            val m = RawMoney(size.amount, size.currency).parse(Globals.moneyContext).getOrElse(???)
            auctionService
              .placeBid(auctionKey, m, split)
        }
      }

    private val withdrawBid: ZEnvEndpoint =
      authedEndpoint.delete.in(auctions / auctionId / bids).serverLogic {
        fromServiceCall { auctionKey =>
          auctionService.withdrawBid(auctionKey)
        }
      }

/*    private val getAuction: ZEnvEndpoint =
      baseEndpoint.get.in(auctions / auctionId).out(auctionViewBody).zServerLogic {
        id =>
          auctionViewService.findOne(id).bimap(
            toApi,
            identity
          )
      }*/

    val endpoints: ZEnvEndpoints = NonEmptyList
      .of(
        startAuction,
        startAuctionWithId,
        placeBid,
        withdrawBid,
//        getAuction
      )
      .map(_.tag(auctions))

  }

}
