package utils

import com.typesafe.config.ConfigFactory

trait Config {
  private val config = ConfigFactory.load()
  private val httpConfig = config.getConfig("http")
  private val databaseConfig = config.getConfig("database")

  val httpInterface = httpConfig.getString("interface")
  val httpPort = httpConfig.getInt("port")

  val databaseUrl = databaseConfig.getString("url")
  val databaseUser = databaseConfig.getString("user")
  val databasePassword = databaseConfig.getString("password")
  val databaseName = databaseConfig.getString("name")
  val databasePort = databaseConfig.getInt("port")
  val databaseHost = databaseConfig.getString("host")
  val databasePoolMaxObjects = databaseConfig.getInt("maxObjects")
  val databasePoolMaxIdle = databaseConfig.getInt("maxIdle")
  val databasePoolMaxQueueSize =  databaseConfig.getInt("maxQueueSize")
}
