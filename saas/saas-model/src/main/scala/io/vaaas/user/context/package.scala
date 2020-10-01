package io.vaaas.user

import io.vaaas.user.context.UserContext.{Context, UserContext}
import zio.{RIO, URIO, ZEnv, ZIO}

package object context {
  type UserTask[A] = RIO[ZEnv with UserContext, A]

  def ctx: URIO[UserContext.UserContext, Context] =
    ZIO.service[Context]

  def userId: ZIO[UserContext.UserContext, Nothing, UserId] =
    ctx.map(_.id)
}
