package io.vaaas.user.auth

import java.time.temporal.ChronoUnit

import com.typesafe.scalalogging.StrictLogging
import io.vaaas.user.UserId
import zio.Task
import zio.clock.Clock

import scala.concurrent.duration.Duration

class ApiKeyService(
  repo: ApiKeyRepository,
  clock: Clock.Service
)(
  sessionCfg: SessionConfig
) extends StrictLogging {

  def create(userId: UserId, validOpt: Option[Duration] = None): Task[ApiKey] = {
    val valid = validOpt.getOrElse(sessionCfg.length)

    for {
      now <- clock.instant
      validUntil = now.plus(valid.toMinutes, ChronoUnit.MINUTES)
      id = ApiKeyId()
      apiKey = ApiKey(id, userId, now, validUntil)

      _ = logger.debug(s"Creating a new api key for user $userId, valid until: $validUntil")

      _ <- repo.insert(apiKey)
    } yield apiKey
  }

}
