package domain

import java.sql.Timestamp
import java.util.UUID.randomUUID

import com.typesafe.scalalogging.LazyLogging
import db.DB.execute
import org.joda.time.DateTime

import domain.User.userFromRowData

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.{ExecutionContext, Future}
import org.mindrot.jbcrypt.BCrypt

case class LoginToken(token: String, modified: DateTime, created: DateTime)

object LoginToken extends LazyLogging {
  implicit val executionContext: ExecutionContext = global

  def authenticateUser(username: String, password: String): Future[Option[User]] = {
    logger.debug(s"Attempting to log in user with username: $username")
    execute("SELECT * FROM users WHERE username=? LIMIT 1", username) map { data =>
      data.rows match {
        case Some(resultSet) =>
          if (resultSet.isEmpty) {
            logger.debug(s"Could not find user: $username")
            None
          } else {
            val user = userFromRowData(resultSet.head)
            if (BCrypt.checkpw(password, user.password)) {
              logger.debug(s"User: $username successfully logged in")
              Some(userFromRowData(resultSet.head))
            }
            else
              logger.debug(s"User: $username was denied login because of wrong password")
              None
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
