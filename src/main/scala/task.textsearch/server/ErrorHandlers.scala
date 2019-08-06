package task.textsearch.server

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._

trait ErrorHandlers {
  implicit def rejectionHandler = RejectionHandler.newBuilder()
    .handle {
      case ValidationRejection(msg, _) =>
        complete((InternalServerError, s"That wasn't valid! $msg"))
    }.handleAll[MethodRejection] {
    methodRejections =>
      val names = methodRejections.map(_.supported.name)
      complete((MethodNotAllowed, s"Can't do that! Supported: ${names mkString " or "}!"))
  }.handleNotFound {
    extractUnmatchedPath { path =>
      complete((NotFound, s"The document you requested [$path] does not exist."))
    }
  }
    .result()
}
