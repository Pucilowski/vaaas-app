package io.vaaas.user.auth

import java.time.Instant

import io.vaaas.user.UserId

case class ApiKey(id: ApiKeyId, userId: UserId, createdOn: Instant, validUntil: Instant)
