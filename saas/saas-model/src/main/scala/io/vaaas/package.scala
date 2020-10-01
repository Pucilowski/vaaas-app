package io

import io.circe.Json
import io.vaaas.user.context.UserContext.UserContext
import zio.{RIO, ZEnv}

package object vaaas {

  type ZTask[A] = RIO[ZEnv, A]
  type UserTask[A] = RIO[ZEnv with UserContext, A]

  type Meta = Map[String, Json]
}
