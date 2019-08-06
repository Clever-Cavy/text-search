package task.textsearch.actor.message

import akka.routing.ConsistentHashingRouter.ConsistentHashable

final case class GetDocument(key: String) extends ConsistentHashable {
  override def consistentHashKey: Any = key
}