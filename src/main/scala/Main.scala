import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.github.mauricio.async.db.{RowData, QueryResult}
import com.typesafe.scalalogging.LazyLogging
import db.DB
import http.HttpService
import utils.{ Migration, Config }

import scala.concurrent.Future


object Main extends LazyLogging with HttpService with App with Migration with DB with Config {

  implicit val actorSystem = ActorSystem("akka-rest-api")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = actorSystem.dispatcher

  migrate()

  val future: Future[Option[Seq[RowData]]] = fetch("SELECT 0").map {
    case Some(_) => {
      print("lol")
      Some(null)
    }
  }

  val (interface, port) = (httpInterface, httpPort)
  val binding = Http().bindAndHandle(handler = allRoutes, interface = interface, port = port)
  logger.info(s"Bound to port $port on interface $interface")
  binding onFailure {
    case ex: Exception ⇒
      logger.error(s"Failed to bind to $interface:$port!", ex)
  }
  sys.addShutdownHook(actorSystem.terminate())
}

