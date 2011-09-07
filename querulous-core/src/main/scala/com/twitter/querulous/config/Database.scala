package com.twitter.querulous.config

import com.twitter.querulous._
import com.twitter.util.Duration
import com.twitter.conversions.time._
import database._


trait PoolingDatabase {
  def apply(): DatabaseFactory
}

trait ServiceNameTagged {
  def apply(serviceName: Option[String]): DatabaseFactory
}

class ApachePoolingDatabase extends PoolingDatabase {
  var sizeMin: Int = 10
  var sizeMax: Int = 10
  var testIdle: Duration = 1.second
  var maxWait: Duration = 10.millis
  var minEvictableIdle: Duration = 60.seconds
  var testOnBorrow: Boolean = false

  def apply() = {
    new ApachePoolingDatabaseFactory(
      sizeMin, sizeMax, testIdle, maxWait, testOnBorrow, minEvictableIdle)
  }
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

class ThrottledPoolingDatabase extends PoolingDatabase with ServiceNameTagged {
  var size: Int = 10
  var openTimeout: Duration = 50.millis
  var repopulateInterval: Duration = 500.millis
  var idleTimeout: Duration = 1.minute

  def apply() = {
    apply(None)
  }

  def apply(serviceName: Option[String]) = {
    new ThrottledPoolingDatabaseFactory(
      serviceName, size, openTimeout, idleTimeout, repopulateInterval, Map.empty)
  }
}

class TimingOutDatabase {
  var poolSize: Int = 10
  var queueSize: Int = 10000
  var open: Duration = 1.second

  def apply(factory: DatabaseFactory) = {
    new TimingOutDatabaseFactory(factory, poolSize, queueSize, open)
  }
}

trait AutoDisablingDatabase {
  def errorCount: Int
  def interval: Duration

  def apply(factory: DatabaseFactory) = {
    new AutoDisablingDatabaseFactory(factory, errorCount, interval)
  }
}



trait Connection {
  def url: String

  def driver: String

  def username: String

  def password: String
}
