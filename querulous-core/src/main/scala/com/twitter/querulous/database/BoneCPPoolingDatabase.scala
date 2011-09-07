package com.twitter.querulous.database

/*
 * Copyright (c) 2011 Denis Bardadym
 * Distributed under Apache License.
 */

import com.jolbox.bonecp.{BoneCP, BoneCPConfig}


class BoneCPPoolingDatabaseFactory(partitionCount: Int, maxConnectionsPerPartition: Int, minConnectionsPerPartition: Int, acquireIncrement: Int) extends DatabaseFactory {
  def apply(driver: String, url: String, username: String, password: String) =


    new BoneCPPoolingDatabase(driver, url, username, password, partitionCount, maxConnectionsPerPartition, minConnectionsPerPartition, acquireIncrement)

}

class BoneCPPoolingDatabase(
                             val driver: String,
                             val url: String,
                             val username: String,
                             password: String,
                             partitionCount: Int,
                             maxConnectionsPerPartition: Int,
                             minConnectionsPerPartition: Int,
                             acquireIncrement: Int) extends Database {
  Class.forName(driver)

  private val config = new BoneCPConfig
  config.setJdbcUrl(url)
  config.setUsername(username)
  config.setPassword(password)
  config.setMinConnectionsPerPartition(minConnectionsPerPartition)
  config.setMaxConnectionsPerPartition(maxConnectionsPerPartition)
  config.setPartitionCount(partitionCount)
  config.setAcquireIncrement(acquireIncrement)

  private val connectionPool = new BoneCP(config);

  def open() = connectionPool.getConnection();

  override def finalize() {
    connectionPool.shutdown()
  }
}