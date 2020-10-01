package io.vaaas

import io.vaaas.tapir.ZEnvEndpoints
import io.vaaas.user.auth.{ApiKeyId, AuthApi, AuthService, Authenticator}
import io.vaaas.user.{UserApi, UserService}
import zio.Has

object SaasApi {
  type SaasApi = Has[ZEnvEndpoints]

  def apply(
             userService: UserService,
             authService: AuthService
           ): SaasApi = {
    implicit val authenticator: Authenticator = token => authService.auth(ApiKeyId(token))

    val endpoints = UserApi(userService) ::: AuthApi(authService)

    Has(endpoints)
  }
}
