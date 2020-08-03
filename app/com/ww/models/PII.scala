package com.ww.models

import java.time.LocalDate
import play.api.libs.json._
import com.ww.models.Gender._
import com.ww.models.PhoneType._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._
import com.ww.utils.RequestValidators._
import reactivemongo.api.bson.Macros
import com.ww.utils.MongoReadsWrites._
import com.ww.utils.Constants.Fields._

case class PII(title: Option[String] = None,
               firstName: String,
               middleName: Option[String] = None,
               lastName: Option[String] = None,
               timeZone: String,
               utcOffset: Int,
               dob: Option[LocalDate] = None,
               phoneType: Option[PhoneType] = None,
               gender: Gender)

object PII {
  implicit val piiJsonReads = (
    validateOptionalString(TITLE, _.length > 1, "should be at least 2 chars long.") and
    validateName(FIRST_NAME) and
    validateOptionalName(MIDDLE_NAME) and
    validateOptionalName(LAST_NAME) and
    validateString(TIME_ZONE, _.length > 4) and
    validateInt(UTC_OFFSET, n => n > -10 && n < 10) and
    validateOptionalLocalDate(DOB) and
    validateOptionalPhoneType() and
    validateGender()
  )(PII.apply _)

  implicit val piiJsonWrites         = Json.writes[PII]
  implicit def piiBSONDocumentReader = Macros.reader[PII]
  implicit def piiBSONDocumentWriter = Macros.writer[PII]

//  implicit object piiBSONDocumentReader extends BSONDocumentReader[PII] {
//    override def readDocument(doc: BSONDocument): Try[PII] =
//      Try(
//        PII(
//          doc.getAsTry[String](TITLE).toOption,
//          doc.getAsTry[String](FIRST_NAME).get,
//          doc.getAsTry[String](MIDDLE_NAME).toOption,
//          doc.getAsTry[String](LAST_NAME).toOption,
//          doc.getAsTry[String](TIME_ZONE).get,
//          doc.getAsTry[Int](UTC_OFFSET).get,
//          doc.getAsTry[String](DOB).map(LocalDate.parse).toOption,
//          phoneTypeBSONReader.readTry(doc.get(PHONE_TYPE).getOrElse(BSONDocument())).toOption,
//          genderBSONReader.readTry(doc.get(GENDER).getOrElse(BSONDocument())).get
//        )
//      )
//  }

}
