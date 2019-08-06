package task.textsearch.actor.message

import akka.routing.ConsistentHashingRouter.ConsistentHashable
import task.textsearch.storage.Document


final case class PutDocument(document: Document) extends ConsistentHashable {
  override def consistentHashKey: Any = document.key
}