import java.util.Date

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

  val date1: Date = new Date(1519574400000L)
  val date2: Date = new Date(1519578000000L)

  "The service" should {

    "return an empty list when there are no known drone" in {
      (droneService.getDrones _).expects().returning(Future {
        List()
      })
      Get("/drones") ~> droneRoutes ~> check {
        contentType shouldBe `application/json`
        responseAs[List[Drone]] shouldEqual List()
        status shouldBe StatusCodes.OK
      }
    }

    "return the list of the drones if there are any" in {
      (droneService.getDrones _).expects().returning(Future {
        List(Drone(1, "OUT", date1), Drone(2, "IN", date2))
      })
      Get("/drones") ~> droneRoutes ~> check {
        contentType shouldBe `application/json`
        responseAs[List[Drone]] shouldEqual List(Drone(1, "OUT", date1), Drone(2, "IN", date2))
        status shouldBe StatusCodes.OK
      }
    }

    "return an error if the drone is not yet seen" in {
      (droneService.getDrone _).expects(1).returning(Future {
        None
      })
      Get("/drones/1") ~> droneRoutes ~> check {
        contentType shouldBe `application/json`
        status shouldBe StatusCodes.NotFound
      }
    }

    "return an error if the id is a string" in {
      Get("/drones/this-is-a-string") ~> droneRoutes ~> check {
        contentType shouldBe `application/json`
        responseAs[Map[String, String]] shouldEqual Map("error" -> "invalid ID :this-is-a-string")
        status shouldBe StatusCodes.NotAcceptable
      }
      Post("/drones/this-is-a-string") ~> droneRoutes ~> check {
        contentType shouldBe `application/json`
        responseAs[Map[String, String]] shouldEqual Map("error" -> "invalid ID :this-is-a-string")
        status shouldBe StatusCodes.NotAcceptable
      }
    }

    "create an entry for the drone 1 and then check it" in {
      (droneService.updateDrone _).expects(1).returning(Future {
        Some(Drone(1, "OUT", date1))
      })
      Post("/drones/1") ~> droneRoutes ~> check {
        contentType shouldBe `application/json`
        responseAs[Drone] shouldEqual Drone(1, "OUT", date1)
        status shouldBe StatusCodes.OK
      }
      (droneService.getDrone _).expects(1).returning(Future {
        Some(Drone(1, "OUT", date1))
      })
      Get("/drones/1") ~> droneRoutes ~> check {
        contentType shouldBe `application/json`
        responseAs[Drone] shouldEqual Drone(1, "OUT", date1)
        status shouldBe StatusCodes.OK
      }
    }

    "update an entry for the drone 2" in {
      (droneService.updateDrone _).expects(2).returning(Future {
        Some(Drone(2, "OUT", date1))
      })
      Post("/drones/2") ~> droneRoutes ~> check {
        contentType shouldBe `application/json`
        responseAs[Drone] shouldEqual Drone(2, "OUT", date1)
        status shouldBe StatusCodes.OK
      }
      (droneService.updateDrone _).expects(2).returning(Future {
        Some(Drone(2, "IN", date1))
      })
      Post("/drones/2") ~> droneRoutes ~> check {
        contentType shouldBe `application/json`
        responseAs[Drone] shouldEqual Drone(2, "IN", date1)
        status shouldBe StatusCodes.OK
      }
    }
    "update an entry for the drone 2 with a specific status" in {
      (droneService.validateUpdate _).expects(*).returns(true)
      (droneService.updateDroneWithPayload _).expects(2, DroneUpdate(status = Some("OUT"))).returning(Future {
        Some(Drone(2, "OUT", date1))
      })

      Put("/drones/2", HttpEntity(`application/json`, """{ "status": "OUT" }""")) ~> droneRoutes ~> check {
        contentType shouldBe `application/json`
        responseAs[Drone] shouldEqual Drone(2, "OUT", date1)
        status shouldBe StatusCodes.OK
      }
    }
    "returns an error message if the status is incorrect" in {
      (droneService.validateUpdate _).expects(*).returns(false)

      Put("/drones/2", HttpEntity(`application/json`, """{ "status": "wrong" }""")) ~> droneRoutes ~> check {
        contentType shouldBe `application/json`
        responseAs[Map[String, String]] shouldEqual Map("error" -> "invalid status: 'wrong'")
        status shouldBe StatusCodes.BadRequest
      }
    }
    "returns an error message with empty body" in {
      (droneService.validateUpdate _).expects(*).returns(false)

      Put("/drones/2", HttpEntity(`application/json`, """{}""")) ~> droneRoutes ~> check {
        contentType shouldBe `application/json`
        responseAs[Map[String, String]] shouldEqual Map("error" -> "The body of the request has not a [status]")
        status shouldBe StatusCodes.BadRequest
      }
    }
    "returns an error message with wrong body" in {
      (droneService.validateUpdate _).expects(*).returns(false)

      Put("/drones/2", HttpEntity(`application/json`, """{ "not-my-key": "not-my-value" }""")) ~> droneRoutes ~> check {
        contentType shouldBe `application/json`
        responseAs[Map[String, String]] shouldEqual Map("error" -> "The body of the request has not a [status]")
        status shouldBe StatusCodes.BadRequest
      }
    }
  }
}