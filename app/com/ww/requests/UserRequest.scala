package com.ww.requests

import com.ww.models.{Credentials, PII}
import play.api.libs.functional.syntax._
import play.api.libs.json._
import com.ww.utils.Constants.Fields

case class UserRequest(credentials: Credentials, pii: PII)

object UserRequest {
  // (__ \ "credentials").read[Credentials] // if request is coming in credential key
  // Credentials.reads // if request is coming at root level
  implicit val reads: Reads[UserRequest] = (
    (__ \ Fields.CREDENTIALS).read[Credentials] and
    (__ \ Fields.PII).read[PII]
  )(UserRequest.apply _)

  implicit val writes = Json.writes[UserRequest]
}
