package com.twitter.querulous.database

import com.twitter.querulous.AutoDisabler
import com.twitter.util.Duration
import java.sql.{Connection, SQLException}


class AutoDisablingDatabaseFactory(val databaseFactory: DatabaseFactory, val disableErrorCount: Int, val disableDuration: Duration) extends DatabaseFactory {
  def apply(driver: String, url: String, username: String, password: String) = {
    new AutoDisablingDatabase(
      databaseFactory(driver, url, username, password),
      disableErrorCount,
      disableDuration)
  }
}

class AutoDisablingDatabase(
  val database: Database,
  protected val disableErrorCount: Int,
  protected val disableDuration: Duration)
extends Database
with DatabaseProxy
with AutoDisabler {
  def open() = {
    throwIfDisabled(url)
    try {
      val rv = database.open()
      noteOperationOutcome(true)
      rv
    } catch {
      case e: SQLException =>
        noteOperationOutcome(false)
        throw e
      case e: Exception =>
        throw e
    }
  }

  override def close(connection: Connection) { database.close(connection) }
}
