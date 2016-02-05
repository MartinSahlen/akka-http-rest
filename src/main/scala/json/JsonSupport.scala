package json

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import domain.{PostRequest, PostResponse}
import exception.UnknownFieldsException
import spray.json._

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val StartProcessingResultFormat = jsonFormat1(PostResponse)

  val CLIENT_NAME_JSON_FIELD = "client_name"

  implicit object StartProcessingRequestFormat extends RootJsonFormat[PostRequest] {
    def write(request: PostRequest) = JsObject(
      CLIENT_NAME_JSON_FIELD  -> JsString(request.clientName)
    )
    def read(value: JsValue) = {
      value.asJsObject.getFields(CLIENT_NAME_JSON_FIELD) match {
        case Seq(JsString(clientName)) =>
          new PostRequest(clientName)
        case _ =>
          val message = s"Could not deserialize object, got missing / unknown fields, null values and / or wrong types:"
          deserializationError(
            message,
            UnknownFieldsException(message, List(CLIENT_NAME_JSON_FIELD), value.asJsObject.fields.keys.toList))
      }
    }
  } // LIST of gotten fields, + LIST of valid fields
}
