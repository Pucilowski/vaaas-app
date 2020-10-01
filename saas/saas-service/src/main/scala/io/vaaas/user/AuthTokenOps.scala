package io.vaaas.user

import java.time.Instant

import zio.Task

trait AuthTokenOps[T, Id] {
  def tokenName: String

  def findById(id: Id): Task[Option[T]]

  def delete: T => Task[Unit]

  def userId: T => UserId

  def validUntil: T => Instant

  def deleteWhenValid: Boolean
}
