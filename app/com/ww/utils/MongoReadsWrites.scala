package com.ww.utils

import java.util.UUID
import com.ww.models.{Gender, PhoneType}
import com.ww.models.PhoneType.PhoneType
import com.ww.models.Gender.Gender
import reactivemongo.api.bson.{BSONReader, BSONString, BSONValue, BSONWriter}
import reactivemongo.bson.{BSONDateTime, BSONDocument, BSONDocumentWriter, BSONObjectID}
import scala.util.{Failure, Success, Try}
import com.ww.utils.Constants.Fields.{_ID, REGISTRATION_DATE}

object MongoReadsWrites {
  implicit object objectIDBSONWriter extends BSONDocumentWriter[BSONObjectID] {
    override def write(pt: BSONObjectID)   = BSONDocument(_ID -> pt)
    override def writeTry(t: BSONObjectID) = Try(write(t))
  }

  implicit object uuidBSONWriter extends BSONWriter[UUID] {
    override def writeTry(uuid: UUID) = Try(BSONString(uuid.toString))
  }

  implicit object uuidBSONReader extends BSONReader[UUID] {
    override def readTry(bson: BSONValue) = bson.asTry[String] match {
      case Failure(exception) => Failure(exception)
      case Success(value)     => Success(UUID.fromString(value))
    }
  }

  implicit object phoneTypeBSONWriter extends BSONWriter[PhoneType] {
    override def writeTry(pt: PhoneType) = enumWriter(pt)
  }

  implicit object phoneTypeBSONReader extends BSONReader[PhoneType] {
    override def readTry(bson: BSONValue) = enumReader(bson, PhoneType)
  }

  implicit object genderBSONWriter extends BSONWriter[Gender] {
    override def writeTry(g: Gender) = enumWriter(g)
  }

  implicit object genderBSONReader extends BSONReader[Gender] {
    override def readTry(bson: BSONValue) = enumReader(bson, Gender)
  }

  def enumWriter[E <: Enumeration](enum: E#Value) = Try(BSONString(enum.toString.toUpperCase))

  def enumReader[E <: Enumeration](bson: BSONValue, enum: E) = bson match {
    case BSONString(s) => Try(enum.withName(s.trim.toUpperCase))
    case _             => Failure(new RuntimeException("String value expected to map to " + enum))
  }

  implicit object dateBSONWriter extends BSONDocumentWriter[BSONDateTime] {
    def write(pt: BSONDateTime)            = BSONDocument(REGISTRATION_DATE -> pt)
    override def writeTry(t: BSONDateTime) = Try(write(t))
  }
}
