package json.facade

import From._
import org.json4s._
import org.json4s.JsonAST._
import org.json4s.JsonDSL._
import org.json4s.jackson.JsonMethods._
import org.json4s.jackson.{JsonMethods, Serialization}

import scala.language.implicitConversions
import scala.util.{Failure, Success, Try}


class Json4SJacksonFacade {

  implicit def lookupReads[T: Manifest](implicit r: Formats = DefaultFormats): ReadF[T] = new ReadF[T] {

    override def read(x: From): Try[T] = for {
      json <- Try {
        x match {
          case FromInputStream(value) => JsonMethods.parse(value)
          case _                      => JsonMethods.parse(x.string)
        }
      }
      v <- Try { json.extract[T] }
    } yield v
  }

  implicit def lookupWrites[T](implicit w: Formats = DefaultFormats): WriteF[T] = new WriteFBase[T] {

    def asString(x: T): String = compact(render(Extraction.decompose(x)))
  }
}

object Json4SJacksonFacade extends Json4SJacksonFacade