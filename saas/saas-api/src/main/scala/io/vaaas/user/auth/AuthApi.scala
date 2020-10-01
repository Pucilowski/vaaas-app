package io.vaaas.user.auth

import cats.data.NonEmptyList
import io.vaaas.tapir.{TapirDsl, ZEnvEndpoint, ZEnvEndpoints}
import io.vaaas.user.auth.command.{Login, Signup}
import io.vaaas.user.auth.view.AuthResult
import io.vaaas.user.view.UserProfileView

import scala.concurrent.duration.{Duration, HOURS}
import sttp.tapir.ztapir._

case class AuthApi(service: AuthService) extends TapirDsl {
  val auth_ = "auth"

  private val signup: ZEnvEndpoint =
    baseEndpoint.post.in(auth_ / "signup").in(Signup.body).out(AuthResult.body).zServerLogic {
      case Signup(email, password, firstName, lastName) =>
        service.signup(email, password, firstName, lastName)
          .bimap(toApi, {
            case (apiKey, user) =>
              val profile = UserProfileView.apply(user)
              AuthResult(apiKey.id, profile)
          })
    }

  private val login: ZEnvEndpoint =
    baseEndpoint.post.in(auth_ / "login").in(Login.body).out(AuthResult.body).zServerLogic {
      case Login(email, password, duration) =>
        service
          .login(email, password, duration.map(d => Duration(d.toLong, HOURS)))
          .bimap(
            toApi,
            {
              case (apiKey, user) =>
                val profile = UserProfileView.apply(user)
                AuthResult(apiKey.id, profile)
            }
          )
    }

  val endpoints: ZEnvEndpoints = NonEmptyList
    .of(
      signup,
      login
    )
    .map(_.tag(auth_))
}


object AuthApi {
  def apply(
             authService: AuthService
           ): ZEnvEndpoints =
    new AuthApi(authService).endpoints
}
