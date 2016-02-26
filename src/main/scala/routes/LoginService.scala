package routes

import javax.ws.rs.Path

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.{ValidationRejection, Directives}
import com.typesafe.scalalogging.LazyLogging
import domain.{UserRepo, User, PostRequest, PostResponse}
import io.swagger.annotations._
import json.JsonSupport
import security.Authentication
import spray.json.{JsString, JsNumber, JsObject}

import scala.util.{Failure, Success}

@Api(value = "/hello", produces = "application/json", consumes = "application/json")
class LoginService extends LazyLogging with Directives with JsonSupport {

  val route = login ~ addUser ~ getUsers

  val userRepo = new UserRepo()

  def addUser = path("adduser") {
    pathEndOrSingleSlash {
      get {
        complete(userRepo.doSomeStuff)
      }
    }
  }

  def extractAuthHeader = extractRequest.tflatMap[Tuple1[String]] {
    case Tuple1(request) =>
      request.headers.find(h => h.name == "Authorization") match {
        case Some(authHeader) =>
          provide(authHeader.value)
        case _ =>
          complete(Unauthorized,  JsObject(Map("status" -> JsString("Missing Authorization header"))))
      }
  }

  def getUsers = path("getusers") {
    pathEndOrSingleSlash {
      get {
        extractAuthHeader { authHeader =>
          logger.info(authHeader)
          complete(userRepo.getAllUsers)
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
        authenticateBasic[String]("martin", Authentication.basicAuth) { username =>
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
