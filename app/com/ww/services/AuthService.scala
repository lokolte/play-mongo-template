package com.ww.services

import com.google.inject.ImplementedBy
import com.ww.models.Credentials
import com.ww.services.impl.AuthServiceImpl
import scala.concurrent.Future
import scala.util.Try

@ImplementedBy(classOf[AuthServiceImpl])
trait AuthService {
  def isValidToken(token: String): Future[Boolean]
  def createToken(c: Credentials): Future[Try[String]]
  def decryptToken(token: String): Future[Try[String]]
}
