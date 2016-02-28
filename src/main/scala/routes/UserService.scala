package routes

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives
import com.typesafe.scalalogging.LazyLogging
import domain.UserRepo.createUser
import domain.{CreateUserRequest, UserJsonFormatters}
import spray.json.{JsObject, JsString}

class UserService extends LazyLogging with Directives with UserJsonFormatters  {

  val route = users

  def users = pathPrefix("users") {
    pathEndOrSingleSlash {
      post {
        entity(as[CreateUserRequest]) { request =>
          com.wix.accord.validate(request) match {
            case com.wix.accord.Success =>
              onSuccess(createUser(request)) {
                case Some(user) => complete(user)
                case _ => complete(InternalServerError, JsObject(Map("status" -> JsString("Something went wrong"))))
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
