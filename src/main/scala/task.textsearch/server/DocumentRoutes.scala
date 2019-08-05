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
import task.textsearch.model.{ActionPerformed, Document, Documents, JsonSupport}
import task.textsearch.server.storage.message._

import scala.concurrent.{ExecutionContext, Future}

trait DocumentRoutes extends JsonSupport with LazyLogging {

  def registryActor: ActorRef

  implicit def executionContext: ExecutionContext

  implicit def timeout: Timeout

  lazy val documentRoutes: Route =
    pathPrefix("storage") {
      path("documents") {
        post {
          entity(as[Document]) { document =>
            // TODO some validation
            val documentCreated: Future[DocumentAdded] =
              (registryActor ? PutDocument(storage.Document(document.key, document.value))).mapTo[DocumentAdded]
            onSuccess(documentCreated) { performed =>
              logger.info("Created document [{}]: {}", document.key, performed.description)
              complete((StatusCodes.Created, ActionPerformed(performed.description)))
            }
          }
        } ~
          get {
            // TODO it should be path segment
            parameter("key") { key =>
              // TODO some validation
              val maybeDocument: Future[Option[Document]] =
                (registryActor ? GetDocument(key)).mapTo[Option[Document]]
              complete(maybeDocument)
            }
          }
      } ~ path("search") {
        get {
          parameter("tokens") { tokens =>
            val tokensSet = tokens.split(',').toSet // TODO some validation
          val documents: Future[Documents] =
            (registryActor ? Search(tokensSet)).mapTo[SearchResult]
                .map(result => Documents(result.documents))
            complete(documents)
          }
        }
      }
    }
}