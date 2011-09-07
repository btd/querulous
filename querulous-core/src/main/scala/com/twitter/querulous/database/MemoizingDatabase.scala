package com.twitter.querulous.database

import scala.collection.mutable

class MemoizingDatabaseFactory(val databaseFactory: DatabaseFactory) extends DatabaseFactory {
  private val databases = new mutable.HashMap[String, Database] with mutable.SynchronizedMap[String, Database]

  def apply(driver: String, url: String, username: String, password: String) = synchronized {
    databases.getOrElseUpdate(
      url,
      databaseFactory(driver, url, username, password))
  }
}
