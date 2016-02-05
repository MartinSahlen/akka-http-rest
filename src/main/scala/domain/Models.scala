package domain

import com.wix.accord.dsl._


case class PostResponse(status: String)
case class PostRequest(clientName: String)

object PostRequest {
  implicit val postRequestValidation = validator[PostRequest] { p =>
    p.clientName  as "client name length" is notEmpty
    p.clientName.length() as "clientName:length" should be > 5
    p.clientName as "clientName:prefix" should startWith("martin")
  }
}