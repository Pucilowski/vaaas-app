package io.vaaas.user

import cats.data.NonEmptyList
import io.vaaas.{BadRequest, ServiceException}

object exceptions {

  val InvalidEmail = BadRequest(
    "email" -> "Invalid email"
  )

  object EmailInUse {
    def apply(): ServiceException = BadRequest(
      "email" -> "Email already in use"
    )
  }

  object IllegalPassword {
    def apply(feedback: NonEmptyList[String]): ServiceException = BadRequest(
      errors = Map("password" -> feedback.toList)
    )
  }

  val InvalidCredentials = BadRequest(
    "password" -> "Invalid username or password"
  )

}
