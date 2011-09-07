package com.twitter.querulous.database

import java.sql.{DriverManager, Driver}

class SingleConnectionDatabaseFactory extends DatabaseFactory {


  def apply(driver: String, url: String, username: String, password: String) = {
    Class.forName(driver)

    new Database {
      def open() = DriverManager.getConnection(url, username, password)
    }
  }
}

