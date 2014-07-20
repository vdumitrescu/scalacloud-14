import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import play.api.mvc.SimpleResult

import play.api.test._
import play.api.test.Helpers._

import scala.concurrent.Future

@RunWith(classOf[JUnitRunner])
class ApplicationSpec extends Specification {

  val SomeName = Some("Party")
  val SomeUrl = Some("http://www.example.com/video")
  val SomeDuration = Some("12")

  val SomeEmptyName = Some(" ")
  val SomeShortUrl = Some("http://hm")
  val SomeBadDuration = Some("15m")

  val ErrorMessage = "Missing ['name','duration','url']."


  "Application" should {

    "respond with 404 on a bad request" in new WithApplication {
      route(FakeRequest(GET, "/boom")) must beNone
    }

    "respond with 200 and an empty list" in new WithApplication {
      val result = route(FakeRequest(GET, "/videos")).get

      status(result) should equalTo(OK)
      contentType(result) should beSome("text/plain")
      contentAsString(result) must beEmpty
    }

    "respond with 400 if the name parameter is missing" in new WithApplication {
      checkErrorResponse(route(FakeRequest(POST, makeUrl(None, SomeUrl, SomeDuration))).get)
    }

    "respond with 400 if the name parameter is empty" in new WithApplication {
      checkErrorResponse(route(FakeRequest(POST, makeUrl(SomeEmptyName, SomeUrl, SomeDuration))).get)
    }

    "respond with 400 if the url parameter is missing" in new WithApplication {
      checkErrorResponse(route(FakeRequest(POST, makeUrl(SomeName, None, SomeDuration))).get)
    }

    "respond with 400 if the url parameter is too short" in new WithApplication {
      checkErrorResponse(route(FakeRequest(POST, makeUrl(SomeName, SomeShortUrl, SomeDuration))).get)
    }

    "respond with 400 if the duration parameter is missing" in new WithApplication {
      checkErrorResponse(route(FakeRequest(POST, makeUrl(SomeName, SomeUrl, None))).get)
    }

    "respond with 400 if the duration parameter is not a number" in new WithApplication {
      checkErrorResponse(route(FakeRequest(POST, makeUrl(SomeEmptyName, SomeUrl, SomeBadDuration))).get)
    }

    "respond with 200 if all parameters are fine" in new WithApplication {
      val result = route(FakeRequest(POST, makeUrl(SomeName, SomeUrl, SomeDuration))).get

      status(result) should equalTo(OK)
      contentType(result) should beSome("text/plain")
      contentAsString(result) must equalTo("Video added.")

      val list = route(FakeRequest(GET, "/videos")).get

      status(list) should equalTo(OK)
      contentType(list) should beSome("text/plain")
      contentAsString(list) must equalTo(s"${SomeName.get}: ${SomeUrl.get}\n")
    }
  }

  private[this] def checkErrorResponse(result: Future[SimpleResult]) = {
    status(result) must equalTo(BAD_REQUEST)
    contentType(result) must beSome("text/plain")
    contentAsString(result) must equalTo(ErrorMessage)
  }

  private[this] def makeUrl(name: Option[String], url: Option[String], duration: Option[String]) = {

    def mkParam(paramName: String, paramValue: Option[String]) =
      paramValue map (paramName + "=" + _) getOrElse ""

    "/videos?" + List(mkParam("name", name), mkParam("url", url), mkParam("duration", duration)).mkString("&")
  }
}
