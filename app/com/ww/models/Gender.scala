package com.ww.models

import play.api.libs.json.JsString

object Gender extends Enumeration {
  type Gender = Value
  val MALE, FEMALE, UNKNOWN     = Value
  implicit val genderJsonWrites = (g: Gender) => JsString(g.toString.toUpperCase)
}
