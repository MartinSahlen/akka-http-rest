package domain

import java.sql.Timestamp

import db.DB
import org.joda.time.DateTime
import scala.concurrent.{Future, ExecutionContext}
import scala.concurrent.ExecutionContext.Implicits._
import java.util.UUID.randomUUID

case class LoginToken(token: String, modified: DateTime, created: DateTime)

object LoginToken extends DB {
  implicit val executionContext: ExecutionContext = global

  def generateLoginTokenForUser(user: User): Future[LoginToken] = {
    val token = LoginToken(randomUUID.toString, DateTime.now, DateTime.now)
    execute("INSERT INTO login_tokens (token, user_id, last_used, created, modified) VALUES (?, ?, ?)",
      token.token,
      user.id,
      new Timestamp(DateTime.now.getMillis),
      new Timestamp(token.created.getMillis),
      new Timestamp(token.modified.getMillis)) map { data =>
      data.rows match {
        case Some(resultSet) => token
      }
    }
  }
}
