package net.zanzapla.drone.service

import scala.concurrent.ExecutionContext
import akka.http.scaladsl.server.Route
import net.zanzapla.drone.service.resources.DroneResource
import net.zanzapla.drone.service.services.DroneService

trait RestInterface extends Resources {

  implicit def executionContext: ExecutionContext

  lazy val droneService = new DroneService()

  val routes: Route = droneRoutes

}

trait Resources extends DroneResource

