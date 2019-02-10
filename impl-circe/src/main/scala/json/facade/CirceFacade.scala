package json.facade

import io.circe._, io.circe.generic.auto._, io.circe.parser.{parse => parseJson, _}, io.circe.syntax._
import Input._

import scala.language.implicitConversions
import scala.util.{Failure, Success, Try}


class CirceFacade extends Implementation {

  override type Value = Json

  override def parse(x: Input): Try[Json] = parseJson(x.mkString).fold(Failure.apply, Success.apply)

  override def stringify(x: Json): String = x.noSpaces

  implicit def lookupReads[T](implicit r: Decoder[T]): ReadF[T] = new ReadF[T] {

    override def read(x: json.facade.Value): Try[T] = r.decodeJson(x.asInstanceOf[Value]) match {
      case Right(v) => Success(v)
      case Left(err) => Failure(err)
    }
  }

  implicit def lookupWrites[T](implicit w: Encoder[T]): WriteF[T] = new WriteF[T] {

    override def write(x: T): json.facade.Value = w(x).asInstanceOf[json.facade.Value]
  }

  implicit def lookupFormat[T](implicit e: Encoder[T], d: Decoder[T]): FormatF[T] = new FormatF[T] {

    override def read(x: json.facade.Value): Try[T] = d.decodeJson(x.asInstanceOf[Value]) match {
      case Right(v) => Success(v)
      case Left(err) => Failure(err)
    }

    override def write(x: T): json.facade.Value = e(x).asInstanceOf[json.facade.Value]
  }
}

object CirceFacade extends CirceFacade