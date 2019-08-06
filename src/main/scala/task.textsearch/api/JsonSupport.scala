package task.textsearch.api

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol

trait JsonSupport extends SprayJsonSupport {
  import DefaultJsonProtocol._

  implicit val documentFormat = jsonFormat2(Document)
  implicit val documentsFormat = jsonFormat1(Documents)
  implicit val actionPerformedJsonFormat = jsonFormat1(ActionPerformed)
}
