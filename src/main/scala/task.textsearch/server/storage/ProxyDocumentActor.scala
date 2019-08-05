package task.textsearch.server.storage

import akka.actor.{Actor, ActorRef}
import akka.routing.{BroadcastGroup, ConsistentHashingGroup}
import task.textsearch.server.storage.message._

class ProxyDocumentActor(workers: Seq[String]) extends Actor {

  val all = context.actorOf(BroadcastGroup(workers.toList).props(), "broadcast")
  val sharded = context.actorOf(ConsistentHashingGroup(workers.toList).props(), "sharded")

  def receive: Receive = {
    case msg: PutDocument =>
      sharded forward msg

    case msg: GetDocument =>
      sharded forward msg

    case msg: Search =>
      all ! msg
      context become collect(sender, workers.size, Seq.empty)
  }

  def collect(sender: ActorRef, count: Int, found: Seq[String]): Receive = {
    case SearchResult(documents) =>
      val mergedResult = found ++ documents
      if (count == 1) {
        sender ! SearchResult(mergedResult)
        context become receive
      } else {
        context become collect(sender, count - 1, mergedResult)
      }
  }
}