package com.ww.services

import java.util.UUID
import com.google.inject.ImplementedBy
import com.ww.models.User
import com.ww.requests.{PatchUserRequest, UserRequest}
import com.ww.services.UserService.UserResponse
import com.ww.services.impl.UserServiceImpl
import scala.concurrent.Future

@ImplementedBy(classOf[UserServiceImpl])
trait UserService {
  def add(req: UserRequest): Future[UserResponse]
  def replace(email: String, req: UserRequest): Future[UserResponse]
  def update(email: String, req: PatchUserRequest): Future[UserResponse]
  def findByEmail(email: String): Future[Option[User]]
  def findByEmailAndPassword(email: String, password: String): Future[Option[User]]
}

object UserService {
  sealed trait UserResponse
  final case class UserSuccess(msg: String)  extends UserResponse
  final case class UserError(errMsg: String) extends UserResponse
  final case class UserCreated(uuid: UUID)   extends UserResponse
  final case object UserNotFound             extends UserResponse
  final case object UserAlreadyExists        extends UserResponse
}
