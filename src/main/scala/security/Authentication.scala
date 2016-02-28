package security

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server._
import com.typesafe.scalalogging.LazyLogging
import domain.{User, UserRepo}
import spray.json.{JsString, JsObject}

object Authentication extends LazyLogging with Directives with SprayJsonSupport {

  val userRepo = UserRepo

  val authenticate: Directive1[User] = {
    optionalHeaderValueByName("Authorization") flatMap {
      case Some(authHeader) =>
        val accessToken = authHeader.split(' ').last
        onSuccess(userRepo.getUserByLoginToken(accessToken)).flatMap {
          case Some(user) => provide(user)
          case _ => complete(Unauthorized,  JsObject(Map("status" -> JsString("Wrong Authorization header"))))
        }
      case _ => complete(Unauthorized,  JsObject(Map("status" -> JsString("Missing Authorization header"))))
    }
  }

  def authenticateWithRoles(roles: Seq[String]): Directive1[User] = {
    optionalHeaderValueByName("Authorization") flatMap {
      case Some(authHeader) =>
        val accessToken = authHeader.split(' ').last
        onSuccess(userRepo.getUserByLoginToken(accessToken)).flatMap {
          case Some(user) =>
            if (roles.contains(user.role)) {
              provide(user)
            } else {
              complete(Unauthorized,  JsObject(Map("status" -> JsString("You are not authorized with the privileges to do this action"))))
            }
          case _ => complete(Unauthorized,  JsObject(Map("status" -> JsString("Wrong Authorization header"))))
        }
      case _ => complete(Unauthorized,  JsObject(Map("status" -> JsString("Missing Authorization header"))))
    }
  }
}
