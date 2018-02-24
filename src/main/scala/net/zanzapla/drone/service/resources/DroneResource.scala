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
              validate( droneService.validateUpdate(droneUpdate) , "Invalid Status") {
                complete(droneService.updateDroneWithPayload(id, droneUpdate))
              }
            }
          }
      } ~
      path(Segment) { thing =>
        get {
          completeWithError(406, "invalid ID")
        } ~
          post {
            completeWithError(406, "invalid ID")
          } ~
          put {
            val pattern = "([0-9])+".r
            thing match {
              case pattern(c) => completeWithError(406, "invalid Status")
              case _ => completeWithError(406, "invalid ID")
            }
          }
      }
  }
}
