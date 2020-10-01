package io.vaaas.http

import sttp.tapir.server.{DecodeFailureContext, DecodeFailureHandler, DecodeFailureHandling, ServerDefaults}

object VaaasDecodeFailureHandler extends DecodeFailureHandler {
  def apply(ctx: DecodeFailureContext): DecodeFailureHandling =
    ServerDefaults.decodeFailureHandler(ctx)
}
