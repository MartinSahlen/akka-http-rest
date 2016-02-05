import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server._
import akka.stream.ActorMaterializer
import com.typesafe.scalalogging.LazyLogging
import json.JsonSupport
import rejection.Rejection
import routes.Login
import scala.util.Properties

object Server extends LazyLogging with Directives with JsonSupport with App {

  implicit val actorSystem = ActorSystem("akka-rest-api")
  implicit val materializer = ActorMaterializer()
  implicit val executionContect = actorSystem.dispatcher
  val (interface, port) = ("0.0.0.0", Properties.envOrElse("PORT", "8080").toInt)

  var routes = handleRejections(Rejection.myRejectionHandler)(Login.route)

  val binding = Http().bindAndHandle(handler = routes, interface = interface, port = port)
  logger.info(s"Bound to port $port on interface $interface")
  binding onFailure {
    case ex: Exception ⇒
      logger.error(s"Failed to bind to $interface:$port!", ex)
  }
}

