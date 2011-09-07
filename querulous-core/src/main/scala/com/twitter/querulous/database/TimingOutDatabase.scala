package com.twitter.querulous.database

import com.twitter.querulous.{FutureTimeout, TimeoutException}
import java.sql.{Connection, SQLException}
import com.twitter.util.Duration


class SqlDatabaseTimeoutException(msg: String, val timeout: Duration) extends SQLException(msg)

class TimingOutDatabaseFactory(
  val databaseFactory: DatabaseFactory,
  val poolSize: Int,
  val queueSize: Int,
  val openTimeout: Duration)
extends DatabaseFactory {

  private def newTimeoutPool() = new FutureTimeout(poolSize, queueSize)

  def apply(driver: String, url: String, username: String, password: String) = {

    new TimingOutDatabase(
      databaseFactory(driver, url, username, password),
      newTimeoutPool(),
      openTimeout
    )
  }
}

class TimingOutDatabase(
  val database: Database,
  timeout: FutureTimeout,
  openTimeout: Duration)
extends Database
with DatabaseProxy {


  private def getConnection(wait: Duration) = {
    try {
      timeout(wait) {
        database.open()
      } { conn =>
        database.close(conn)
      }
    } catch {
      case e: TimeoutException =>
        throw new SqlDatabaseTimeoutException(database.url, wait)
    }
  }

  override def open() = getConnection(openTimeout)

  override def close(connection: Connection) { database.close(connection) }
}
