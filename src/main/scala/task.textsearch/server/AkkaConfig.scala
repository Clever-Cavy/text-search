package task.textsearch.server

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import task.textsearch.actor.{DocumentRegistryActor, ProxyDocumentActor}

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

trait AkkaConfig {
  def configName: String

  val config = {
    val config = ConfigFactory.load()
    config.getConfig(configName).withFallback(config)
  }

  implicit val system: ActorSystem = ActorSystem("textsearch", config)
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContext = system.dispatcher

  def timeout = Timeout(config.getLong("askTimeoutMs") millis)

  val registryActor: ActorRef = if (config.getBoolean("master")) {
    val remoteWorkers = config.getStringList("workers").asScala
    system.actorOf(Props(new ProxyDocumentActor(remoteWorkers)), "master")
  } else {
    system.actorOf(DocumentRegistryActor.props, "worker")
  }
}
