package com.twitter.querulous.database

import com.twitter.util.Duration
import java.sql.{SQLException, Connection}

object Database {
  //TODO: allow passing this via setup configuration
  private[querulous] var driverName: String = "jdbc:mysql"

  val defaultUrlOptions = Map(
    "useUnicode" -> "true",
    "characterEncoding" -> "UTF-8",
    "connectTimeout" -> "100"
  )
}

trait DatabaseFactory {
  def apply(driver: String, url: String, username: String, password: String): Database

}


trait DatabaseProxy extends Database {
  def database: Database

  def url             = database.url
  def driver          = database.driver
  def username        = database.username


  def getInnermostDatabase(): Database = {
    database match {
      case dbProxy: DatabaseProxy => dbProxy.getInnermostDatabase()
      case db: Database => db
    }
  }
}

trait Database {
  def driver: String

  def url: String

  def username: String

  def open(): Connection

  def close(connection: Connection) {
    try {
      connection.close()
    } catch {
      case _: SQLException =>
    }
  }

  def withConnection[A](f: Connection => A): A = {
    val connection = open()
    try {
      f(connection)
    } finally {
      close(connection)
    }
  }

  protected[database] def getGauges: Seq[(String, ()=>Double)] = {
    List.empty
  }

}