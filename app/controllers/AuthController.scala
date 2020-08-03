package controllers

import com.ww.models.Credentials
import com.ww.services.AuthService
import javax.inject.{Inject, Singleton}
import play.api.libs.json.Json
import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

@Singleton
class AuthController @Inject() (authService: AuthService)(implicit ec: ExecutionContext)
    extends WWBaseController {

  def post = Action.async(parse.json) { implicit request =>
    handle[Credentials](
      authService
        .createToken(_)
        .map({
          case Success(token) => Created(success(Json.obj("token" -> token)))
          case Failure(exp)   => NotFound(error(exp.getMessage))
        })
    )
  }

}
