package controllers

import com.ww.models.User
import com.ww.services.{AuthService, UserService}
import javax.inject.Inject
import play.api.mvc._
import play.api.libs.json._
import scala.concurrent.Future
import play.api.mvc.Results._
import scala.concurrent._
import scala.util.{Failure, Success}

class AuthenticatedRequest[A](val user: User, val request: Request[A])
    extends WrappedRequest[A](request)

class SecuredAction @Inject()(val parser: BodyParsers.Default,
                              authService: AuthService,
                              userService: UserService)(
    implicit val executionContext: ExecutionContext
) extends ActionBuilder[AuthenticatedRequest, AnyContent] {

  override def invokeBlock[A](request: Request[A],
                              block: (AuthenticatedRequest[A]) => Future[Result]) =
    authService
      .decryptToken(request.headers.get("JWT_TOKEN").getOrElse(""))
      .flatMap {
        case Success(payload) => {
          val email = (Json.parse(payload) \ "payload" \ "email").asOpt[String].getOrElse("")
          userService
            .findByEmail(email)
            .flatMap(
              _.fold(Future.successful(Unauthorized(s"User doesn't exist.")))(
                user => block(new AuthenticatedRequest(user, request))
              )
            )
        }
        case Failure(exp) => Future.successful(Unauthorized(exp.getMessage))
      }
}
