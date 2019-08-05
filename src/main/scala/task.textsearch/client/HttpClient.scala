package task.textsearch.client

import java.net.URLEncoder

import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding.{Get, Post}
import akka.http.scaladsl.model.HttpRequest
import com.typesafe.scalalogging.LazyLogging
import task.textsearch.model.{Document, JsonSupport}

import scala.util.{Failure, Success}

class HttpClient extends AkkaConfig with LazyLogging with JsonSupport {
  private val storageHost = config.getString("http.endpoint")
  logger.info(s"Connecting to $storageHost")

  def get(key: String) = {

    val request = Get(uri = s"$storageHost/storage/documents?key=${encode(key)}")
    send(request)
  }

  def put(key: String, document: String) = {
    val request = Post(uri = s"$storageHost/storage/documents", Document(key, document))
    send(request)
  }

  def search(tokens: String) = {
    val request = Get(uri = s"$storageHost/storage/search?tokens=${encode(tokens)}")
    send(request)
  }

  private def encode(str: String) = URLEncoder.encode(str, "UTF-8")

  private def send(request: HttpRequest): Unit = {
    Http().singleRequest(request).onComplete {
      case Success(result) => println(result.entity.toString)
      case Failure(e) => logger.error("something wrong", e)
    }
  }
}
