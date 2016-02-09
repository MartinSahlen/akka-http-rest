import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.typesafe.scalalogging.LazyLogging
import http.HttpService
import utils.{ Migration, Config }


object Main extends LazyLogging with HttpService with App with Migration with Config {

  implicit val actorSystem = ActorSystem("akka-rest-api")
  implicit val materializer = ActorMaterializer()
  implicit val executionContect = actorSystem.dispatcher

  migrate()

  val (interface, port) = (httpInterface, httpPort)
  val binding = Http().bindAndHandle(handler = routes, interface = interface, port = port)
  logger.info(s"Bound to port $port on interface $interface")
  binding onFailure {
    case ex: Exception ⇒
      logger.error(s"Failed to bind to $interface:$port!", ex)
  }
}

