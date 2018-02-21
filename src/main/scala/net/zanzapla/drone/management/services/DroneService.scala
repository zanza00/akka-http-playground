package net.zanzapla.drone.management.services

import net.zanzapla.drone.management.entities.Drone

import scala.concurrent.{ExecutionContext, Future}

class DroneService(implicit val executionContext: ExecutionContext) {

  var dronesDB = Vector.empty[Drone]

  def getDrones(): Future[List[Drone]] = Future {
    dronesDB.toList
  }

  def getDrone(id: String): Future[Option[Drone]] = Future {
    dronesDB.find(_.id == id.toInt)
  }

  def createDrone(drone: Drone): Future[Option[Drone]] = Future {
    dronesDB.find(_.id == drone.id) match {
      case Some(d) => None // Conflict! id is already taken
      case None =>
        dronesDB = dronesDB :+ drone
        Some(drone)
    }
  }

  def updateDrone(id: String): Future[Option[Drone]] =  {

    def updateEntity(drone: Drone): Drone = {
      val status: String = drone.status match {
        case "OUT" => "IN"
        case "IN" => "OUT"
      }
      Drone(id.toInt, status)
    }


    getDrone(id).flatMap { maybeDrone =>
      maybeDrone match {
        case None => createDrone(Drone(id.toInt, "OUT")) // Not Found needs to be created
        case Some(drone) =>
          val updatedDrone = updateEntity(drone)
          deleteDrone(id).flatMap(_ =>
            createDrone(updatedDrone).map(_ => Some(updatedDrone))
          )
      }
    }

  }

  def deleteDrone(id: String): Future[Unit] = Future {
    dronesDB = dronesDB.filterNot(_.id == id.toInt)
  }

}

