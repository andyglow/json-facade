package json.facade

import play.api.libs.json._
import From._

import scala.language.implicitConversions
import scala.util.{Failure, Success, Try}


class PlayFacade {
  import PlayFacade._

  implicit def lookupReads[T](implicit r: Reads[T]): ReadF[T] = new ReadF[T] {

    override def read(x: From): Try[T] = for {
      json  <- Try { Json.parse(x.string) }
      v     <- Try { r.reads(json) } flatMap {
                 case JsSuccess(v, _) => Success(v)
                 case err: JsError    => Failure(PlayJsonError(err))
               }
    } yield v
  }

  implicit def lookupWrites[T](implicit w: Writes[T]): WriteF[T] = new WriteFBase[T] {

    override def asString(x: T): String = Json stringify w.writes(x)
  }
}

object PlayFacade extends PlayFacade {

  final case class PlayJsonError(error: JsError) extends RuntimeException with JsonException {
    override def getMessage: String =
      JsError.toJson(error).toString()
  }
}