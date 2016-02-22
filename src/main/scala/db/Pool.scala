package db

import akka.actor.ActorSystem
import com.github.mauricio.async.db.postgresql.pool.PostgreSQLConnectionFactory
import com.github.mauricio.async.db.pool.ConnectionPool
import com.github.mauricio.async.db.pool.PoolConfiguration
import com.github.mauricio.async.db.postgresql.util.URLParser
import utils.Config

import scala.concurrent.ExecutionContext

class Pool(implicit val executionContext: ExecutionContext, implicit val actorSystem: ActorSystem) extends Config {
  val configuration = URLParser.parse("jdbc:postgresql://localhost:5233/my_database?user=postgres&password=somepassword")
  val factory = new PostgreSQLConnectionFactory(configuration)
  val pool = new ConnectionPool(factory, new PoolConfiguration(dbPoolMaxObjects, dbPoolMaxIdle, dbPoolMaxQueueSize))
}
