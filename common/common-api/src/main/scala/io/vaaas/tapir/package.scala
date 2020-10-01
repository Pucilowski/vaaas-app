package io.vaaas

import cats.data.NonEmptyList
import sttp.tapir.ztapir.ZServerEndpoint
import zio.ZEnv

package object tapir {

  type ZEnvEndpoint = ZServerEndpoint[ZEnv, _, _, _]
  type ZEnvEndpoints = NonEmptyList[ZServerEndpoint[ZEnv, _, _, _]]

}
