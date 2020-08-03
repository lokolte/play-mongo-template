package com.ww.utils

import java.time.LocalDate

import com.ww.models.PhoneType
import com.ww.models.Gender
import play.api.libs.json.{
  JsError,
  JsLookupResult,
  JsPath,
  JsResult,
  JsSuccess,
  JsValue,
  JsonValidationError,
  Reads
}
import play.api.libs.json.Reads._
import com.ww.utils.Constants.Fields._
import com.ww.utils.Constants.MessageKeys._

import scala.util.{Failure, Success, Try}
import scala.util.matching.Regex

object RequestValidators {

  private def error(msg: String, key: String, args: Any*) =
    JsError(JsPath \ key, JsonValidationError(msg, args: _*))

  val EMAIL_REGEX: Regex =
    """(?:[A-Za-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[A-Za-z0-9!#$%&'*+/=?^_`{|}~-]+)*|"(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21\x23-\x5b\x5d-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])*")@(?:(?:[A-Za-z0-9](?:[A-Za-z0-9-]*[A-Za-z0-9])?\.)+[A-Za-z0-9](?:[A-Za-z0-9-]*[A-Za-z0-9])?|\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[A-Za-z0-9-]*[A-Za-z0-9]:(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21-\x5a\x53-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])+)\])""".stripMargin.r

  val PASSWORD_REGEX: Regex = """^.{6,32}$""".r

  def validateEmails(key: String = "emails",
                     f: String => Boolean = isValidEmail,
                     msg: String = "Should be an array of valid emails."): Reads[Seq[String]] =
    (json: JsValue) => validateArray(key, (json \ key), Reads.seq[String], f, msg)

  def validateArray[T](key: String,
                       elems: JsLookupResult,
                       reads: Reads[Seq[T]],
                       f: T => Boolean,
                       msg: String): JsResult[Seq[T]] =
    if (elems.isDefined)
      elems.validate[Seq[T]](reads) match {
        case JsSuccess(v, _) => handleArraySuccess(key, v, f, msg)
        case err: JsError    => handleArrayFailure(key, err, msg)
      }
    else
      error("error.path.missing", key)

  def handleArraySuccess[T](key: String,
                            xs: Seq[T],
                            f: T => Boolean,
                            msg: String): JsResult[Seq[T]] =
    if (xs.forall(f)) JsSuccess(xs)
    else error(s"$msg Invalid values are ${xs.filterNot(f)}. ", key)

  def handleArrayFailure(key: String, jsError: JsError, msg: String): JsError =
    if (jsError.errors.toString.contains("error.expected.jsarray"))
      error(msg, key)
    else
      error(jsError.errors.map(e => s"value at index ${e._1} has invalid type.").mkString(" "), key)

  def validateAges(
      key: String = "ages",
      f: Int => Boolean = _ > 18,
      msg: String = "Should be an array of integers greater than 18."
  ): Reads[Seq[Int]] =
    (json: JsValue) => validateArray(key, (json \ key), Reads.seq[Int], f, msg)

  def isValidEmail(email: String) = EMAIL_REGEX.matches(email)

  def isValidName(name: String) = name.length > 1 && name.length < 50

  def isValidPassword(password: String) = PASSWORD_REGEX.matches(password)

  def validate[T](key: String, value: Option[T], f: T => Boolean, msg: String): JsResult[T] =
    value match {
      case None => error("error.path.missing", key)
      case Some(v) =>
        if (f(v)) JsSuccess(v)
        else error(msg, key)
    }

  def validateOptional[T](key: String,
                          value: Option[T],
                          f: T => Boolean,
                          msg: String): JsResult[Option[T]] =
    value match {
      case None => JsSuccess(None)
      case Some(v) =>
        if (f(v)) JsSuccess(Some(v))
        else error(msg, key)
    }

  def validateEnum[E <: Enumeration](key: String,
                                     msgKey: String = INVALID_VALUE,
                                     enum: E): Reads[E#Value] =
    (json: JsValue) =>
      Try(enum.withName((json \ key).asOpt[String].get.trim.toUpperCase)) match {
        case Success(e) => JsSuccess(e)
        case Failure(_) => error(msgKey, key, enumValues(enum))
      }

  def validateOptionalEnum[E <: Enumeration](key: String,
                                             msgKey: String = INVALID_VALUE,
                                             enum: E): Reads[Option[E#Value]] =
    (json: JsValue) =>
      (json \ key).asOpt[String] match {
        case None => JsSuccess(None)
        case Some(e) =>
          Try(enum.withName(e.trim.toUpperCase)) match {
            case Success(v) => JsSuccess(Some(v))
            case Failure(_) => error(msgKey, key, enumValues(enum))
          }
      }

  def enumValues(enum: Enumeration): String =
    enum.values.toList.map(_.toString.toUpperCase).mkString(", ")

  def validateEmail(key: String = EMAIL): Reads[String] =
    validateString(key, isValidEmail, INVALID_EMAIL)

  def validatePassword(key: String = PASSWORD): Reads[String] =
    validateString(key, isValidPassword, INVALID_PASSWORD)

  def validateOptionalPassword(key: String = PASSWORD): Reads[Option[String]] =
    validateOptionalString(key, isValidPassword, INVALID_PASSWORD)

  def validateName(key: String): Reads[String] =
    validateString(key, isValidName, INVALID_NAME)

  def validateOptionalName(key: String): Reads[Option[String]] =
    validateOptionalString(key, isValidName, INVALID_NAME)

  def validateInt(key: String, f: Int => Boolean, msg: String = INVALID_VALUE): Reads[Int] =
    (json: JsValue) => validate[Int](key, (json \ key).asOpt[Int], f, msg)

  def validateOptionalInt(key: String,
                          f: Int => Boolean,
                          msg: String = INVALID_VALUE): Reads[Option[Int]] =
    (json: JsValue) => validateOptional[Int](key, (json \ key).asOpt[Int], f, msg)

  def validateString(key: String,
                     f: String => Boolean,
                     msg: String = INVALID_VALUE): Reads[String] =
    (json: JsValue) => validate[String](key, (json \ key).asOpt[String], f, msg)

  def validateOptionalString(
      key: String,
      f: String => Boolean,
      msg: String = INVALID_VALUE
  ): Reads[Option[String]] =
    (json: JsValue) => validateOptional[String](key, (json \ key).asOpt[String], f, msg)

  def validateLocalDate(key: String): Reads[LocalDate] =
    (json: JsValue) =>
      Try(LocalDate.parse((json \ key).asOpt[String].get.trim)) match {
        case Success(value) => JsSuccess(value)
        case Failure(exp)   => error(INVALID_LOCAL_DATE, key)
      }

  def validateOptionalLocalDate(key: String): Reads[Option[LocalDate]] =
    (json: JsValue) =>
      (json \ key).asOpt[String] match {
        case None => JsSuccess(None)
        case Some(d) =>
          Try(LocalDate.parse(d)) match {
            case Success(value) => JsSuccess(Some(value))
            case Failure(exp)   => error(INVALID_LOCAL_DATE, key)
          }
      }

  def validateGender(key: String = GENDER) =
    validateEnum(key, INVALID_GENDER, Gender)

  def validateOptionalGender(key: String = GENDER) =
    validateOptionalEnum(key, INVALID_GENDER, Gender)

  def validatePhoneType(key: String = PHONE_TYPE) =
    validateEnum(key, INVALID_PHONE_TYPE, PhoneType)

  def validateOptionalPhoneType(key: String = PHONE_TYPE) =
    validateOptionalEnum(key, INVALID_PHONE_TYPE, PhoneType)
}
