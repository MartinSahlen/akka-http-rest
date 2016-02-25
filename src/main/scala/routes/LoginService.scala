package routes

import javax.ws.rs.Path

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives
import com.typesafe.scalalogging.LazyLogging
import domain.{UserRepo, User, PostRequest, PostResponse}
import io.swagger.annotations._
import json.JsonSupport
import security.Authentication
import spray.json.{JsNumber, JsObject, JsString}

import scala.util.{Failure, Success}

@Api(value = "/hello", produces = "application/json", consumes = "application/json")
class LoginService extends LazyLogging with Directives with JsonSupport {

  val route = login ~ addUser ~ getUsers

  val userRepo = new UserRepo()

  def addUser = path("adduser") {
    pathEndOrSingleSlash {
      get {
        onComplete(userRepo.doSomeStuff) {
          case Success(data) => complete(data.toString)
          case Failure(ex) => complete(ex.toString)
        }
      }
    }
  }

  def getUsers = path("getusers") {
    pathEndOrSingleSlash {
      get {
        onComplete(userRepo.getAllUsers) {
          case Success(users) => complete(users)
          case Failure(ex) => complete(ex.toString)
        }
      }
    }
  }

  @ApiOperation(value = "Login", notes = "", nickname = "Login", httpMethod = "POST")
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Return Login Response", response = classOf[PostResponse]),
    new ApiResponse(code = 403, message = "Bad username")
  ))
  @Path("/login")
  def login = path("login") {
    pathEndOrSingleSlash {
      post {
        authenticateBasic[String]("martin", Authentication.basicAuth) { username ⇒
          logger.info(s"$username successfully logged in!")
          entity(as[PostRequest]) { request =>
            com.wix.accord.validate(request) match {
              case com.wix.accord.Success => complete(PostResponse(s"${request.clientName}"))
              case f@com.wix.accord.Failure(_) => {
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
