package task.textsearch.actor

import akka.actor.{Actor, Props}
import com.typesafe.scalalogging.LazyLogging
import task.textsearch.actor.message._
import task.textsearch.storage.DocumentStorage

class DocumentRegistryActor extends Actor with LazyLogging {

  val storage = new DocumentStorage()

  def receive: Receive = {
    case PutDocument(document) =>
      logger.debug(s"put ${document.key}")
      if (storage.exists(document.key)) {
        sender() ! DocumentAdded(s"Document with key ${document.key} already exists. Updating documents is not supported. Use another key")
      } else {
        storage.put(document)
        sender() ! DocumentAdded(s"Document created. Access it by key: ${document.key}")
      }

    case GetDocument(key) =>
      logger.debug(s"get $key")
      sender() ! storage.get(key)

    case Search(tokens) =>
      logger.debug(s"search $tokens")
      sender() ! SearchResult(storage.search(tokens))
  }
}

object DocumentRegistryActor {
  def props: Props = Props[DocumentRegistryActor]
}
