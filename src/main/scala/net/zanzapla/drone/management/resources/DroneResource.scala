package net.zanzapla.drone.management.resources

import akka.http.scaladsl.server.Route
import net.zanzapla.drone.management.routing.MyResource
import net.zanzapla.drone.management.services.DroneService
import net.zanzapla.drone.management.entities.DroneUpdate

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
          }
      } ~
    path(Segment) { _ =>
      get{
        completeWithError(406, "invalid ID")
      } ~
        post {
          completeWithError(406, "invalid ID")
        }
    }
  }
}

