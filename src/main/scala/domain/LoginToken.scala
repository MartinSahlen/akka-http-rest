package domain

import java.sql.Timestamp
import java.util.UUID.randomUUID

import com.typesafe.scalalogging.LazyLogging
import db.DB
import org.joda.time.DateTime

import domain.User.userFromRowData

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.{ExecutionContext, Future}

case class LoginToken(token: String, modified: DateTime, created: DateTime)

object LoginToken extends DB with LazyLogging {
  implicit val executionContext: ExecutionContext = global

  def authenticateUser(username: String, password: String): Future[Option[User]] = {
    execute("SELECT * FROM users WHERE username= ? AND password = ? LIMIT 1", username, password) map { data =>
      data.rows match {
        case Some(resultSet) =>
          if (resultSet.isEmpty) {
            None
          } else {
            Some(userFromRowData(resultSet.head))
          }
        case _ => None
      }
    }
  }

  def generateLoginToken(username: String, password: String): Future[Option[LoginToken]] =
    authenticateUser(username, password) flatMap {
      case Some(user) =>
        val token = LoginToken(randomUUID.toString, DateTime.now, DateTime.now)
        execute("INSERT INTO login_tokens (token, user_id, last_used, created, modified) VALUES (?, ?, ?, ?, ?)",
          randomUUID.toString,
          user.id,
          new Timestamp(DateTime.now.getMillis),
          new Timestamp(token.created.getMillis),
          new Timestamp(token.modified.getMillis)) map { data =>
          data.rows match {
            case Some(resultSet) => Some(token)
            case _ => None
          }
        }
      case _ => Future(None)
      }
}
