import akka.http.scaladsl.model.{HttpEntity, StatusCodes}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import net.zanzapla.drone.service.entities.{Drone, DroneUpdate}
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

    "Return an empty list when there are no known drone" in {
      (droneService.getDrones _).expects().returning(Future {
        List()
      })
      Get("/drones") ~> droneRoutes ~> check {
        contentType shouldBe `application/json`
        responseAs[List[Drone]] shouldEqual List()
        status shouldBe StatusCodes.OK
      }
    }

    "Return the list of the drones if there are any" in {
      (droneService.getDrones _).expects().returning(Future {
        List(Drone(1, "OUT"), Drone(2, "IN"))
      })
      Get("/drones") ~> droneRoutes ~> check {
        contentType shouldBe `application/json`
        responseAs[List[Drone]] shouldEqual List(Drone(1, "OUT"), Drone(2, "IN"))
        status shouldBe StatusCodes.OK
      }
    }

    "Return an error if the drone is not yet seen" in {
      (droneService.getDrone _).expects(1).returning(Future {
        None
      })
      Get("/drones/1") ~> droneRoutes ~> check {
        contentType shouldBe `application/json`
        status shouldBe StatusCodes.NotFound
      }
    }

    "Return an error if the id is a string" in {
      Get("/drones/this-is-a-string") ~> droneRoutes ~> check {
        contentType shouldBe `application/json`
        responseAs[Map[String, String]] shouldEqual Map("error" -> "invalid ID")
        status shouldBe StatusCodes.NotAcceptable
      }
      Post("/drones/this-is-a-string") ~> droneRoutes ~> check {
        contentType shouldBe `application/json`
        responseAs[Map[String, String]] shouldEqual Map("error" -> "invalid ID")
        status shouldBe StatusCodes.NotAcceptable
      }
    }

    "Can create an entry for the drone 1 and then check it" in {
      (droneService.updateDrone _).expects(1).returning(Future {
        Some(Drone(1, "OUT"))
      })
      Post("/drones/1") ~> droneRoutes ~> check {
        contentType shouldBe `application/json`
        responseAs[Drone] shouldEqual Drone(1, "OUT")
        status shouldBe StatusCodes.OK
      }
      (droneService.getDrone _).expects(1).returning(Future {
        Some(Drone(1, "OUT"))
      })
      Get("/drones/1") ~> droneRoutes ~> check {
        contentType shouldBe `application/json`
        responseAs[Drone] shouldEqual Drone(1, "OUT")
        status shouldBe StatusCodes.OK
      }
    }

    "Can update an entry for the drone 2" in {
      (droneService.updateDrone _).expects(2).returning(Future {
        Some(Drone(2, "OUT"))
      })
      Post("/drones/2") ~> droneRoutes ~> check {
        contentType shouldBe `application/json`
        responseAs[Drone] shouldEqual Drone(2, "OUT")
        status shouldBe StatusCodes.OK
      }
      (droneService.updateDrone _).expects(2).returning(Future {
        Some(Drone(2, "IN"))
      })
      Post("/drones/2") ~> droneRoutes ~> check {
        contentType shouldBe `application/json`
        responseAs[Drone] shouldEqual Drone(2, "IN")
        status shouldBe StatusCodes.OK
      }
    }
    "Can update an entry for the drone 2 with a specific status" in {
      (droneService.validateUpdate _).expects(*).returns(true)
      (droneService.updateDroneWithPayload _).expects(2, DroneUpdate(status = Some("OUT"))).returning(Future {
        Some(Drone(2, "OUT"))
      })

      Put("/drones/2", HttpEntity(`application/json`, """{ "status": "OUT" }""")) ~> droneRoutes ~> check {
        contentType shouldBe `application/json`
        responseAs[Drone] shouldEqual Drone(2, "OUT")
        status shouldBe StatusCodes.OK
      }
    }
  }
}