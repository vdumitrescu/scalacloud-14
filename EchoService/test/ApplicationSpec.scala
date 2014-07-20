import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.test._
import play.api.test.Helpers._

@RunWith(classOf[JUnitRunner])
class ApplicationSpec extends Specification {

  "Application" should {

    "respond with 404 on a bad request" in new WithApplication{
      route(FakeRequest(GET, "/boom")) must beNone
    }

    "respond with 200 and the provided message" in new WithApplication {
      val someMessage = "This is a message"
      val response = route(FakeRequest(GET, s"/echo?msg=$someMessage")).get

      status(response) must equalTo(OK)
      contentType(response) must beSome("text/plain")
      contentAsString(response) must equalTo(s"Echo: $someMessage")
    }

    "respond with 400 if msg parameter is missing" in new WithApplication{
      val response = route(FakeRequest(GET, "/echo")).get

      status(response) must equalTo(BAD_REQUEST)
      contentType(response) must beSome("text/plain")
      contentAsString(response) must equalTo("Missing 'msg' query parameter.")
    }
  }
}
