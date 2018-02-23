import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import net.zanzapla.drone.service.entities.Drone
import net.zanzapla.drone.service.resources.DroneResource
import net.zanzapla.drone.service.services.DroneService
import org.scalamock.scalatest.MockFactory
import org.scalatest._
import akka.http.scaladsl.model.ContentTypes._

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.ExecutionContext.Implicits.global


class ServiceSpec extends WordSpec with Matchers with ScalatestRouteTest with MockFactory with DroneResource {

  override val droneService: DroneService = mock[DroneService]

  override def executionContext: ExecutionContext = global

  "The service" should {

    "Return an empty list the first time" in {
      (droneService.getDrones _).expects().returning(Future{List()})
      Get("/drones") ~> droneRoutes ~> check {
        status shouldBe StatusCodes.OK
      }
    }

    "Return the list of the drones if there are any" in {
      (droneService.getDrones _).expects().returning(Future{List(Drone(1, "OUT"), Drone(2, "IN"))})
      Get("/drones") ~> droneRoutes ~> check {
        status shouldBe StatusCodes.OK
        responseAs[List[Drone]] shouldEqual List(Drone(1, "OUT"), Drone(2, "IN"))
        contentType shouldBe `application/json`
      }
    }


    "Return an error if the drone is not yet seen" in {
      (droneService.getDrone _).expects(1).returning(Future{None})
      Get("/drones/1") ~> droneRoutes ~> check {
        status shouldBe StatusCodes.NotFound
        contentType shouldBe `application/json`
      }
    }

    "Return an error if the id is a string" in {
      Get("/drones/this-is-a-string") ~> droneRoutes ~> check {
        status shouldBe StatusCodes.NotAcceptable
        responseAs[Map[String, String]] shouldEqual Map("error"-> "invalid ID" )
      }
    }

    "Can create an entry for the drone 1 and then check it" in {
        (droneService.updateDrone _).expects(1).returning(Future{Some(Drone(1, "OUT"))})
        Post("/drones/1") ~> droneRoutes ~> check {
          status shouldBe StatusCodes.OK
          responseAs[Drone] shouldEqual Drone(1, "OUT")
          contentType shouldBe `application/json`
        }
        (droneService.getDrone _).expects(1).returning(Future{Some(Drone(1, "OUT"))})
        Get("/drones/1") ~> droneRoutes ~> check {
          status shouldBe StatusCodes.OK
          responseAs[Drone] shouldEqual Drone(1, "OUT")
          contentType shouldBe `application/json`
        }
    }

    "Can create an entry for the drone 2,then update it" in {
      (droneService.updateDrone _).expects(2).returning(Future{Some(Drone(2, "OUT"))})
      Post("/drones/2") ~> droneRoutes ~> check {
        status shouldBe StatusCodes.OK
        responseAs[Drone] shouldEqual Drone(2, "OUT")
        contentType shouldBe `application/json`
      }
      (droneService.updateDrone _).expects(2).returning(Future{Some(Drone(2, "IN"))})
      Post("/drones/2") ~> droneRoutes ~> check {
        status shouldBe StatusCodes.OK
        responseAs[Drone] shouldEqual Drone(2, "IN")
        contentType shouldBe `application/json`
      }
    }
  }
}