package http

import akka.http.scaladsl.server.Directives
import json.JsonSupport
import rejection.Rejection
import routes.Login
import utils.CorsSupport

trait HttpService extends JsonSupport with Directives with CorsSupport {
  val routes =
    corsHandler {
      handleRejections(Rejection.myRejectionHandler)(Login.route)
    }
}
