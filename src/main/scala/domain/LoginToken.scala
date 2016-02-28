package domain

import java.sql.Timestamp
import java.util.UUID.randomUUID

import com.typesafe.scalalogging.LazyLogging
import db.DB.execute
import org.joda.time.{DateTimeZone, DateTime}

import domain.UserRepo.userFromRowData

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.{ExecutionContext, Future}
import org.mindrot.jbcrypt.BCrypt

case class LoginToken(token: String, modified: DateTime, created: DateTime)

object LoginToken extends LazyLogging {
  implicit val executionContext: ExecutionContext = global

  def authenticateUser(username: String, password: String): Future[Option[User]] = {
    logger.info(s"Attempting to log in user with username: $username")
    execute("SELECT * FROM users WHERE username=? LIMIT 1", username) map { data =>
      data.rows match {
        case Some(resultSet) =>
          resultSet.length match {
            case 0 =>
              logger.info(s"Could not find user: $username")
              None
            case 1 =>
              val user = userFromRowData(resultSet.head)
              BCrypt.checkpw(password, user.password) match {
                case true =>
                  logger.info(s"User: $username successfully logged in")
                  Some(user)
                case _ =>
                  logger.info(s"User: $username was denied login because of wrong password")
                  None
              }
            case _ => None
          }

        case _ => None
      }
    }
  }

  def generateLoginToken(username: String, password: String): Future[Option[LoginToken]] =
    authenticateUser(username, password) flatMap {
      case Some(user) =>
        val created = DateTime.now
        val token = LoginToken(randomUUID.toString, created, created)
        execute("INSERT INTO login_tokens (token, user_id, last_used, created, modified) VALUES (?,?,?,?,?)",
          randomUUID.toString,
          user.id,
          new Timestamp(token.created.getMillis),
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
