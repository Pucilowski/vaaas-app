package io.vaaas.user

import cats.data.NonEmptyList
import io.vaaas.tapir.{ TapirDsl, ZEnvEndpoint, ZEnvEndpoints }
import io.vaaas.user.auth.{ Authenticator, TapirAuthDsl }
import io.vaaas.user.view.UserProfileView

class UserApi(service: UserService)(implicit auth: Authenticator) extends TapirDsl with TapirAuthDsl {

  val user = "user"

  private val getProfile: ZEnvEndpoint =
    authedEndpoint.description("Get user profile").get.in(user).out(UserProfileView.body).serverLogic {
      fromServiceCall { _ =>
        service.userProfile().map(UserProfileView(_))
      }
    }

  val endpoints: ZEnvEndpoints = NonEmptyList
    .of(
      getProfile
    )
    .map(_.tag(user))

}

object UserApi {
  def apply(service: UserService)(implicit auth: Authenticator): ZEnvEndpoints = new UserApi(service).endpoints
}
