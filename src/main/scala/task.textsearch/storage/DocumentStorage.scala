package task.textsearch.storage

import java.util.Locale

import scala.collection.mutable

class DocumentStorage {
  val locale = Locale.US

  // by key
  var documents = Map.empty[String, Document]

  // by tokens
  val index = mutable.Map.empty[String, List[Document]]

  def put(document: Document): Unit = {
    documents += (document.key -> document)
    tokenize(document).foreach { token =>
      index.put(token, document +: index.getOrElse(token, Nil))
    }
  }

  def get(key: String): Option[Document] = documents.get(key)

  def exists(key: String): Boolean = documents.contains(key)

  def search(tokens: Set[String]): List[String] = {
    tokens.map {
      token => index.getOrElse(token.toLowerCase(locale), Nil)
    }.reduceLeft(_ intersect _).map(_.key)
  }

  private def tokenize(document: Document): Seq[String] = {
    document.value.replaceAll("[^a-zA-Z0-9\\s+]", " ").toLowerCase(locale).split(' ').distinct
  }
}
