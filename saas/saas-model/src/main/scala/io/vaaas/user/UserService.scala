package io.vaaas.user

import io.vaaas.UserTask

trait UserService {
  def userProfile(): UserTask[User]
}
