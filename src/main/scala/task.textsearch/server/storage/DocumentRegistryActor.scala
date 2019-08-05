package task.textsearch.server.storage

import akka.actor.{Actor, Props}
import com.typesafe.scalalogging.LazyLogging
import task.textsearch.server.storage.message._

import scala.collection.mutable

class DocumentRegistryActor extends Actor with LazyLogging {

  var documents = Map.empty[String, Document]
  val index = mutable.Map.empty[String, List[Document]]

  def receive: Receive = {
    case PutDocument(document) =>
      logger.debug(s"put ${document.key}")
      if (documents.contains(document.key)) {
        sender() ! DocumentAdded(s"Document with key ${document.key} already exists. Updating documents is not supported. Use another key")
      } else {
        documents += (document.key -> document)
        tokenize(document).foreach { token =>
          index.put(token, document +: index.getOrElse(token, Nil))
        }
        sender() ! DocumentAdded(s"Document created. Access it by key: ${document.key}")
      }

    case GetDocument(key) =>
      logger.debug(s"get $key")
      sender() ! documents.get(key)

    case Search(tokens) =>
      logger.debug(s"search $tokens")
      val maybe = tokens.map {
        token => index.getOrElse(token, Nil)
      }.reduceLeft(_ intersect _).map(_.key)

      val result = documents.values.filter { document =>
        tokens.forall(token => document.value.contains(token))
      }.map(_.key).toSeq
      sender() ! SearchResult(result)
  }

  private def tokenize(document: Document): Seq[String] = {
    document.value.split(' ').distinct
  }
}

object DocumentRegistryActor {
  def props: Props = Props[DocumentRegistryActor]
}
