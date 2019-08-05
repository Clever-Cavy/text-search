package task.textsearch.server.storage.message

import akka.routing.ConsistentHashingRouter.ConsistentHashable
import task.textsearch.server.storage.Document


final case class PutDocument(document: Document) extends ConsistentHashable {
  override def consistentHashKey: Any = document.key
}