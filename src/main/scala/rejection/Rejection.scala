package rejection

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server._
import com.typesafe.scalalogging.LazyLogging
import exception.{ErrorMessage, UnknownFieldsErrorMessage, UnknownFieldsException}
import json.JsonSupport

object Rejection extends LazyLogging with Directives with JsonSupport {

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
      .handle { case UnsupportedRequestContentTypeRejection(supported) =>
        complete(BadRequest, ErrorMessage("Unsupported content type in request. supported: " + supported.mkString(",")))
      }
      .handleAll[AuthenticationFailedRejection] { message =>
      logger.info(message.toString)
      complete(Unauthorized, ErrorMessage("Autentication failed."))
    }
      .handleAll[MethodRejection] { methodRejections ⇒
      val names = methodRejections.map(_.supported.name)
      complete(MethodNotAllowed, ErrorMessage(s"Method not allowed. Supported methods: ${names mkString " or "}"))
    }
      .handleNotFound { complete(NotFound, ErrorMessage("Not found")) }
      .result()

}
