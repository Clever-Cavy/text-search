package task.textsearch.client

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory

import scala.concurrent.ExecutionContext

trait AkkaConfig {
  val config = ConfigFactory.load().getConfig("client")

  implicit val system: ActorSystem = ActorSystem("client", config)
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContext = system.dispatcher
}
