import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import net.zanzapla.drone.service.resources.DroneResource
import net.zanzapla.drone.service.services.DroneService
import org.scalamock.scalatest.MockFactory
import org.scalatest._

import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext.Implicits.global


class ServiceSpec extends WordSpec with Matchers with ScalatestRouteTest with MockFactory with DroneResource {

  override val droneService: DroneService = mock[DroneService]

  override def executionContext: ExecutionContext = global

  "The service" should {

    "Return an empty list the first time" in {
      Get("/drones") ~> droneRoutes ~> check {
        status shouldBe StatusCodes.OK
      }
    }
  }
}