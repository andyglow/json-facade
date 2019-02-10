package json.facade

import Input._
import spray.json._

import scala.language.implicitConversions
import scala.util.{Failure, Success, Try}


class SprayFacade extends Implementation {

  override type Value = JsValue

  override def parse(x: Input): Try[JsValue] = Try { new JsonParser(ParserInput(x.mkString)).parseJsValue() }

  override def stringify(x: JsValue): String = x.compactPrint

  implicit def lookupReads[T](implicit r: JsonReader[T]): ReadF[T] = new ReadF[T] {

    override def read(x: json.facade.Value): Try[T] = Try { r.read(x.asInstanceOf[Value]) }
  }

  implicit def lookupWrites[T](implicit w: JsonWriter[T]): WriteF[T] = new WriteF[T] {

    override def write(x: T): json.facade.Value = w.write(x).asInstanceOf[json.facade.Value]
  }

  implicit def lookupFormat[T](implicit f: JsonFormat[T]): FormatF[T] = new FormatF[T] {

    override def read(x: json.facade.Value): Try[T] = Try { f.read(x.asInstanceOf[Value]) }

    override def write(x: T): json.facade.Value = f.write(x).asInstanceOf[json.facade.Value]
  }

//  override def deriveR[T]: R[T] = lookupReads(Json.reads[T])
//
//  override def deriveW[T]: W[T] = lookupWrites(Json.writes[T])
//
//  override def deriveF[T]: F[T] = lookupFormat(Json.format[T])
}

object SprayFacade extends SprayFacade