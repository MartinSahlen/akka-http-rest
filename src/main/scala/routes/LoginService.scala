package routes

import javax.ws.rs.Path

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives
import com.typesafe.scalalogging.LazyLogging
import domain.{LoginToken, PostResponse}
import LoginToken.generateLoginToken
import io.swagger.annotations._
import security.{LoginFormatters, LoginRequest, LoginResponse}
import spray.json.{JsObject, JsString}

@Api(value = "Login service", produces = "application/json", consumes = "application/json")
class LoginService extends LazyLogging with Directives with LoginFormatters {

  val route = login

  @ApiOperation(value = "Login", notes = "", nickname = "Login", httpMethod = "POST")
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Return Login Response", response = classOf[PostResponse]),
    new ApiResponse(code = 403, message = "Bad username")
  ))
  @Path("/login")
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
