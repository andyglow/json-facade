package json.facade

import akka.actor.ActorSystem
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model.ContentTypes.`application/json`
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.{ RejectionError, ValidationRejection }
import akka.http.scaladsl.unmarshalling.Unmarshaller.UnsupportedContentTypeException
import akka.http.scaladsl.unmarshalling.{ Unmarshal, Unmarshaller }
import akka.stream.ActorMaterializer
import org.scalatest.{ AsyncWordSpec, BeforeAndAfterAll, Matchers }
import play.api.libs.json.{ Format, Json }
import scala.collection.immutable.Seq
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt


final class AkkaHttpAdapterSpec extends AsyncWordSpec with Matchers with BeforeAndAfterAll {
  import PlayFacade._
  import AkkaHttpAdapter._
  import AkkaHttpAdapterSpec._

  private implicit val system = ActorSystem()
  private implicit val mat    = ActorMaterializer()

  "PlayJsonSupport" should {
    "enable marshalling and unmarshalling objects for which `Writes` and `Reads` exist" in {
      val foo = Foo("bar")
      Marshal(foo)
        .to[RequestEntity]
        .flatMap(Unmarshal(_).to[Foo])
        .map(_ shouldBe foo)
    }

    "provide proper error messages for requirement errors" in {
      val entity = HttpEntity(MediaTypes.`application/json`, """{ "bar": "baz" }""")
      Unmarshal(entity)
        .to[Foo]
        .failed
        .map(_ should have message "requirement failed: bar must be 'bar'!")
    }

    "provide stringified error representation for parsing errors" in {
      val entity = HttpEntity(MediaTypes.`application/json`, """{ "bar": 5 }""")
      Unmarshal(entity)
        .to[Foo]
        .failed
        .map({ err =>
          err shouldBe a[RejectionError]
          err match {
            case RejectionError(ValidationRejection(message, Some(PlayJsonError(error)))) =>
              message should be("""{"obj.bar":[{"msg":["error.expected.jsstring"],"args":[]}]}""")
              error.errors should have length 1
              error.errors.head._1.toString() should be("/bar")
              error.errors.head._2.flatMap(_.messages) should be(Seq("error.expected.jsstring"))
            case _ => fail("Did not throw correct validation error.")
          }
        })
    }

    "fail with NoContentException when unmarshalling empty entities" in {
      val entity = HttpEntity.empty(`application/json`)
      Unmarshal(entity)
        .to[Foo]
        .failed
        .map(_ shouldBe Unmarshaller.NoContentException)
    }

    "fail with UnsupportedContentTypeException when Content-Type is not `application/json`" in {
      val entity = HttpEntity("""{ "bar": "bar" }""")
      Unmarshal(entity)
        .to[Foo]
        .failed
        .map(_ shouldBe UnsupportedContentTypeException(`application/json`))
    }

    "allow unmarshalling with passed in Content-Types" in {
      val foo = Foo("bar")
      val `application/json-home` =
        MediaType.applicationWithFixedCharset("json-home", HttpCharsets.`UTF-8`, "json-home")

      final object CustomAkkaHttpAdapterSupport extends AkkaHttpAdapter {
        override def unmarshallerContentTypes = List(`application/json`, `application/json-home`)
      }
      import CustomAkkaHttpAdapterSupport._

      val entity = HttpEntity(`application/json-home`, """{ "bar": "bar" }""")
      Unmarshal(entity).to[Foo].map(_ shouldBe foo)
    }
  }

  override protected def afterAll() = {
    Await.ready(system.terminate(), 42.seconds)
    super.afterAll()
  }
}

object AkkaHttpAdapterSpec {

  final case class Foo(bar: String) {
    require(bar == "bar", "bar must be 'bar'!")
  }

  implicit val fooFormat: Format[Foo] =
    Json.format[Foo]
}