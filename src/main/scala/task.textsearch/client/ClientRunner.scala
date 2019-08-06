package task.textsearch.client

import com.typesafe.scalalogging.LazyLogging

object ClientRunner extends App with LazyLogging {
  val client = new CliClient(new HttpClient())

  client.run()
}