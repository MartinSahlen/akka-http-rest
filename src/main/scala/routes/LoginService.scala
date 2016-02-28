package routes

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives
import com.typesafe.scalalogging.LazyLogging
import domain.LoginToken.generateLoginToken
import security.{LoginJsonFormatters, LoginRequest, LoginResponse}
import spray.json.{JsObject, JsString}

class LoginService extends LazyLogging with Directives with LoginJsonFormatters {

  val route = login

  def login = pathPrefix("login") {
    pathEndOrSingleSlash {
      post {
        entity(as[LoginRequest]) { request =>
          com.wix.accord.validate(request) match {
            case com.wix.accord.Success =>
              onSuccess(generateLoginToken(request.username, request.password)) {
                case Some(token) => complete(LoginResponse(token.token))
                case _ => complete(BadRequest, JsObject(Map("status" -> JsString("Wrong username or password"))))
              }
            case f@com.wix.accord.Failure(_) =>
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
