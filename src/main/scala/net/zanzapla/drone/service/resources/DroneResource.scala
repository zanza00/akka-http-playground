package net.zanzapla.drone.service.resources

import akka.http.scaladsl.server.Route
import net.zanzapla.drone.service.routing.MyResource
import net.zanzapla.drone.service.services.DroneService
import net.zanzapla.drone.service.entities.DroneUpdate

trait DroneResource extends MyResource {

  val droneService: DroneService

  def droneRoutes: Route = pathPrefix("drones") {
    pathEndOrSingleSlash {
      get {
        complete(droneService.getDrones())
      }
    } ~
      path(IntNumber) { id =>
        get {
          complete(droneService.getDrone(id))
        } ~
          post {
            complete(droneService.updateDrone(id))
          } ~
          put {
            entity(as[DroneUpdate]) { droneUpdate =>
              if (droneService.validateUpdate(droneUpdate)) {
                complete(droneService.updateDroneWithPayload(id, droneUpdate))
              } else {
                droneUpdate.status match {
                  case None => completeWithError(400, "The body of the request has not a [status]")
                  case _ => completeWithError(400, s"invalid status: '${droneUpdate.status.get}'")
                }
              }
            }
          }
      } ~
      path(Segment) { thing =>
        val message = s"invalid ID :$thing"
        get {
          completeWithError(406, message)
        } ~
          post {
            completeWithError(406, message)
          } ~
          put {
            completeWithError(406, message)
          }
      }
  }
}
