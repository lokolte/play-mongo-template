package com.ww.models

import java.time.Instant
import java.util.UUID

import play.api.libs.json.{Json, Reads, Writes}
import reactivemongo.api.bson.Macros
import com.ww.utils.MongoReadsWrites._
import reactivemongo.api.bson.compat._

case class User(uuid: UUID, registrationDate: Instant, credentials: Credentials, pii: PII)

object User {
  implicit val userJsonWrites: Writes[User] = Json.writes[User]
  implicit val userJsonReads: Reads[User]   = Json.reads[User]
  implicit def userBSONDocumentReader       = Macros.reader[User]
  implicit def userBSONDocumentWriter       = Macros.writer[User]
}
