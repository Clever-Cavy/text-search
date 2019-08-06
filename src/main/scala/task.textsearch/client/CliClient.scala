package task.textsearch.client

import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.Future
import scala.util.{Failure, Random, Success}
import scala.concurrent.ExecutionContext.Implicits.global

class CliClient(client: HttpClient) extends LazyLogging {


  val helpString: String =
    """Available commands:
      |  put <text>,
      |  get <key>,
      |  search <comma-separated tokens>,
      |  quit""".stripMargin

  private def createKey = {
    Random.alphanumeric.take(5).mkString.toLowerCase
  }

  def run(): Unit = {
    println(helpString)
    prompt()
  }

  def prompt(): Unit = {
    readLine() match {
      case None => System.exit(0)
      case Some(command) =>
        execute(command).onComplete {
          case Success(result) if result == "!exit" =>
            System.exit(0)
          case Success(result) =>
            println(result)
            prompt()
          case Failure(e) =>
            logger.error("something went wrong", e)
            prompt()
        }
    }
  }


  def readLine(): Option[String] = {
    Option(scala.io.StdIn.readLine("> "))
  }

  def execute(command: String): Future[String] = {
    command match {
      case str if str.startsWith("put ") =>
        client.put(createKey, str.substring("put ".length))

      case str if str.startsWith("get ") =>
        client.get(str.substring("get ".length))

      case str if str.startsWith("search ") =>
        client.search(str.substring("search ".length))

      case str if str.startsWith("q") =>
        Future.successful("!exit")

      case _ =>
        Future.successful(helpString)
    }
  }
}
