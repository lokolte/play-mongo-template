package com.ww.models

import play.api.libs.json.JsString

object PhoneType extends Enumeration {
  type PhoneType = Value
  val MOBILE, WORK, HOME, UNKNOWN = Value
  implicit val ptJsonWrites       = (p: PhoneType) => JsString(p.toString.toUpperCase)
}
