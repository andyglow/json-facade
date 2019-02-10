package json.facade

import Input._
import org.json4s._
import org.json4s.JsonAST._
import org.json4s.JsonDSL._
import org.json4s.jackson.JsonMethods._
import org.json4s.jackson.{JsonMethods, Serialization}

import scala.language.implicitConversions
import scala.util.{Failure, Success, Try}


class Json4SJacksonFacade extends Implementation {

  override type Value = JValue

  override def parse(x: Input): Try[JValue] = Try { JsonMethods.parse(x.mkString) }

  override def stringify(x: JValue): String =  compact(render(x))

  implicit def lookupReads[T: Manifest](implicit r: Formats = DefaultFormats): ReadF[T] = new ReadF[T] {

    override def read(x: json.facade.Value): Try[T] = Try { x.asInstanceOf[Value].extract[T] }
  }

  implicit def lookupWrites[T](implicit w: Formats = DefaultFormats): WriteF[T] = new WriteF[T] {

    override def write(x: T): json.facade.Value = Extraction.decompose(x).asInstanceOf[json.facade.Value]
  }

  implicit def lookupFormat[T: Manifest](implicit f: Formats = DefaultFormats): FormatF[T] = new FormatF[T] {

    override def read(x: json.facade.Value): Try[T] = Try { x.asInstanceOf[Value].extract[T] }

    override def write(x: T): json.facade.Value = Extraction.decompose(x).asInstanceOf[json.facade.Value]
  }

//  override def deriveR[T]: R[T] = lookupReads(Json.reads[T])
//
//  override def deriveW[T]: W[T] = lookupWrites(Json.writes[T])
//
//  override def deriveF[T]: F[T] = lookupFormat(Json.format[T])
}

object Json4SJacksonFacade extends Json4SJacksonFacade