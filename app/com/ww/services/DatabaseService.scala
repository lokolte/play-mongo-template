package com.ww.services

import com.google.inject.ImplementedBy
import com.ww.services.impl.DatabaseServiceImpl
import reactivemongo.api.bson.collection.BSONCollection

import scala.concurrent.Future

@ImplementedBy(classOf[DatabaseServiceImpl])
trait DatabaseService {
  val USERS: Future[BSONCollection]
}
