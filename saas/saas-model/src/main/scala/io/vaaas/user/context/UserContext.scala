package io.vaaas.user.context

import io.vaaas.user.UserId
import zio.Has

object UserContext {
  type UserContext = Has[Context]

  case class Context(
    id: UserId
  )
}
