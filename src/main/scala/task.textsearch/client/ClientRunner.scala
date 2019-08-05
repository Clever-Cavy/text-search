package task.textsearch.client

import com.typesafe.scalalogging.LazyLogging

import scala.util.Random

object ClientRunner extends App with LazyLogging {
  val client = new HttpClient()

  printHelp()

  while (true) {
    try {
      readInput()
    } catch {
      case e: Exception =>
        logger.error("some error occurred", e)
    }
  }

  private def createKey = {
    Random.alphanumeric.take(5).mkString
  }

  private def printHelp() = println(
    """Available commands:
      |  put <text>,
      |  get <key>,
      |  search <tokens>,
      |  quit""".stripMargin
  )

  def readInput() = {
    scala.io.StdIn.readLine("> ") match {
      case str if str.startsWith("put ") =>
        client.put(createKey, str.substring("put ".length))

      case str if str.startsWith("get ") =>
        client.get(str.substring("get ".length))

      case str if str.startsWith("search ") =>
        client.search(str.substring("search ".length))

      case str if str.startsWith("q") => System.exit(0)

      case _ =>
        printHelp()
    }
  }
}