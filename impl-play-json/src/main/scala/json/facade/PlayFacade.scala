package json.facade

import play.api.libs.json._
import Input._

import scala.language.implicitConversions
import scala.util.{Failure, Success, Try}


class PlayFacade extends Implementation {
  import PlayFacade._

  override type Value = JsValue

  override def parse(x: Input): Try[JsValue] = Try { Json.parse(x.mkString) }

  override def stringify(x: JsValue): String = Json.stringify(x)

  implicit def lookupReads[T](implicit r: Reads[T]): ReadF[T] = new ReadF[T] {

    override def read(x: json.facade.Value): Try[T] = r.reads(x.asInstanceOf[Value]) match {
      case JsSuccess(v, _) => Success(v)
      case err: JsError => Failure(PlayJsonError(err))
    }
  }

  implicit def lookupWrites[T](implicit w: Writes[T]): WriteF[T] = new WriteF[T] {

    override def write(x: T): json.facade.Value = w.writes(x).asInstanceOf[json.facade.Value]
  }

  implicit def lookupFormat[T](implicit f: Format[T]): FormatF[T] = new FormatF[T] {

    override def read(x: json.facade.Value): Try[T] = f.reads(x.asInstanceOf[Value]) match {
      case JsSuccess(v, _) => Success(v)
      case err: JsError => Failure(PlayJsonError(err))
    }

    override def write(x: T): json.facade.Value = f.writes(x).asInstanceOf[json.facade.Value]
  }

//  override def deriveR[T]: R[T] = lookupReads(Json.reads[T])
//
//  override def deriveW[T]: W[T] = lookupWrites(Json.writes[T])
//
//  override def deriveF[T]: F[T] = lookupFormat(Json.format[T])
}

object PlayFacade extends PlayFacade {

  final case class PlayJsonError(error: JsError) extends RuntimeException {
    override def getMessage: String =
      JsError.toJson(error).toString()
  }
}