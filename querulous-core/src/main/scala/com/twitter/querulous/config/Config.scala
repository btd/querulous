package com.twitter.querulous.config

import com.twitter.querulous._
import database._


trait PoolingDatabase {
  def apply(): DatabaseFactory
}


class BoneCPPoolingDatabase extends PoolingDatabase {
  var partitionCount: Int = 2
  var maxConnectionsPerPartition: Int = 5
  var minConnectionsPerPartition: Int = 1
  var acquireIncrement: Int = 2

  def apply() =
    new BoneCPPoolingDatabaseFactory(
      partitionCount, maxConnectionsPerPartition, minConnectionsPerPartition, acquireIncrement)

}


trait ConnectionConfig {
  def url: String

  def driver: String

  def username: String

  def password: String
}
