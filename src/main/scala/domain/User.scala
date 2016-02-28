package domain

import com.github.mauricio.async.db.{RowData, QueryResult}
import db.DB.execute
import spray.json._
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.ExecutionContext.Implicits.global

case class User(id: String, username: String, password: String, role: String)

object User {

  implicit val executionContext: ExecutionContext = global

  def createUser(user: User): Future[User] = {
    Future {new User("234234", "yo@uo.no", "passoword", "admin")}
  }

  def userFromRowData(row: RowData): User = {
    User(
      row.apply("id").asInstanceOf[String],
      row.apply("username").asInstanceOf[String],
      row.apply("password").asInstanceOf[String],
      row.apply("role").asInstanceOf[String]
    )
  }

  def getUserByLoginToken(loginToken: String): Future[Option[User]] = Future {Some(new User("id","yo@uo.no", "passoword", "admin"))}

}
