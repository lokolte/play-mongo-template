package controllers

import java.util.Locale

import javax.inject.{Inject, Singleton}
import play.api.i18n.Lang
import play.api.libs.json._
import play.api.mvc.{MessagesInjectedController, Request, Result}
import scala.collection.Seq
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class WWBaseController @Inject() ()(implicit ec: ExecutionContext)
    extends MessagesInjectedController {

  val MESSAGE = "message"
  val ERRORS  = "errors"

  implicit val lang = Lang(Locale.ENGLISH)

  def success(message: String): JsValue = success(Json.obj(MESSAGE -> message))

  def success(jsValue: JsValue) = Json.obj("data" -> jsValue)

  def error(message: String): JsValue = error(Json.obj(MESSAGE -> message))

  def error(jsValue: JsValue) = Json.obj(ERRORS -> JsArray(Seq(jsValue)))

  def handle[R](f: R => Future[Result])(implicit request: Request[JsValue], reads: Reads[R]) =
    request.body
      .validate[R]
      .map(f)
      .recoverTotal { e => Future(BadRequest(error(formatValidationErrors(e)))) }

  def error(errors: Seq[JsValue]) = Json.obj(ERRORS -> errors)

  def formatValidationErrors(jsError: JsError): Seq[JsValue] =
    jsError.errors
      .flatMap { errorWithPath => errorWithPath._2.map(validationErrorToJson(errorWithPath._1, _)) }

  def validationErrorToJson(jsPath: JsPath, validationError: JsonValidationError): JsValue = {
    val key   = jsPath.toString.substring(1)
    val value = messagesApi(validationError.message, validationError.args: _*)
    Json.obj(key -> value)
  }
}
