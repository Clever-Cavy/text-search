package task.textsearch.client

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{AsyncWordSpec, Matchers}

import scala.concurrent.Future

class ClientSpec extends AsyncWordSpec with Matchers with ScalaFutures {

  private val stubHttpClient: HttpClient = new HttpClient {
    override def get(key: String): Future[String] = Future.successful(s"Document by key: $key")
    override def put(key: String, document: String): Future[String] = Future.successful(s"New document: $document")
    override def search(tokens: String): Future[String] = Future.successful(s"Searching by tokens: $tokens")
  }

  val client = new CliClient(stubHttpClient)

  "CliClient" should {
    "give some help" in {
      client.execute("something wrong") map { response =>
        response shouldBe client.helpString
      }
    }

    "put some documents" in {
      client.execute("put doc text") map { response =>
        response shouldBe "New document: doc text"
      }
    }

    "get documents by key" in {
      client.execute("get uniqueid") map { response =>
        response shouldBe "Document by key: uniqueid"
      }
    }

    "search documents" in {
      client.execute("search akka,http") map { response =>
        response shouldBe "Searching by tokens: akka,http"
      }
    }

    "be able to exit" in {
      client.execute("q") map { response =>
        response shouldBe "!exit"
      }
    }
  }
}
