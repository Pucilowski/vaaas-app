package io.vaaas.user.auth

import io.vaaas.tapir.TapirDsl
import io.vaaas.user.context.UserContext
import io.vaaas.user.context.UserContext.UserContext
import io.vaaas.{ServiceException, UserTask}
import sttp.tapir.ztapir.{ZPartialServerEndpoint, _}
import zio.{Has, ZEnv, ZIO}

trait TapirAuthDsl extends TapirDsl {

  def authedEndpoint(implicit
                     authenticator: Authenticator
                    ): ZPartialServerEndpoint[ZEnv, UserContext.UserContext, Unit, ServiceException, Unit] =
    baseEndpoint.in(auth.bearer[String]).zServerLogicForCurrent { token =>
      authenticator(token).bimap(toApi, Has(_))
    }

  def fromServiceCall[I, O](call: I => UserTask[O]): ((UserContext, I)) => ZIO[ZEnv, ServiceException, O] = {
    case (ctx: UserContext, i) => {
      call(i).provideSome[ZEnv](zEnv => zEnv ++ ctx).mapError(toApi)
    }
  }

}
