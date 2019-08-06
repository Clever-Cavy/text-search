package task.textsearch.server

import akka.actor.ActorRef
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.{get, post}
import akka.http.scaladsl.server.directives.PathDirectives.path
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import task.textsearch.api.{ActionPerformed, Document, Documents, JsonSupport}
import task.textsearch.actor.message._
import task.textsearch.storage

import scala.concurrent.{ExecutionContext, Future}

trait DocumentRoutes extends JsonSupport with LazyLogging {

  import DocumentRoutes._

  def registryActor: ActorRef

  implicit def executionContext: ExecutionContext

  implicit def timeout: Timeout

  lazy val documentRoutes: Route =
    pathPrefix("storage") {
      pathPrefix("documents") {
        pathEnd {
          post {
            withSizeLimit(OneMb) {
              entity(as[Document]) { document =>
                val documentCreated: Future[DocumentAdded] =
                  (registryActor ? PutDocument(storage.Document(document.key, document.value))).mapTo[DocumentAdded]
                onSuccess(documentCreated) { performed =>
                  logger.info("Created document [{}]: {}", document.key, performed.description)
                  complete((StatusCodes.Created, ActionPerformed(performed.description)))
                }
              }
            }
          }
        } ~
          path(Segment) { key =>
            get {
              validate(key.length < MaxKeyLength, s"key is too long: '${key.length}' symbols. Max allowed: $MaxKeyLength") {
                val maybeDocument: Future[Option[Document]] =
                  (registryActor ? GetDocument(key)).mapTo[Option[Document]]
                rejectEmptyResponse(
                  complete(maybeDocument)
                )
              }
            }
          }
      } ~ path("search") {
        get {
          parameter("tokens") { tokens =>
            validate(tokens.length < MaxTokenLength, s"tokens too long: '${tokens.length}' symbols. Max allowed: $MaxTokenLength") {
              val tokensSet = tokens.split(',').toSet
              validate(tokensSet.size < MaxTokensCount, s"too many tokens: '${tokensSet.size}'. Max allowed: $MaxTokensCount") {
                val documents: Future[Documents] =
                  (registryActor ? Search(tokensSet)).mapTo[SearchResult]
                    .map(result => Documents(result.documents))
                complete(documents)
              }
            }
          }
        }
      }
    }
}

object DocumentRoutes {
  val OneMb = 1000000
  val MaxKeyLength = 128
  val MaxTokenLength = 1000
  val MaxTokensCount = 50
}