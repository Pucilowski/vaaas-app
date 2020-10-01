package io.vaaas

sealed trait ServiceException extends Exception {
  val code: Int
  val message: String
}

case class BadRequest(
                       message: String = "Bad request",
                       errors: Map[String, List[String]] = Map.empty
                     ) extends ServiceException {
  val code = 400
}

object BadRequest {

  //  def apply(message: String): BadRequest = new BadRequest(message)

  case class RequestError(field: String, message: String)

  def apply(fields: (String, String)*): BadRequest = {
    val errors = fields.map {
      case (k, j) => k -> List(j)
    }
    new BadRequest(errors = errors.toMap)
  }

  def fromErrors(requestErrors: RequestError*): BadRequest = {
    val errors = requestErrors.groupBy(_.field).map {
      case (field, errors) =>
        field -> errors.toList.map(_.message)
    }

    BadRequest(errors = errors)
  }
}

case class Unauthorized(
                         message: String = "Unauthorized"
                       ) extends ServiceException {
  val code = 401
}

case class Forbidden(
                      message: String = "Forbidden"
                    ) extends ServiceException {
  val code = 403
}

case class NotFound(
                     message: String = "Not found"
                   ) extends ServiceException {
  val code = 404
}

case class InternalServerError(
                                message: String = "Internal server error"
                              ) extends ServiceException {
  val code = 500
}
