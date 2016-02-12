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
    "API docs for Akka-Http Example project",
    "0.1","Akka-Http Example",
    "",
    Some(Contact("Martin","","")),
    None,
    Map())
  override val basePath = "/"    //the basePath for the API you are exposing
  override val apiDocsPath = "/api-docs" //where you want the swagger-json endpoint exposed

  //override def swaggerConfig { n}

  val apiRoutes = new LoginService().route

  val allRoutes = corsHandler {
    handleRejections(Rejection.myRejectionHandler)(apiRoutes ~ routes)
  }
}
