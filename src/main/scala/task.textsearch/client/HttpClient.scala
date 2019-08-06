package task.textsearch.client

import java.net.URLEncoder

import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding.{Get, Post}
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.unmarshalling.Unmarshal
import com.typesafe.scalalogging.LazyLogging
import task.textsearch.api.{Document, JsonSupport}

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

class HttpClient extends AkkaConfig with LazyLogging with JsonSupport {
  private lazy val storageHost = {
    val host = config.getString("http.endpoint")
    logger.info(s"Connecting to $host")
    host
  }

  def get(key: String): Future[String] = {
    send(
      Try(Get(uri = s"$storageHost/storage/documents/${encode(key)}"))
    )
  }

  def put(key: String, document: String): Future[String] = {
    send(
      Try(Post(uri = s"$storageHost/storage/documents", Document(key, document)))
    )
  }

  def search(tokens: String): Future[String] = {
    send(
      Try(Get(uri = s"$storageHost/storage/search?tokens=${encode(tokens)}"))
    )
  }

  private def encode(str: String) = URLEncoder.encode(str, "UTF-8")

  private def send(request: Try[HttpRequest]): Future[String] = {
    request match {
      case Success(request) =>
        Http().singleRequest(request).flatMap(response => Unmarshal(response.entity).to[String])
      case Failure(exception) =>
        Future.failed(exception)
    }

  }
}
