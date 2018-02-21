package net.zanzapla.drone.management.resources

import akka.http.scaladsl.server.Route
import net.zanzapla.drone.management.entities.{Question, QuestionUpdate}
import net.zanzapla.drone.management.routing.MyResource
import net.zanzapla.drone.management.services.QuestionService

trait QuestionResource extends MyResource {

  val questionService: QuestionService

  def questionRoutes: Route = pathPrefix("questions") {
    pathEnd {
      post {
        entity(as[Question]) { question =>
          completeWithLocationHeader(
            resourceId = questionService.createQuestion(question),
            ifDefinedStatus = 201, ifEmptyStatus = 409)
          }
        }
    } ~
    path(Segment) { id =>
      get {
        complete(questionService.getQuestion(id))
      } ~
      put {
        entity(as[QuestionUpdate]) { update =>
          complete(questionService.updateQuestion(id, update))
        }
      } ~
      delete {
        complete(questionService.deleteQuestion(id))
      }
    }

  }
}

