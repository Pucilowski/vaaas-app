package io.vaaas.http

import cats.implicits._
import io.vaaas.ZTask
import io.vaaas.tapir.ZEnvEndpoints
import org.http4s.HttpRoutes
import org.http4s.server.Router
import org.http4s.server.middleware.CORS
import sttp.tapir.docs.openapi._
import sttp.tapir.openapi
import sttp.tapir.openapi.circe.yaml._
import sttp.tapir.server.http4s._
import sttp.tapir.server.http4s.ztapir._
import sttp.tapir.swagger.http4s.SwaggerHttp4s
import zio.interop.catz._
import zio.{Task, URIO, ZEnv}
import zio.interop.catz._
import org.http4s.implicits._

class Routes(
              endpoints: ZEnvEndpoints,
              apiContextPath: String
            ) {

  implicit val serverOptions: Http4sServerOptions[ZTask] = Http4sServerOptions
    .default[ZTask]
    .copy(
      decodeFailureHandler = VaaasDecodeFailureHandler
    )

  val apiRoutes: URIO[ZEnv, HttpRoutes[Task]] = {
    endpoints.toList.toRoutesR
  }

  def docsRoutes: HttpRoutes[Task] = {
    val es = endpoints.map(_.endpoint)
    val oapi = es.toList.toOpenAPI("Opteq", "1.0").servers(List(openapi.Server(s"$apiContextPath", None)))
    val yaml = oapi.toYaml
    new SwaggerHttp4s(yaml).routes[Task]
  }

  def routes: URIO[ZEnv, HttpRoutes[Task]] = for {
    api <- apiRoutes
    docs = docsRoutes

    val routes = (CORS(api) <+> docs)

    app = Router[Task](
      s"$apiContextPath" -> routes
    )
  } yield app
}

object Routes {
  def apply(
             endpoints: ZEnvEndpoints,
             apiContextPath: String = "/api/v1"
           ): URIO[ZEnv, HttpRoutes[Task]] = {
    val r = new Routes(endpoints, apiContextPath)

    r.routes
  }
}
