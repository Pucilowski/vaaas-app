package io.vaaas.user.auth

import java.time.Instant

import doobie.implicits._
import doobie.Meta
import doobie.util.transactor.Transactor
import io.vaaas.user.{AuthTokenOps, UserId}
import zio.Task
import zio.interop.catz._

case class ApiKeyRepository(
  xa: Transactor[Task]
) extends AuthTokenOps[ApiKey, ApiKeyId]
    /*with doobie.util.meta.LegacyInstantMetaInstance*/ {

  implicit val idMeta: Meta[ApiKeyId] =
    Meta.StringMeta.timap(ApiKeyId.apply)(_.value)

  def insert(apiKey: ApiKey): Task[Unit] = {
    sql"""INSERT INTO api_keys (id, user_id, created_on, valid_until)
         |VALUES (${apiKey.id}, ${apiKey.userId}, ${apiKey.createdOn}, ${apiKey.validUntil})""".stripMargin.update.run
      .transact(xa)
      .unit
  }

  def findById(id: ApiKeyId): Task[Option[ApiKey]] = {
    println(s"findById($id)")

    sql"""SELECT id, user_id, created_on, valid_until FROM api_keys WHERE id = $id""".query[ApiKey].option.transact(xa)
      .map { result =>

        println(s"result!!!: $result")

        result
      }
  }

  def delete(id: ApiKeyId): Task[Unit] = {
    sql"""DELETE FROM api_keys WHERE id = $id""".update.run.transact(xa).unit
  }

  override def tokenName: String = "ApiKey"
//  override def findById: ApiKeyId => Task[Option[ApiKey]] = findById
  override def delete: ApiKey => Task[Unit] = ak => delete(ak.id)
  override def userId: ApiKey => UserId = _.userId
  override def validUntil: ApiKey => Instant = _.validUntil
  override def deleteWhenValid: Boolean = false
}
