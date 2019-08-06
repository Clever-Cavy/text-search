package task.textsearch.server

import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route

import scala.util.{Failure, Success}

class HttpServer(val configName: String) extends DocumentRoutes with ErrorHandlers with AkkaConfig {

  lazy val routes: Route = documentRoutes

  Http().bindAndHandle(routes, "localhost", config.getInt("http.server.port")).onComplete {
    case Success(bound) =>
      logger.info(s"Server online at http://${bound.localAddress.getHostString}:${bound.localAddress.getPort}/")
    case Failure(e) =>
      logger.error(s"Server could not start!", e)
      system.terminate()
  }

}
