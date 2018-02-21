package net.zanzapla.drone.management.services

import net.zanzapla.drone.management.entities.Drone

import scala.concurrent.{ExecutionContext, Future}

class DroneService(implicit val executionContext: ExecutionContext) {

  var dronesDB = Vector.empty[Drone]

  def getDrones(): Future[List[Drone]] = Future{
    dronesDB.toList
  }

  def getDrone(id: String): Future[Option[Drone]] = Future {
    dronesDB.find(_.id == id.toInt)
  }

  def updateDrone(id: String, payload: Any) = Future {
    List(1,2,3)
  }

}

