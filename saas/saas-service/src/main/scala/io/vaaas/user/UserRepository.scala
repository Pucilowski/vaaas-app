package io.vaaas.user

import doobie.Transactor
import zio.Task
import doobie.implicits._
import doobie.util.log.LogHandler
import zio.interop.catz._

class UserRepository(xa: Transactor[Task]) extends UserMappers {

  def save(model: User): Task[User] = UserSQL
    .update(model)
    .updateWithLogHandler(LogHandler.jdkLogHandler)
    .withGeneratedKeys[User]("id", "email", "password", "first_name", "last_name", "meta", "roles")
    .compile
    .lastOrError
    .transact(xa)

  def update(id: UserId, fn: User => User): Task[User] = {
    for {
      u0 <- UserSQL.select(id).query[User].unique
      u1 = fn(u0)
      _ <- UserSQL.insert(u1).update.run
    } yield u1
  }.transact(xa)

  def findById(id: UserId): Task[Option[User]] =
    UserSQL.select(id).query[User].option.transact(xa)

  def findByEmail(email: Email): Task[Option[User]] =
    UserSQL.select(email).option.transact(xa)

  def byId(id: UserId): Task[User] =
    UserSQL.select(id).query[User].unique.transact(xa)

}

object UserRepository {
  def apply(xa: Transactor[Task]): UserRepository = new UserRepository(xa)
}
