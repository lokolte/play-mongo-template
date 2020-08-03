package com.ww.utils

object Constants {
  object Fields {
    val _ID               = " _id"
    val TITLE             = "title"
    val FIRST_NAME        = "firstName"
    val LAST_NAME         = "lastName"
    val MIDDLE_NAME       = "middleName"
    val DOB               = "dob"
    val TIME_ZONE         = "timeZone"
    val UTC_OFFSET        = "utcOffset"
    val PHONE_TYPE        = "phoneType"
    val GENDER            = "gender"
    val MALE              = "male"
    val FEMALE            = "female"
    val EMAIL             = "email"
    val PASSWORD          = "password"
    val CREDENTIALS       = "credentials"
    val PII               = "pii"
    val UUID              = "uuid"
    val REGISTRATION_DATE = "registrationDate"

    val CREDENTIALS_EMAIL    = s"$CREDENTIALS.$EMAIL"
    val CREDENTIALS_PASSWORD = s"$CREDENTIALS.$PASSWORD"
    val PII_TITLE            = s"$PII.$TITLE"
    val PII_FIRST_NAME       = s"$PII.$FIRST_NAME"
    val PII_LAST_NAME        = s"$PII.$LAST_NAME"
    val PII_MIDDLE_NAME      = s"$PII.$MIDDLE_NAME"
    val PII_DOB              = s"$PII.$DOB"
    val PII_TIMEZONE         = s"$PII.$TIME_ZONE"
    val PII_UTC_OFFSET       = s"$PII.$UTC_OFFSET"
    val PII_GENDER           = s"$PII.$GENDER"
    val PII_PHONE_TYPE       = s"$PII.$PHONE_TYPE"
  }

  object MessageKeys {
    val INVALID_VALUE       = "invalid.value"
    val INVALID_GENDER      = "invalid.gender"
    val INVALID_PHONE_TYPE  = "invalid.phoneType"
    val INVALID_NAME        = "invalid.name"
    val INVALID_LOCAL_DATE  = "invalid.localDate"
    val INVALID_PASSWORD    = "invalid.password"
    val INVALID_EMAIL       = "invalid.email"
    val USER_ALREADY_EXISTS = "user.already.exists"
  }
}
