package controllers

import com.ww.requests.{PatchUserRequest, UserRequest}
import com.ww.services.UserService
import com.ww.services.UserService._
import javax.inject.{Inject, Singleton}
import play.api.libs.json.Json
import com.ww.models.User.userJsonWrites
import play.api.mvc.Result
import com.ww.utils.RequestValidators.isValidEmail
import com.ww.utils.Constants.Fields.{UUID, EMAIL}
import com.ww.utils.Constants.MessageKeys.{USER_ALREADY_EXISTS, INVALID_EMAIL}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UsersController @Inject() (userService: UserService, securedAction: SecuredAction)(
    implicit ec: ExecutionContext
) extends WWBaseController {

  def post = Action.async(parse.json) { implicit request =>
    handle[UserRequest](userService.add(_).map(userResponseHandler))
  }

  def put(email: String) = Action.async(parse.json) { implicit request =>
    handle[UserRequest](userService.replace(email, _).map(userResponseHandler))
  }

  def patch(email: String) = Action.async(parse.json) { implicit request =>
    handle[PatchUserRequest](userService.update(email, _).map(userResponseHandler))
  }

  def get(email: String) = Action.async {
    if (isValidEmail(email)) {
      userService.findByEmail(email).map {
        case Some(u) => Ok(success(Json.toJson(u)))
        case None    => NotFound
      }
    } else Future(BadRequest(error(Json.obj(EMAIL -> INVALID_EMAIL))))
  }

  def test = securedAction { implicit request => Ok(success(Json.toJson(request.user))) }

  def userResponseHandler(res: UserResponse): Result = res match {
    case UserNotFound      => NotFound
    case UserCreated(uuid) => Created(success(Json.obj(UUID -> uuid.toString)))
    case UserAlreadyExists => Conflict(error(messagesApi(USER_ALREADY_EXISTS)))
    case UserError(msg)    => InternalServerError(error(msg))
    case UserSuccess(msg)  => Ok(success(msg))
  }
}
