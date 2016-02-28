package domain

import java.sql.Timestamp

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.github.mauricio.async.db.RowData
import db.DB
import org.joda.time.DateTime
import org.mindrot.jbcrypt.BCrypt
import spray.json._
import DB.execute
import java.util.UUID.randomUUID

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}
import com.wix.accord.dsl._

case class User(id: String, username: String, password: String, role: String)
case class CreateUserRequest(username: String, password: String)

object UserRepo {

  implicit val executionContext: ExecutionContext = global

  def createUser(userRequest: CreateUserRequest): Future[Option[User]] = {
    val hashedPassword = BCrypt.hashpw(userRequest.password, BCrypt.gensalt())
    val id = randomUUID.toString
    val created = new Timestamp(DateTime.now.getMillis)
    val modified = created
    val role = "user"
    execute("INSERT INTO users (id, username, password, role, created, modified) VALUES (?,?,?,?,?,?)",
      id,
      userRequest.username,
      hashedPassword,
      role,
      created,
      modified) map { data =>
      data.rows match {
        case Some(resultSet) =>
          Some(User(id, userRequest.username, hashedPassword, role))
        case _ => None
      }
    }
  }

  def userFromRowData(row: RowData): User = {
    User(
      row.apply("id").asInstanceOf[String],
      row.apply("username").asInstanceOf[String],
      row.apply("password").asInstanceOf[String],
      row.apply("role").asInstanceOf[String]
    )
  }

  def getUserByLoginToken(loginToken: String): Future[Option[User]] = Future {Some(new User("id","yo@uo.no", "password", "admin"))}
}

trait UserJsonFormatters extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val userRequestValidation = validator[CreateUserRequest] { r =>
    r.password.length() as "password:length" should be > 5
    r.username.length() as "username:length" should be > 2
  }

  implicit val userRequestFormat = jsonFormat2(CreateUserRequest)

  implicit object UserJsonFormat extends RootJsonFormat[User] {
    def write(u: User) = JsObject(
      "id" -> JsString(u.id),
      "username" -> JsString(u.username)
    )
    def read(value: JsValue) = {
      value.asJsObject.getFields("username", "id", "password", "roles") match {
        case Seq(JsString(username), JsString(id), JsString(password), JsString(roles)) =>
          new User(id, username, password, roles)
        case _ => throw new DeserializationException("User expected")
      }
    }
  }
}
