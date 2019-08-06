package task.textsearch.actor

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import task.textsearch.actor.message._
import task.textsearch.storage.Document

class ProxyDocumentActorSpec extends TestKit(ActorSystem("MySpec"))
  with ImplicitSender
  with WordSpecLike
  with Matchers
  with BeforeAndAfterAll {

  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }

  val workers = Seq("worker1", "worker2", "worker3")
  val workerActors = workers.map { name =>
    system.actorOf(DocumentRegistryActor.props, name)
  }
  val proxyActor = system.actorOf(Props(new ProxyDocumentActor(workers.map(name => s"/user/$name"))), "master")

  "An proxy (master) actor" must {

    "be able to work with workers" in {
      val document1 = Document("k1", "k1value one two three")
      val document2 = Document("k2", "k2value one two")
      val document3 = Document("k3", "k3value absjodifjsd")
      Seq(document1, document2, document3).foreach { document =>
        proxyActor ! PutDocument(document)
        expectMsg(DocumentAdded(s"Document created. Access it by key: ${document.key}"))
      }

      workerActors.foreach { actor =>
        actor ! GetDocument("k1")
      }
      expectMsgAllOf(None, Some(document1), None) // it means only one of the workers actually stores the document

      proxyActor ! GetDocument("k1")
      expectMsg(Some(document1))

      proxyActor ! GetDocument("k2")
      expectMsg(Some(document2))

      proxyActor ! GetDocument("k3")
      expectMsg(Some(document3))

      proxyActor ! Search(Set("one"))
      expectMsgAnyOf(SearchResult(Seq("k1", "k2")), SearchResult(Seq("k2", "k1")))

      proxyActor ! Search(Set("nothing"))
      expectMsg(SearchResult(Seq()))
    }

  }
}
