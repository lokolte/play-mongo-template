package com.ww.requests

import java.time.LocalDate

import com.ww.models.Gender.Gender
import com.ww.models.PhoneType.PhoneType
import play.api.libs.json.{Json, Reads}
import com.ww.utils.RequestValidators._
import play.api.libs.functional.syntax._
import com.ww.utils.Constants.Fields._

case class PatchUserRequest(password: Option[String] = None,
                            title: Option[String] = None,
                            firstName: Option[String] = None,
                            middleName: Option[String] = None,
                            lastName: Option[String] = None,
                            timeZone: Option[String] = None,
                            utcOffset: Option[Int] = None,
                            dob: Option[LocalDate] = None,
                            gender: Option[Gender] = None,
                            phoneType: Option[PhoneType] = None)

object PatchUserRequest {
  implicit val reads: Reads[PatchUserRequest] = (
    validateOptionalPassword() and
    validateOptionalString(TITLE, _.length > 1) and
    validateOptionalName(FIRST_NAME) and
    validateOptionalName(MIDDLE_NAME) and
    validateOptionalName(LAST_NAME) and
    validateOptionalString(TIME_ZONE, _.length > 4) and
    validateOptionalInt(UTC_OFFSET, _ > -10) and
    validateOptionalLocalDate(DOB) and
    validateOptionalGender() and
    validateOptionalPhoneType()
  )(PatchUserRequest.apply _)

  implicit val writes = Json.writes[PatchUserRequest]

}
