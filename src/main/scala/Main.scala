import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.github.mauricio.async.db.pool.{ConnectionPool, PoolConfiguration}
import com.github.mauricio.async.db.postgresql.PostgreSQLConnection
import com.github.mauricio.async.db.postgresql.pool.PostgreSQLConnectionFactory
import com.github.mauricio.async.db.postgresql.util.URLParser
import com.github.mauricio.async.db.{Connection, RowData, QueryResult}
import com.typesafe.scalalogging.LazyLogging
import http.HttpService
import utils.{ Migration, Config }
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}


object Main extends LazyLogging with HttpService with App with Migration with Config {

  implicit val actorSystem = ActorSystem("akka-rest-api")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = actorSystem.dispatcher

  migrate()

  val configuration = URLParser.parse("jdbc:postgresql://localhost:5233/my_database?user=postgres&password=somepassword")
  val factory = new PostgreSQLConnectionFactory(configuration)
  val connectionPool = new ConnectionPool(factory, new PoolConfiguration(5, 5, 5))
  val future: Future[QueryResult] = connectionPool.sendQuery("SELECT 0")

  val mapResult: Future[Any] = future.map(queryResult => queryResult.rows match {
    case Some(resultSet) => {
      val row : RowData = resultSet.head
      row(0)
    }
    case None => -1
  }
  )

  val result = Await.result( mapResult, 5 seconds )

  println(result)

  connectionPool.disconnect


  val (interface, port) = (httpInterface, httpPort)
  val binding = Http().bindAndHandle(handler = allRoutes, interface = interface, port = port)
  logger.info(s"Bound to port $port on interface $interface")
  binding onFailure {
    case ex: Exception ⇒
      logger.error(s"Failed to bind to $interface:$port!", ex)
  }
  sys.addShutdownHook(actorSystem.terminate())
}

