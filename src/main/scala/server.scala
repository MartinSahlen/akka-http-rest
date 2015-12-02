import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server._
import akka.http.scaladsl.server.directives.Credentials
import akka.http.scaladsl.server.directives.Credentials.Provided
import akka.stream.ActorMaterializer
import com.typesafe.scalalogging.LazyLogging
import com.wix.accord._
import com.wix.accord.dsl._
import spray.json._

import scala.util.Properties

// Request domain objects

case class PostResponse(status: String)
case class PostRequest(clientName: String)

object PostRequest {
  implicit val postRequestValidation = validator[PostRequest] { p =>
    p.clientName  as "client name length" is notEmpty
    p.clientName.length() as "clientName:length" should be > 5
    p.clientName as "clientName:prefix" should startWith("martin")
  }
}

// Request domain object serializers

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val StartProcessingResultFormat = jsonFormat1(PostResponse)

  val CLIENT_NAME_JSON_FIELD = "client_name"

  implicit object StartProcessingRequestFormat extends RootJsonFormat[PostRequest] {
    def write(request: PostRequest) = JsObject(
      CLIENT_NAME_JSON_FIELD  -> JsString(request.clientName)
    )
    def read(value: JsValue) = {
      value.asJsObject.getFields(CLIENT_NAME_JSON_FIELD) match {
        case Seq(JsString(clientName)) =>
          new PostRequest(clientName)
        case wat =>
          val message = s"Could not deserialize object, got missing / unknown fields, null values and / or wrong types:"
          deserializationError(
            message,
            UnknownFieldsException(message, List(CLIENT_NAME_JSON_FIELD), value.asJsObject.fields.keys.toList))
      }
    }
  } // LIST of gotten fields, + LIST of valid fields
}

case class UnknownFieldsException(message:String, fields:List[String], fieldsReceived:List[String]) extends Throwable

case class UnknownFieldsErrorMessage(message: String, fields:List[String], fieldsReceived:List[String])
object UnknownFieldsErrorMessage {
  import spray.json.DefaultJsonProtocol._
  implicit val errorFormat = jsonFormat3(UnknownFieldsErrorMessage.apply)
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

  val myRejectionHandler =
    RejectionHandler.newBuilder()
      .handle { case MalformedRequestContentRejection(msg, Some(UnknownFieldsException(message, fields, fieldsReceived))) =>
        complete(BadRequest, UnknownFieldsErrorMessage(message, fields, fieldsReceived))
      }
      .handle { case MalformedRequestContentRejection(msg, e) =>
        println(e)
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
       (pathPrefix("login") & pathEndOrSingleSlash) {
        post {
          authenticateBasic[String]("martin", basicAuth) { username ⇒
            logger.info(s"$username successfully logged in!")
            entity(as[PostRequest]) { request =>
              com.wix.accord.validate(request) match {
                case com.wix.accord.Success => complete(PostResponse(s"${request.clientName}"))
                case f@Failure(_) => {
                  complete(BadRequest, for { v <- f.violations } yield {
                    JsObject(Map("error" -> JsString(v.constraint),
                      "description" -> JsString(v.description.getOrElse("")),
                      "value" -> JsString(v.value.toString)
                    ))
                  })
                }
              }
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


