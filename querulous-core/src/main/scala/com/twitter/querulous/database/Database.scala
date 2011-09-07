package com.twitter.querulous.database

import java.sql.{SQLException, Connection}


trait DatabaseFactory {
  def apply(driver: String, url: String, username: String, password: String): Database

}

trait Database {
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


}