package io.vaaas.user.auth

import cats.data.{ Validated, ValidatedNel }
import cats.implicits.catsSyntaxTuple4Semigroupal
import io.vaaas.BadRequest.RequestError
import io.vaaas.user.context.UserContext.Context
import io.vaaas.{ BadRequest, ServiceException }
import io.vaaas.user.exceptions.{ EmailInUse, InvalidCredentials }
import io.vaaas.user.{ Auth, Email, Password, User, UserId, UserRepository }
import tsec.common.{ VerificationFailed, Verified }
import zio.{ Task, ZIO }

import scala.concurrent.duration.Duration

class LiveAuthService(
  userRepository: UserRepository,
  apiKeyService: ApiKeyService,
  apiKeyAuth: Auth[ApiKey, ApiKeyId]
)(signupCfg: SignupConfig)
    extends AuthService {

  def signup(
    rawEmail: String,
    rawPassword: String,
    firstName: String,
    lastName: String
  ): Task[(ApiKey, User)] = {

    def minLength(field: String, value: String): ValidatedNel[RequestError, String] = Validated.condNel(
      value.nonEmpty,
      value,
      RequestError(field, "Must not be empty")
    )

    val validated: ValidatedNel[RequestError, (String, String, Email, Password)] = (
      minLength("firstName", firstName),
      minLength("lastName", lastName),
      Email(rawEmail),
      Password(rawPassword)(signupCfg.passwordRules)
    ).tupled

    for {
      (firstName, lastName, email, password) <- ZIO.fromEither {
        validated.toEither
      }.mapError[ServiceException] { e =>
        BadRequest.fromErrors(e.toList: _*)
      }

      _ <- userRepository.findByEmail(email).flatMap {
        case Some(_) => Task.fail(EmailInUse())
        case None    => Task.succeed()
      }

      u0 = User(
        UserId.none,
        email,
        password.hash,
        firstName,
        lastName,
        Map.empty
      )

      u1 <- userRepository.save(u0)

      //  _ <- emailService.userRegistered(model)

      apiKey <- apiKeyService.create(u1.id)
    } yield (apiKey, u1)
  }

  def login(
    rawEmail: String,
    rawPassword: String,
    apiKeyValidHours: Option[Duration]
  ): Task[(ApiKey, User)] =
    for {
      user <- userRepository.findByEmail(Email.unsafe(rawEmail)).map(_.toRight(InvalidCredentials)).absolve

      _ <- LiveAuthService.verifyPassword(user, rawPassword)

      apiKey <- apiKeyService.create(user.id, apiKeyValidHours)
    } yield (apiKey, user)

  override def auth(id: ApiKeyId): Task[Context] = {
    println("id: " + id)
    for {
      apiKey <- apiKeyAuth(id)
      _ = println("apikey: " + apiKey)
      user <- userRepository.byId(apiKey.userId)
    } yield user.toCtx
  }
}

object LiveAuthService {
  def apply(
    repo: UserRepository,
    apiKeyService: ApiKeyService,
    apiKeyAuth: Auth[ApiKey, ApiKeyId]
  )(signupCfg: SignupConfig): AuthService = {
    new LiveAuthService(repo, apiKeyService, apiKeyAuth)(signupCfg)
  }

  def verifyPassword(user: User, rawPassword: String): ZIO[Any, Throwable, Unit] =
    user.verifyPassword(rawPassword) match {
      case Verified           => Task.succeed()
      case VerificationFailed => Task.fail(InvalidCredentials)
    }
}
