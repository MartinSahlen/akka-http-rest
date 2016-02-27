package security

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.wix.accord.dsl._
import io.swagger.annotations.{ApiModelProperty, ApiModel}
import spray.json.DefaultJsonProtocol

@ApiModel
case class LoginRequest(
                         @ApiModelProperty
                         password: String,
                         @ApiModelProperty
                         username: String
                       )
@ApiModel
case class LoginResponse(@ApiModelProperty token: String)

trait LoginFormatters extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val loginRequestValidation = validator[LoginRequest] { request =>
    request.password is notEmpty
    request.username is notEmpty
    request.password.length() as "password:length" should be > 5
  }

  implicit val loginRequestFormat = jsonFormat2(LoginRequest)
  implicit val loginResponseFormat = jsonFormat1(LoginResponse)
}
