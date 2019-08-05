package task.textsearch.server

import akka.actor.{ActorRef, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import task.textsearch.server.storage.{DocumentRegistryActor, ProxyDocumentActor}

import scala.collection.JavaConverters._
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.{Failure, Success}

class HttpServer(val configName: String) extends DocumentRoutes with ErrorHandlers with AkkaConfig {

  val registryActor: ActorRef = if (config.getBoolean("master")) {
    val remoteWorkers = config.getStringList("workers").asScala
    system.actorOf(Props(new ProxyDocumentActor(remoteWorkers)), "master")
  } else {
    system.actorOf(DocumentRegistryActor.props, "worker")
  }

  lazy val routes: Route = documentRoutes

  Http().bindAndHandle(routes, "localhost", config.getInt("http.server.port")).onComplete {
    case Success(bound) =>
      logger.info(s"Server online at http://${bound.localAddress.getHostString}:${bound.localAddress.getPort}/")
    case Failure(e) =>
      logger.error(s"Server could not start!", e)
      system.terminate()
  }

  Await.result(system.whenTerminated, Duration.Inf)
}
