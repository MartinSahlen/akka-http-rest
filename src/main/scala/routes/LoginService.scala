package routes

import javax.ws.rs.Path

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives
import com.github.mauricio.async.db.QueryResult
import com.typesafe.scalalogging.LazyLogging
import db.DB
import domain.{PostRequest, PostResponse}
import io.swagger.annotations._
import json.JsonSupport
import security.Authentication
import spray.json.{JsNumber, JsString, JsObject}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}


@Api(value = "/hello", produces = "application/json", consumes = "application/json")
class LoginService(implicit val executionContext: ExecutionContext = global) extends LazyLogging with Directives with JsonSupport with DB {

  val route = login ~ addUser ~ getUsers

  def doSomeStuff: Future[QueryResult] = {
    execute("INSERT INTO users (username, password) VALUES (?, ?)", "martin", "password")
  }

  def addUser = path("adduser") {
    pathEndOrSingleSlash {
      get {
        onComplete(doSomeStuff) {
          case Success(data) => complete(data.toString)
          case Failure(ex) => complete(ex.toString)
        }
      }
    }
  }

  def getSomeStuff: Future[QueryResult] = {
    execute("SELECT * FROM users")
  }

  def getUsers = path("getusers") {
    pathEndOrSingleSlash {
      get {
        onComplete(getSomeStuff) {
          case Success(data) => {
            data.rows match {
              case Some(resultSet) => complete(OK, for {r <- resultSet} yield {
                JsObject(Map("id" -> JsNumber(r.apply("id").asInstanceOf[Long]),
                  "username" -> JsString(r.apply("username").asInstanceOf[String]),
                  "password" -> JsString(r.apply("password").asInstanceOf[String])
                ))
              })
              case _ => complete("-1")
            }
          }
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
