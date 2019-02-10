package json.facade

import From._
import spray.json._

import scala.language.implicitConversions
import scala.util.{Failure, Success, Try}


class SprayFacade {

  implicit def lookupReads[T](implicit r: JsonReader[T]): ReadF[T] = new ReadF[T] {

    override def read(x: From): Try[T] = for {
      json  <- Try { new JsonParser(ParserInput(x.string)).parseJsValue() }
      v     <- Try { r.read(json) }
    } yield v
  }

  implicit def lookupWrites[T](implicit w: JsonWriter[T]): WriteF[T] = new WriteFBase[T] {

    override def asString(x: T): String = w.write(x).compactPrint
  }
}

object SprayFacade extends SprayFacade