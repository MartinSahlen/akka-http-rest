package domain

import com.github.mauricio.async.db.QueryResult
import db.DB
import spray.json._
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.ExecutionContext.Implicits.global

case class User(id: String, username: String, password: String, role: String)

object User extends DB {

  implicit val executionContext: ExecutionContext = global

  def getSomeStuff: Future[QueryResult] = {
    execute("SELECT * FROM users")
  }

  def createUser(user: User): Future[User] = {
    Future {new User("234234", "yo@uo.no", "passoword", "admin")}
  }

  def getUserByLoginToken(loginToken: String): Future[Option[User]] = Future {Some(new User("yo@uo.no", "passoword", "admin"))}

  def getAllUsers: Future[JsValue] = {
    getSomeStuff map { data =>
      data.rows match {
        case Some(resultSet) => JsArray((for {r <- resultSet} yield {
          JsObject(Map("id" -> JsNumber(r.apply("id").asInstanceOf[Long]),
            "username" -> JsString(r.apply("username").asInstanceOf[String]),
            "password" -> JsString(r.apply("password").asInstanceOf[String]),
            "role" -> JsString(r.apply("role").asInstanceOf[String])
          ))
        }).toVector)
        case _ => JsObject(Map("status" -> JsString("No objects found")))
      }
    } recover {
      case _ => JsObject(Map("status" -> JsString("Something went really wrong")))
    }
  }

  def doSomeStuff: Future[String] = {
    execute("INSERT INTO users (username, password, role) VALUES (?, ?, ?)", "martin", "password", "admin") map { data =>
      data.rows match {
        case Some(resultSet) => resultSet.toString
      }
    } recover {
      case _ => "Something went really wrong"
    }
  }
}
