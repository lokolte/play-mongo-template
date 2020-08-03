import play.api.http.HttpErrorHandler
import play.api.mvc._
import play.api.mvc.Results._

import scala.concurrent._
import javax.inject.Singleton
import play.api.libs.json.{JsObject, Json}

@Singleton
class ErrorHandler extends HttpErrorHandler {
  private def error(statusCode: Int, message: String) =
    Json.obj(
      "errors" -> Json.arr(
        Json.obj(
          "status"  -> statusCode,
          "message" -> message
        )
      )
    )

  def onClientError(request: RequestHeader, statusCode: Int, message: String): Future[Result] =
    Future.successful(BadRequest(error(statusCode, message)))

  def onServerError(request: RequestHeader, exception: Throwable): Future[Result] =
    Future.successful(InternalServerError(error(500, exception.getMessage)))
}
