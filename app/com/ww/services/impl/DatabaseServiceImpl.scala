package com.ww.services.impl

import akka.actor.ActorSystem
import com.ww.services.DatabaseService
import javax.inject.{Inject, Singleton}
import reactivemongo.api.{AsyncDriver, DefaultDB, MongoConnection}
import reactivemongo.api.bson.collection.BSONCollection

import scala.concurrent.Future
import play.api.Configuration

import scala.util.Try

@Singleton
class DatabaseServiceImpl @Inject()(conf: Configuration, system: ActorSystem)
    extends DatabaseService {
  implicit val myExecutionContext = system.dispatchers.lookup("mongodb.context")
  val mongoUri                    = conf.get[String]("mongodb.uri")

  // Connect to the database: Must be done only once per application
  val driver                                 = AsyncDriver()
  val parsedUri                              = MongoConnection fromString mongoUri
  val futureConnection                       = parsedUri.flatMap(driver.connect)
  def db1: Future[DefaultDB]                 = futureConnection.flatMap(_.database("test"))
  override val USERS: Future[BSONCollection] = db1.map(_.collection("users"))

}
