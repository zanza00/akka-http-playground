package net.zanzapla.drone.management.resources

import akka.http.scaladsl.server.Route
import net.zanzapla.drone.management.routing.MyResource
import net.zanzapla.drone.management.services.DroneService
import net.zanzapla.drone.management.entities.DroneUpdate

trait DroneResource extends MyResource {

  val droneService: DroneService

  def droneRoutes: Route = pathPrefix("drones") {
    pathEnd {
      get {
        complete(droneService.getDrones())
      }
    } ~
      path(Segment) { id =>
        get {
          complete(droneService.getDrone(id))
        } ~
          post {
            entity(as[DroneUpdate]) { payload =>
              complete(droneService.updateDrone(id, payload))
            }
          }
      }
  }
}

