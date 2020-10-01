package io.vaaas.user

import java.security.SecureRandom

import cats.data.OptionT
import com.typesafe.scalalogging.StrictLogging
import io.vaaas.Unauthorized
import zio.clock.Clock
import zio.duration._
import zio.interop.catz._
import zio.{Has, Task, ZIO}

class Auth[T, Id](
  authTokenOps: AuthTokenOps[T, Id],
  clock: Clock.Service
) extends StrictLogging {

  // see https://hackernoon.com/hack-how-to-use-securerandom-with-kubernetes-and-docker-a375945a7b21
  private val random = SecureRandom.getInstance("NativePRNGNonBlocking")

  /**
   * Authenticates using the given authentication token. If the token is invalid, a failed [[Task]] is returned,
   * with an instance of the [[Fail]] class. Otherwise, the id of the authenticated user is given.
   */
  def apply(id: Id): Task[T] = {
    val tokenOpt = (for {
      token <- OptionT(authTokenOps.findById(id))
      _ = println(s"token: $token")
      validated <- OptionT(verifyValid(token))
      _ = println(s"validated: $validated")
    } yield token).value

    tokenOpt.flatMap {
      case None =>
        logger.debug(s"Auth failed for: ${authTokenOps.tokenName} $id")
        // random sleep to prevent timing attacks
        val wait: Task[Unit] = ZIO.sleep(random.nextInt(1000).millis).provide(Has(clock))

        wait *> Task.fail(Unauthorized())
      case Some(token) =>
        val delete = if (authTokenOps.deleteWhenValid) authTokenOps.delete(token) else Task.unit

        delete *> Task.succeed(token)
    }
  }

  private def verifyValid(token: T): Task[Option[Unit]] = {
    for {
      instant <- clock.instant
      result <-
        if (instant.isAfter(authTokenOps.validUntil(token))) {
          logger.info(s"${authTokenOps.tokenName} expired: $token")
          authTokenOps.delete(token).as(None)
        } else Task(Some(()))
    } yield result

  }
}
