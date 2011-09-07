package com.twitter.querulous.database

import org.apache.commons.dbcp.DriverManagerConnectionFactory
import java.sql.{SQLException, Connection}
import com.twitter.conversions.time._

class SingleConnectionDatabaseFactory(defaultUrlOptions: Map[String, String]) extends DatabaseFactory {
  def this() = this(Map.empty)

  def apply(driver: String, url: String, username: String, password: String) = {


    new SingleConnectionDatabase(driver, url, username, password)
  }
}

class SingleConnectionDatabase(
  val driver: String,
  val url: String,
  val username: String,
  password: String)
extends Database {
  Class.forName(driver)
  private val connectionFactory = new DriverManagerConnectionFactory(url, username, password)

  override def close(connection: Connection) {
    try {
      connection.close()
    } catch {
      case _: SQLException =>
    }
  }

  def open() = connectionFactory.createConnection()
  override def toString = url
}
