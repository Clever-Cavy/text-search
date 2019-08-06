package task.textsearch.storage

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{Matchers, WordSpec}

class DocumentStorageSpec extends WordSpec with Matchers with ScalaFutures {

  val storage = new DocumentStorage()

  "DocumentStorage" should {
    "put some documents" in {
      val document = Document("k1", "k1value")
      storage.put(document)

      storage.exists("k1") shouldBe true
      storage.exists("not-k1") shouldBe false
    }

    "get documents by key" in {
      val document = Document("k2", "k2value")
      storage.put(document)

      storage.get("k2") shouldBe Some(document)
      storage.get("not-k2") shouldBe None
    }

    "search documents by tokens" in {
      val document1 = Document("search1", "one, two three four five.")
      val document2 = Document("search2", "one: two three - aa bb cc dd")
      val document3 = Document("search3", "one aabbccdd able about")
      Seq(document1, document2, document3).foreach(storage.put)

      storage.search(Set("one")) sameElements Seq("search1", "search2", "search3")
      storage.search(Set("one", "two", "three")) sameElements Seq("search1", "search2")
      storage.search(Set("one", "two", "three", "four", "five")) sameElements Seq("search1")
      storage.search(Set("one", "two", "three", "smth-wrong")) sameElements Seq()
      storage.search(Set("aa")) sameElements Seq("search2")
      storage.search(Set("-")) sameElements Seq()
    }
  }
}
