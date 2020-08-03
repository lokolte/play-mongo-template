package com.ww.services.impl

import java.time.Instant
import java.util.UUID
import com.ww.models.User
import com.ww.requests.{PatchUserRequest, UserRequest}
import com.ww.services.{DatabaseService, UserService}
import com.ww.services.UserService._
import javax.inject.{Inject, Singleton}
import reactivemongo.api.bson.{BSONDocument, document}
import scala.concurrent.{ExecutionContext, Future}
import reactivemongo.api.commands.{UpdateWriteResult, WriteResult}
import com.ww.utils.Constants.Fields._
import com.ww.utils.MongoReadsWrites._

@Singleton
class UserServiceImpl @Inject()(dbService: DatabaseService)(implicit ec: ExecutionContext)
    extends UserService {

  // We can also use the "setOnInsert" to perform a safe insert like this
  //  override def add(req: UserRequest): Future[UserResponse] = {
  //    val uuid = UUID.randomUUID()
  //    dbService.USERS.flatMap(
  //      _.update
  //        .one(
  //          document(CREDENTIALS_EMAIL -> req.credentials.email),
  //          document("$setOnInsert"    -> User(uuid, Instant.now, req.credentials, req.pii)),
  //          upsert = true
  //        )
  //        .map { res =>
  //          if (res.ok) {
  //            if (!res.upserted.isEmpty) UserCreated(uuid)
  //            else UserAlreadyExists
  //          } else UserError(res.writeErrors.mkString(","))
  //        }
  //    )
  //  }

  override def add(req: UserRequest): Future[UserResponse] =
    findByEmail(req.credentials.email).flatMap {
      case None => insertUser(User(UUID.randomUUID, Instant.now, req.credentials, req.pii))
      case _    => Future(UserAlreadyExists)
    }

  override def replace(email: String, req: UserRequest): Future[UserResponse] =
    dbService.USERS.flatMap(
      _.update
        .one(document(CREDENTIALS_EMAIL -> email),
             document(
               "$set" -> document(PII -> req.pii, CREDENTIALS_PASSWORD -> req.credentials.password)
             ))
        .map(userUpdateHandler)
    )

  override def update(email: String, req: PatchUserRequest): Future[UserResponse] =
    dbService.USERS.flatMap(
      _.update
        .one(document(CREDENTIALS_EMAIL -> email), document("$set" -> getPatchUpdateQuery(req)))
        .map(userUpdateHandler)
    )

  override def findByEmail(email: String): Future[Option[User]] =
    findOneUser(document(CREDENTIALS_EMAIL -> email))

  override def findByEmailAndPassword(email: String, password: String): Future[Option[User]] =
    findOneUser(document(CREDENTIALS_EMAIL -> email, CREDENTIALS_PASSWORD -> password))

  def findOneUser(query: BSONDocument): Future[Option[User]] =
    dbService.USERS
      .flatMap(
        _.find[BSONDocument, BSONDocument](
          document(query),
          None
        ).one[User]
      )

  def getPatchUpdateQuery(req: PatchUserRequest): BSONDocument = {
    var updateQuery = document
    updateQuery ++= req.title.map(t => document(PII_TITLE               -> t)).getOrElse(document)
    updateQuery ++= req.password.map(p => document(CREDENTIALS_PASSWORD -> p)).getOrElse(document)
    updateQuery ++= req.firstName.map(f => document(PII_FIRST_NAME      -> f)).getOrElse(document)
    updateQuery ++= req.lastName.map(l => document(PII_LAST_NAME        -> l)).getOrElse(document)
    updateQuery ++= req.middleName.map(m => document(PII_MIDDLE_NAME    -> m)).getOrElse(document)
    updateQuery ++= req.timeZone.map(t => document(PII_TIMEZONE         -> t)).getOrElse(document)
    updateQuery ++= req.utcOffset.map(u => document(PII_UTC_OFFSET      -> u)).getOrElse(document)
    updateQuery ++= req.dob.map(d => document(PII_DOB                   -> d)).getOrElse(document)
    updateQuery ++= req.gender.map(g => document(PII_GENDER             -> g)).getOrElse(document)
    updateQuery ++= req.phoneType.map(p => document(PII_PHONE_TYPE      -> p)).getOrElse(document)
    updateQuery
  }

  def userUpdateHandler(res: WriteResult): UserResponse = {
    if (res.n == 0)
      UserNotFound
    else if (res.n > 0)
      UserSuccess("successfully updated.")
    else
      UserError(
        s"[writeErrors]=${res.writeErrors
          .mkString(",")}, [writeConcernError]=${res.writeConcernError}, [code]=${res.code}"
      )
  }

  def insertUser(user: User): Future[UserResponse] =
    dbService.USERS.flatMap(
      _.insert
        .one(user)
        .map(insertWriteResultHandler(user.uuid))
    )

  def insertWriteResultHandler(uuid: UUID)(wr: WriteResult): UserResponse =
    if (wr.ok) UserCreated(uuid)
    else UserError(wr.writeErrors.toList.mkString(","))

}
