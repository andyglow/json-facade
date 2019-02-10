package json.facade

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives
import akka.stream.{ ActorMaterializer, Materializer }
import play.api.libs.json.{ Format, Json }
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.io.StdIn
import AkkaHttpAdapterSpec._


object ExampleApp {

  final object Foo {
    implicit val fooFormat: Format[Foo] = Json.format[Foo]
  }
  final case class ExampleApp(bar: String)

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem()
    implicit val mat    = ActorMaterializer()

    Http().bindAndHandle(route, "127.0.0.1", 8000)

    StdIn.readLine("Hit ENTER to exit")
    Await.ready(system.terminate(), Duration.Inf)
  }

  def route(implicit mat: Materializer) = {
    import Directives._
    import PlayFacade._
    import AkkaHttpAdapter._

    pathSingleSlash {
      post {
        entity(as[Foo]) { foo =>
          complete {
            foo
          }
        }
      }
    }
  }
}