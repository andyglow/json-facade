package json.facade

import io.circe._, io.circe.generic.auto._, io.circe.parser.{parse => parseJson, _}, io.circe.syntax._
import From._

import scala.language.implicitConversions
import scala.util.{Failure, Success, Try}


class CirceFacade {

  implicit def lookupReads[T](implicit r: Decoder[T]): ReadF[T] = new ReadF[T] {

    override def read(x: From): Try[T] = for {
      json <- parseJson(x.string).fold(Failure.apply, Success.apply)
      v <- r.decodeJson(json) match {
        case Right(v)  => Success(v)
        case Left(err) => Failure(err)
      }
    } yield v
  }

  implicit def lookupWrites[T](implicit w: Encoder[T]): WriteF[T] = new WriteFBase[T] {

    override def asString(x: T): String = w(x).noSpaces
  }
}

object CirceFacade extends CirceFacade