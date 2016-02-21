package http

import akka.http.scaladsl.server.Directives
import com.github.swagger.akka.model.{Contact, Info}
import com.github.swagger.akka.{HasActorSystem, SwaggerHttpService}
import rejection.Rejection
import routes.LoginService
import utils.CorsSupport
import scala.reflect.runtime.{universe => ru}

trait HttpService extends Directives with CorsSupport with SwaggerHttpService with HasActorSystem {
  override val apiTypes = Seq(ru.typeOf[LoginService])
  override val host = "localhost:8080"
  override val info = Info(
    "API docs for Akka-Http Blog project",
    "0.1","Akka-Http Blog",
    "",
    Some(Contact("Martin","","")),
    None,
    Map())
  override val basePath = "/"
  override val apiDocsPath = "docs"

  val apiRoutes = new LoginService().route

  val allRoutes = corsHandler {
    handleRejections(Rejection.myRejectionHandler)(apiRoutes ~ routes)
  }
}
