package routes

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives
import com.typesafe.scalalogging.LazyLogging
import com.wix.accord.Failure
import domain.{PostResponse, PostRequest}
import json.JsonSupport
import spray.json.{JsString, JsObject}
import rejection.Rejection
import security.Authentication

object Login extends LazyLogging with Directives with JsonSupport  {
  val route =
    handleRejections(Rejection.myRejectionHandler) {
      (pathPrefix("login") & pathEndOrSingleSlash) {
        post {
          authenticateBasic[String]("martin", Authentication.basicAuth) { username ⇒
            logger.info(s"$username successfully logged in!")
            entity(as[PostRequest]) { request =>
              com.wix.accord.validate(request) match {
                case com.wix.accord.Success => complete(PostResponse(s"${request.clientName}"))
                case f@Failure(_) => {
                  complete(BadRequest, for {v <- f.violations} yield {
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
}
