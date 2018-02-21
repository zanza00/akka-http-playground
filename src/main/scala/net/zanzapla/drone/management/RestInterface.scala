package net.zanzapla.drone.management

import scala.concurrent.ExecutionContext
import akka.http.scaladsl.server.Route
import net.zanzapla.drone.management.resources.DroneResource
import net.zanzapla.drone.management.services.DroneService

trait RestInterface extends Resources {

  implicit def executionContext: ExecutionContext

  lazy val droneService = new DroneService()

  val routes: Route = droneRoutes

}

trait Resources extends DroneResource

