package security

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server._
import com.typesafe.scalalogging.LazyLogging
import domain.{UserRepo, User}
import json.JsonSupport
import spray.json.{JsString, JsObject}

object Authentication extends LazyLogging with Directives with JsonSupport {

  val userRepo = UserRepo

  val authenticate: Directive1[User] = {
    optionalHeaderValueByName("Authorization") flatMap {
      case Some(authHeader) =>
        val accessToken = authHeader.split(' ').last
        onSuccess(userRepo.getUserByAuthHeader(accessToken)).flatMap {
          case Some(user) => provide(user)
          case _       => complete(Unauthorized,  JsObject(Map("status" -> JsString("Wrong Authorization header"))))
        }
      case _ => complete(Unauthorized,  JsObject(Map("status" -> JsString("Missing Authorization header"))))
    }
  }
}
