package task.textsearch.server.storage.message

import akka.routing.ConsistentHashingRouter.ConsistentHashable

final case class GetDocument(key: String) extends ConsistentHashable {
  override def consistentHashKey: Any = key
}