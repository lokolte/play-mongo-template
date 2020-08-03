package com.ww.services.impl

import java.time.Clock
import com.ww.models.{Credentials, User}
import com.ww.services.{AuthService, UserService}
import javax.inject.{Inject, Singleton}
import pdi.jwt.{Jwt, JwtAlgorithm, JwtClaim}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

@Singleton
class AuthServiceImpl @Inject()(userService: UserService)(implicit ec: ExecutionContext)
    extends AuthService {

  implicit val clock: Clock = Clock.systemUTC
  val SECRET_KEY            = "my_secret_key"

  override def isValidToken(token: String): Future[Boolean] = Future {
    Jwt.isValid(token, SECRET_KEY, Seq(JwtAlgorithm.HS512))
  }

  override def createToken(c: Credentials): Future[Try[String]] =
    userService
      .findByEmailAndPassword(c.email, c.password)
      .map(
        _.fold[Try[String]](Failure(new Throwable("user not found")))(
          (u: User) => Success(encrypt(s"""{"email": "${c.email}"}"""))
        )
      )

  override def decryptToken(token: String): Future[Try[String]] = Future {
    Jwt.decodeRaw(token, SECRET_KEY, Seq(JwtAlgorithm.HS512))
  }

  def encrypt(payload: String): String =
    Jwt.encode(
      JwtClaim({ s"""{"payload":$payload}""" }).issuedNow.expiresIn(900), // 900 seconds = 15 mins
      SECRET_KEY,
      JwtAlgorithm.HS512
    )

}
