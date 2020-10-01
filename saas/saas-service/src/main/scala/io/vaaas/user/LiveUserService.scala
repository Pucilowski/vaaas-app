package io.vaaas.user

import io.vaaas.UserTask

class LiveUserService(repo: UserRepository) extends UserService {
  override def userProfile(): UserTask[User] = for {
    userId: UserId <- context.userId
    user: User <- repo.byId(userId)
  } yield user
}

object LiveUserService {
  def apply(repo: UserRepository): UserService = {
    new LiveUserService(repo)
  }
}
