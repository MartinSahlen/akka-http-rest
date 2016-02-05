package security

import akka.http.scaladsl.server.directives.Credentials
import akka.http.scaladsl.server.directives.Credentials.Provided
import com.typesafe.scalalogging.LazyLogging

object Authentication extends LazyLogging {
  def basicAuth(credentials: Credentials): Option[String] = {
    credentials match {
      case p@Provided(username) if p.verify("password") => Option(username)
      case _                                            => logger.info("You shall not pass!"); Option.empty
    }
  }
}
