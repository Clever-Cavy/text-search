package task.textsearch.server

import akka.actor.ActorRef
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.util.Timeout
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{Matchers, WordSpec}
import task.textsearch.model.Document
import task.textsearch.server.storage.DocumentRegistryActor

import scala.concurrent.duration._

class DocumentRoutesSpec extends WordSpec with Matchers with ScalaFutures with ScalatestRouteTest
  with DocumentRoutes with ErrorHandlers {

  override def timeout = Timeout(5.seconds)
  implicit val executionContext = executor

  override val registryActor: ActorRef = system.actorOf(DocumentRegistryActor.props, "documentRegistry")

  lazy val routes = documentRoutes

  "DocumentRoutes" should {
    "return no documents if no present (GET /storage/search)" in {
      val document = Document("x-file-to-search", "aaaa bbbb zzzz ffff cccc")
      val documentEntity = Marshal(document).to[MessageEntity].futureValue
      val postRequest = Post("/storage/documents").withEntity(documentEntity)
      postRequest ~> routes ~> runRoute

      val request = Get(uri = "/storage/search?tokens=zzzz,ffff")
      request ~> routes ~> check {
        status should ===(StatusCodes.OK)
        contentType should ===(ContentTypes.`application/json`)
        entityAs[String] should ===("""{"documents":["x-file-to-search"]}""")
      }
    }

    "be able to add documents (POST /storage/documents)" in {
      val document = Document("x-file-3078", "aaaa bbbb")
      val documentEntity = Marshal(document).to[MessageEntity].futureValue
      val request = Post("/storage/documents").withEntity(documentEntity)

      request ~> routes ~> check {
        status should ===(StatusCodes.Created)
        contentType should ===(ContentTypes.`application/json`)
        entityAs[String] should ===("""{"description":"Document x-file-3078 created."}""")
      }
    }

    "be able to get document by key key (GET /storage/documents)" in {
      val document = Document("x-file-9999", "aaaa bbbb")
      val documentEntity = Marshal(document).to[MessageEntity].futureValue
      val postRequest = Post("/storage/documents").withEntity(documentEntity)
      postRequest ~> routes ~> runRoute

      val request = Get("/storage/documents?key=x-file-9999")
      request ~> routes ~> check {
        status should ===(StatusCodes.OK)
        contentType should ===(ContentTypes.`application/json`)
        entityAs[String] should ===("""{"key":"x-file-9999","value":"aaaa bbbb"}""")
      }
    }
  }
}