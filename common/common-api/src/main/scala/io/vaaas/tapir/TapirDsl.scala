package io.vaaas.tapir

import io.vaaas._
import sttp.model.StatusCode
import sttp.tapir.json.circe.TapirJsonCirce
import sttp.tapir.{Endpoint, EndpointOutput, Tapir}

trait TapirDsl extends Tapir with TapirJsonCirce {

  import io.circe.generic.auto._

  val badRequest: EndpointOutput.StatusMapping[BadRequest] = statusMapping(StatusCode.BadRequest, jsonBody[BadRequest])
  val unauthorized: EndpointOutput.StatusMapping[Unauthorized] = statusMapping(StatusCode.Unauthorized, jsonBody[Unauthorized])
  val forbidden: EndpointOutput.StatusMapping[Forbidden] = statusMapping(StatusCode.Forbidden, jsonBody[Forbidden])
  val notFound: EndpointOutput.StatusMapping[NotFound] = statusMapping(StatusCode.NotFound, jsonBody[NotFound])
  val internalServerError: EndpointOutput.StatusMapping[InternalServerError] = statusMapping(StatusCode.InternalServerError, jsonBody[InternalServerError])

  val failOutput: EndpointOutput[ServiceException] = oneOf(
    badRequest,
    unauthorized,
    forbidden,
    notFound,
    internalServerError
  )

  val baseEndpoint: Endpoint[Unit, ServiceException, Unit, Nothing] =
    endpoint.errorOut(failOutput)

  val secureEndpoint: Endpoint[String, ServiceException, Unit, Nothing] =
    baseEndpoint.in(auth.bearer[String])

  def toApi(thr: Throwable): ServiceException = thr match {
    case e: ServiceException => e
    //    case e: org.postgresql.util.PSQLException =>
    //      e.printStackTrace()
    //
    //      InternalServerError("Persistence error: " + e.toString)
    case e =>
      e.printStackTrace()
      InternalServerError(e.toString)
  }
}
