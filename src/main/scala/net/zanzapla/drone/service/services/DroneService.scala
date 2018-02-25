package net.zanzapla.drone.service.services

import java.util.Date

import net.zanzapla.drone.service.entities.{Drone, DroneUpdate}

import scala.concurrent.{ExecutionContext, Future}

class DroneService(implicit val executionContext: ExecutionContext) {

  var dronesDB = Vector.empty[Drone]

  val IN = "IN"
  val OUT = "OUT"
  val allowedStatus = List(IN, OUT);

  def getDrones(): Future[List[Drone]] = Future {
    dronesDB.toList
  }

  def getDrone(id: Int): Future[Option[Drone]] = Future {
    dronesDB.find(_.id == id)
  }

  def createDrone(drone: Drone): Future[Option[Drone]] = Future {
    dronesDB.find(_.id == drone.id) match {
      case Some(_) => None // Conflict! id is already taken
      case None =>
        dronesDB = dronesDB :+ drone
        Some(drone)
    }
  }

  def updateDrone(id: Int): Future[Option[Drone]] = {

    def changeStatus(drone: Drone): Drone = {
      val status: String = drone.status match {
        case OUT => IN
        case IN => OUT
        case _ => OUT
      }
      val lastSeen = new Date()
      Drone(id, status, lastSeen)
    }


    getDrone(id).flatMap {
      case None => createDrone(Drone(id.toInt, OUT, new Date())) // Not Found needs to be created
      case Some(drone) =>
        val updatedDrone = changeStatus(drone)
        deleteDrone(id).flatMap(_ =>
          createDrone(updatedDrone).map(_ => Some(updatedDrone))
        )
    }

  }

  def updateDroneWithPayload(id: Int, update: DroneUpdate): Future[Option[Drone]] = {

    def updateEntity(drone: Drone): Drone = {
      val status = update.status.getOrElse(drone.status)
      Drone(id, status, drone.lastSeen)
    }

    getDrone(id).flatMap {
      case None => Future {
        None
      }
      case Some(drone) =>
        val updatedDrone = updateEntity(drone)
        deleteDrone(id).flatMap(_ =>
          createDrone(updatedDrone).map(_ => Some(updatedDrone))
        )
    }

  }

  def validateUpdate(update: DroneUpdate): Boolean = {
    update.status.getOrElse("default") match {
      case IN => true
      case OUT => true
      case _ => false
    }
  }

  def deleteDrone(id: Int): Future[Unit] = Future {
    dronesDB = dronesDB.filterNot(_.id == id)
  }

}

