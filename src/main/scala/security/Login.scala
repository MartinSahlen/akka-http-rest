package security

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.wix.accord.dsl._
import io.swagger.annotations.{ApiModel, ApiModelProperty}
import spray.json.DefaultJsonProtocol

@ApiModel
case class LoginRequest(
                         @ApiModelProperty
                         password: Option[String],
                         @ApiModelProperty
                         username: Option[String]
                       )
@ApiModel
case class LoginResponse(@ApiModelProperty token: String)

trait LoginJsonFormatters extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val loginRequestValidation = validator[LoginRequest] { request =>
    request.password is notEmpty
    request.username is notEmpty
    request.password.get.length as "password:length" should be > 5
  }

  implicit val loginRequestFormat = jsonFormat2(LoginRequest)
  implicit val loginResponseFormat = jsonFormat1(LoginResponse)
}

/*
object SearchRequestJsonProtocol extends DefaultJsonProtocol {
    implicit object SearchRequestJsonFormat extends JsonFormat[SearchRequest] {
        def read(value: JsValue) = value match {
            case JsObject(List(
                    JsField("url", JsString(url)),
                    JsField("nextAt", JsString(nextAt)))) =>
                SearchRequest(url, Some(new Instant(nextAt)))

            case JsObject(List(JsField("url", JsString(url)))) =>
                SearchRequest(url, None)

            case _ =>
                throw new DeserializationException("SearchRequest expected")
        }

        def write(obj: SearchRequest) = obj.nextAt match {
            case Some(nextAt) =>
                JsObject(JsField("url", JsString(obj.url)),
                         JsField("nextAt", JsString(nextAt.toString)))
            case None => JsObject(JsField("url", JsString(obj.url)))
        }
    }
}
 */