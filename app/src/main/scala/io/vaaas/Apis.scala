package io.vaaas

import io.vaaas.AuctionServices.AuctionServices
import io.vaaas.SaasApi.SaasApi
import io.vaaas.SaasServices.SaasServices
import io.vaaas.auction.{AuctionService, AuctionsApi}
import io.vaaas.auction.AuctionsApi.AuctionsApi
import io.vaaas.auction.view.AuctionViewService
import io.vaaas.tapir.ZEnvEndpoints
import io.vaaas.user.UserService
import io.vaaas.user.auth.AuthService
import zio.{Has, ZLayer}

object Apis {
  type Apis = Has[ZEnvEndpoints]
  //  type Apis = SaasApi with AuctionsApi

  def apply(): ZLayer[SaasServices with AuctionServices, Nothing, Apis] =
    ZLayer
      .fromServicesMany[UserService, AuthService, AuctionService /*, AuctionViewService*/ , Apis](
        (userService, authService, auctionService /*, auctionViewService*/) => {
          val endpoints = SaasApi(userService, authService).get :::
            AuctionsApi(auctionService /*, auctionViewService*/)(authService).get

          Has(endpoints)
        }


      )
}
