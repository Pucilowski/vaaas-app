package io.vaaas.user.auth

import io.vaaas.user.User
import io.vaaas.user.context.UserContext.Context
import zio.Task

import scala.concurrent.duration.Duration

trait AuthService {
  def signup(
              rawEmail: String,
              rawPassword: String,
              firstName: String,
              lastName: String
            ): Task[(ApiKey, User)]

  def login(
             rawEmail: String,
             rawPassword: String,
             apiKeyValidHours: Option[Duration]
           ): Task[(ApiKey, User)]

  def auth(
            id: ApiKeyId
          ): Task[Context]
}
