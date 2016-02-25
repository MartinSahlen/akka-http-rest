package db

import com.github.mauricio.async.db.Configuration
import com.github.mauricio.async.db.pool.ConnectionPool
import com.github.mauricio.async.db.pool.PoolConfiguration
import com.github.mauricio.async.db.postgresql.pool.PostgreSQLConnectionFactory
import utils.Config

class Pool extends Config {

  val configuration = new Configuration(username = databaseUser,
    port = databasePort,
    host = databaseHost,
    password = Some(databasePassword),
    database = Some(databaseName))

  val factory = new PostgreSQLConnectionFactory(configuration)

  val pool = new ConnectionPool(factory, new PoolConfiguration(
    databasePoolMaxObjects,
    databasePoolMaxIdle,
    databasePoolMaxQueueSize))
}
