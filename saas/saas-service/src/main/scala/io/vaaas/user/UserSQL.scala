package io.vaaas.user

import doobie.implicits._
import doobie.util.fragment
import doobie.util.query.Query0

object UserSQL extends UserMappers {

  def insert(model: User): fragment.Fragment =
    sql"""
      INSERT INTO users (email, password, first_name, last_name, meta)
      VALUES (${model.email}, ${model.passwordHash}, ${model.firstName}, ${model.lastName}, ${model.meta})
      """

  def update(model: User): fragment.Fragment =
    sql"""
      INSERT INTO users (email, password, first_name, last_name, meta)
      VALUES (${model.email}, ${model.passwordHash}, ${model.firstName}, ${model.lastName}, ${model.meta})
      ON CONFLICT (email)
      DO UPDATE SET
        first_name = EXCLUDED.first_name,
        last_name = EXCLUDED.last_name,
        password = EXCLUDED.password,
        meta = EXCLUDED.meta
      """

  def select(id: UserId): fragment.Fragment = sql"""
      SELECT * FROM users WHERE id = $id
      """

  def select(email: Email): Query0[User] = sql"""
      SELECT * FROM users WHERE email = $email
      """.query[User]

}
