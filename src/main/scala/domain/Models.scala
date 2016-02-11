package domain

import com.wix.accord.dsl._
import io.swagger.annotations.{ApiModelProperty, ApiModel}

import scala.annotation.meta.field

@ApiModel
case class PostResponse(@(ApiModelProperty @field)(value = "Status")
                         status: String)

@ApiModel
case class PostRequest(@(ApiModelProperty @field)(value = "CLient name")
                        clientName: String)

object PostRequest {
  implicit val postRequestValidation = validator[PostRequest] { p =>
    p.clientName  as "client name length" is notEmpty
    p.clientName.length() as "clientName:length" should be > 5
    p.clientName as "clientName:prefix" should startWith("martin")
  }
}