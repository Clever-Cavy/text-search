package task.textsearch.server

import com.typesafe.scalalogging.LazyLogging

object ServerRunner extends App with LazyLogging {
  val configName = args(0)
  val server = new HttpServer(configName)
}