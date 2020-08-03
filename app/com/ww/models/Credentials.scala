package com.ww.models

import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._
import com.ww.utils.RequestValidators._
import reactivemongo.api.bson.Macros

case class Credentials(email: String, password: String)

object Credentials {
  implicit val credentialsJsonReads: Reads[Credentials] = (
    validateEmail() and
    validatePassword()
  )(Credentials.apply _)
    .filterNot(
      JsonValidationError("email and password can't be same.", "message")
    )(creds => creds.email == creds.password)

  implicit val credentialsJsonWrites         = Json.writes[Credentials]
  implicit def credentialsBSONDocumentReader = Macros.reader[Credentials]
  implicit def credentialsBSONDocumentWriter = Macros.writer[Credentials]
}
