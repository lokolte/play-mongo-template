package controllers

import java.time.{Instant, LocalDate}
import java.util.UUID
import com.ww.models.{Credentials, Gender, PII, PhoneType, User}
import com.ww.requests.{PatchUserRequest, UserRequest}
import com.ww.services.UserService
import com.ww.services.UserService.{
  UserAlreadyExists,
  UserCreated,
  UserError,
  UserNotFound,
  UserResponse,
  UserSuccess
}
import com.ww.utils.Constants
import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.test._
import play.api.test.Helpers._
import org.scalatestplus.mockito.MockitoSugar
import scala.concurrent.ExecutionContext.Implicits.global
import org.mockito.Mockito._
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Result
import play.api.test.Helpers._
import scala.concurrent.Future
import org.mockito.ArgumentMatchers._
import org.scalatest.Assertion

class UsersControllerSpec
    extends PlaySpec
    with GuiceOneAppPerTest
    with Injecting
    with MockitoSugar {

  implicit val messagesAPI = stubMessagesApi()
  val mockUserService      = mock[UserService]
  val mockSecuredAction    = mock[SecuredAction]
  val controller =
    new UsersController(stubControllerComponents(), mockUserService, stubLangs(), mockSecuredAction)

  val registrationDate = Instant.now()
  val email            = "test@ww.com"
  val credentials      = Credentials(email, "secret")
  val pii = PII(
    title = Some("Batman"),
    firstName = "userFirstName",
    middleName = Some("userMiddleName"),
    lastName = Some("userLastName"),
    timeZone = "America/New_York",
    utcOffset = 1,
    gender = Gender.MALE,
    dob = Some(LocalDate.now()),
    phoneType = Some(PhoneType.HOME)
  )

  "UsersController GET" should {
    "return 400 if invalid email is passed" in {
      status(controller.get("wrongemail").apply(FakeRequest())) mustBe BAD_REQUEST
    }

    "return 404 if user not found" in {
      when(mockUserService.findByEmail(any[String])).thenReturn(Future(None))
      status(controller.get(email).apply((FakeRequest()))) mustBe NOT_FOUND
    }

    "return 200 if user is found" in {
      val user = User(UUID.randomUUID(), registrationDate, credentials, pii)
      when(mockUserService.findByEmail(any[String])).thenReturn(Future(Some(user)))
      val result = controller.get(email).apply((FakeRequest()))
      status(result) mustBe OK
      (contentAsJson(result) \ "data").as[User] mustBe user
    }
  }

  "UserController POST" should {
    "return 400 if request is invalid" in {
      implicit val materializer = app.materializer
      val userRequest           = UserRequest(credentials.copy(email = "wrongemail"), pii)
      val request               = fakeRequest(POST, "/users", Json.toJson(userRequest))
      val result                = controller.post(request)
      status(result) mustBe BAD_REQUEST
    }

    val fakeReq = fakeRequest(POST, "/users", Json.toJson(UserRequest(credentials, pii)))
    "return 409 if user already exists" in {
      testStatus(
        () => mockUserService.add(any[UserRequest]),
        () => controller.post(fakeReq),
        UserAlreadyExists,
        CONFLICT
      )
    }

    "return 201 if user gets created successfully" in {
      val uuid = UUID.randomUUID()
      testStatusAndResponse(
        () => mockUserService.add(any[UserRequest]),
        () => controller.post(fakeReq),
        (result) => (contentAsJson(result) \ "data" \ Constants.Fields.UUID).as[UUID] mustBe uuid,
        UserCreated(uuid),
        CREATED
      )
    }

    "return 500 if a server side error occurs" in {
      test500(
        () => mockUserService.add(any[UserRequest]),
        () => controller.post(fakeReq)
      )
    }
  }

  "UserController PUT" should {
    val fakeReq = fakeRequest(PUT, "/users", Json.toJson(UserRequest(credentials, pii)))
    "return 404 if user not found" in {
      testStatus(
        () => mockUserService.replace(any[String], any[UserRequest]),
        () => controller.put(email)(fakeReq)
      )
    }

    "return 200 if user updated successfully" in {
      test200(
        () => mockUserService.replace(any[String], any[UserRequest]),
        () => controller.put(email)(fakeReq)
      )
    }

    "return 500 if user update failed" in {
      test500(
        () => mockUserService.replace(any[String], any[UserRequest]),
        () => controller.put(email)(fakeReq)
      )
    }
  }

  "UserController PATCH" should {
    val fakeReq = fakeRequest(PATCH, "/users", Json.toJson(PatchUserRequest()))
    "return 500 if user update failed" in {
      test500(
        () => mockUserService.update(any[String], any[PatchUserRequest]),
        () => controller.patch(email)(fakeReq),
      )
    }

    "return 404 if user not found" in {
      testStatus(
        () => mockUserService.update(any[String], any[PatchUserRequest]),
        () => controller.patch(email)(fakeReq)
      )
    }

    "return 200 if user updated successfully" in {
      test200(
        () => mockUserService.update(any[String], any[PatchUserRequest]),
        () => controller.patch(email)(fakeReq)
      )
    }
  }

  def test500(mockBlock: () => Any, call: () => Future[Result]): Unit = {
    val assertion = (result: Future[Result]) =>
      ((contentAsJson(result) \ "errors")(0) \ "message").as[String] mustBe "failed"
    testStatusAndResponse(mockBlock, call, assertion, UserError("failed"), INTERNAL_SERVER_ERROR)
  }

  def test200(mockBlock: () => Any, call: () => Future[Result]): Unit = {
    val assertion = (result: Future[Result]) =>
      (contentAsJson(result) \ "data" \ "message").as[String] mustBe "success"
    testStatusAndResponse(mockBlock, call, assertion, UserSuccess("success"), OK)
  }

  def testStatusAndResponse(mockBlock: () => Any,
                            call: () => Future[Result],
                            assertion: (Future[Result]) => Assertion,
                            resp: UserResponse,
                            code: Int) = {
    implicit val materializer = app.materializer
    when(mockBlock()).thenReturn(Future(resp))
    val result = call()
    status(result) mustBe code
    assertion(result)
  }

  def testStatus(mockBlock: () => Any,
                 call: () => Future[Result],
                 resp: UserResponse = UserNotFound,
                 code: Int = NOT_FOUND) = {
    implicit val materializer = app.materializer
    when(mockBlock()).thenReturn(Future(resp))
    status(call()) mustBe code
  }

  def fakeRequest(httpVerb: String, path: String, json: JsValue): FakeRequest[JsValue] =
    FakeRequest(httpVerb, path)
      .withBody(json)
      .withHeaders((CONTENT_TYPE, JSON))
}
