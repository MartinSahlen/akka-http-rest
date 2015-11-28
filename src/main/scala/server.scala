import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.http.scaladsl.server.directives.Credentials
import akka.http.scaladsl.server.directives.Credentials.Provided
import akka.stream.ActorMaterializer
import com.typesafe.scalalogging.LazyLogging
import spray.json._
import scala.util.Properties
import StatusCodes._


// Request domain objects

case class PostResponse(status: String)
case class PostRequest(clientName: String)

// Request domain object serializers

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val StartProcessingResultFormat = jsonFormat1(PostResponse)

  val CLIENT_NAME_JSON_FIELD = "client_name"

  implicit object StartProcessingRequestFormat extends RootJsonFormat[PostRequest] {
    def write(request: PostRequest) = JsObject(
      CLIENT_NAME_JSON_FIELD  → JsString(request.clientName)
    )
    def read(value: JsValue) = {
      value.asJsObject.getFields(CLIENT_NAME_JSON_FIELD) match {
        case Seq(JsString(clientName)) ⇒
          new PostRequest(clientName)
        case _ ⇒ deserializationError("could not deserialize JSON to valid object. valid fields: " + CLIENT_NAME_JSON_FIELD +
        ". Got fields: " + value.asJsObject.fields.keys.mkString(",")
        )
      }
    }
  } // LIST of gotten fields, + LIST of valid fields
}

case class ErrorMessage(message: String)
object ErrorMessage {
  import spray.json.DefaultJsonProtocol._
  implicit val errorFormat = jsonFormat1(ErrorMessage.apply)
}


object Server extends LazyLogging with Directives with JsonSupport with App {

  implicit val actorSystem = ActorSystem("akka-rest-api")
  implicit val materializer = ActorMaterializer()
  implicit val executionContect = actorSystem.dispatcher

  def basicAuth(credentials: Credentials): Option[String] = {
    credentials match {
      case p@Provided(username) if p.verify("password") => Option(username)
      case _                                            => logger.info("You shall not pass!"); Option.empty
    }
  }

   def myRejectionHandler =
    RejectionHandler.newBuilder()
      .handle { case MalformedRequestContentRejection(msg , _) =>
        complete(BadRequest, ErrorMessage(msg))
      }
      .handle { case UnsupportedRequestContentTypeRejection(supported) =>
        complete(BadRequest, ErrorMessage("Unsupported content type in request. supported: " + supported.mkString(",")))
      }
      .handleAll[MethodRejection] { methodRejections ⇒
        val names = methodRejections.map(_.supported.name)
        complete(MethodNotAllowed, ErrorMessage(s"Method not allowed. Supported methods: ${names mkString " or "}"))
      }
      .handleNotFound { complete(NotFound, ErrorMessage("Not found")) }
      .result()

  val route =
    handleRejections(myRejectionHandler) {
      path("login") {
        post {
          authenticateBasic[String]("martin", basicAuth) { username ⇒
            logger.info(s"$username successfully logged in!")
            entity(as[PostRequest]) { request =>
              complete(PostResponse(s"${request.clientName}"))
            }
          }
        }
      }
    }

  val (interface, port) = ("0.0.0.0", Properties.envOrElse("PORT", "8080").toInt)
  val binding = Http().bindAndHandle(handler = route, interface = interface, port = port)
  logger.info(s"Bound to port $port on interface $interface")
  binding onFailure {
    case ex: Exception ⇒
      logger.error(s"Failed to bind to $interface:$port!", ex)
  }
}


