package io.vaaas.user

import io.vaaas.user.context.UserContext
import zio.Task

package object auth {
  type Authenticator = String => Task[UserContext.Context]
}
