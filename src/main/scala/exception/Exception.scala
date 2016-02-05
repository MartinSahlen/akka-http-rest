package exception
import spray.json.DefaultJsonProtocol._


case class UnknownFieldsException(message:String, validFields:List[String], fieldsReceived:List[String]) extends Throwable

case class UnknownFieldsErrorMessage(message: String, validFields:List[String], fieldsReceived:List[String])
object UnknownFieldsErrorMessage {
  implicit val errorFormat = jsonFormat3(UnknownFieldsErrorMessage.apply)
}

case class ErrorMessage(message: String)
object ErrorMessage {
  implicit val errorFormat = jsonFormat1(ErrorMessage.apply)
}