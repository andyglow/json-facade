package json.facade

import Input._

import scala.language.implicitConversions
import scala.util.{Failure, Success, Try}
import upickle.default._

import scala.util.control.NoStackTrace

class UJsonFacade extends Implementation with Direct {
  import UJsonFacade._

  override type Value = ujson.Value

  override def parse(x: Input): Try[Value] = Try { ujson.read(x.mkString) }

  override def directParse[T: ReadF](x: Input): Try[T] = for {
    r <- readerOf[T]
    t <- Try { upickle.default.read(x.mkString)(r) }
  } yield t

  override def directStringify[T: WriteF](t: T): String = {
    val w = writerOf[T].get
    upickle.default.write(t)(w)
  }

  override def stringify(x: Value): String = x.render()

  implicit def lookupReads[T](implicit r: Reader[T]): ReadF[T] = new ReadFacade[T](r)

  implicit def lookupWrites[T](implicit w: Writer[T]): WriteF[T] = new WriteFacade[T](w)

  implicit def lookupFormat[T](implicit rw: ReadWriter[T]): FormatF[T] = new FormatFacade[T](rw)
}

object UJsonFacade extends UJsonFacade {

  class ReadFacade[T](val r: Reader[T]) extends ReadF[T] {
    def read(x: json.facade.Value): Try[T] = Try { upickle.default.read(x.asInstanceOf[Value])(r) }
  }

  class WriteFacade[T](val w: Writer[T]) extends WriteF[T] {
    def write(x: T): json.facade.Value = writeJs(x)(w).asInstanceOf[json.facade.Value]
  }

  class FormatFacade[T](val rw: ReadWriter[T]) extends FormatF[T] {
    def read(x: json.facade.Value): Try[T] = Try { upickle.default.read(x.asInstanceOf[Value])(rw) }
    def write(x: T): json.facade.Value = writeJs(x)(rw).asInstanceOf[json.facade.Value]
  }

  private def readerOf[T](implicit x: ReadF[T]): Try[Reader[T]] = x match {
    case x: FormatFacade[T] => Success(x.rw)
    case x: ReadFacade[T]   => Success(x.r)
    case _                  => Failure(new Exception(s"ReadF of type ${x.getClass} is unknown from UJsonFacade perspective") with NoStackTrace)
  }

  private def writerOf[T](implicit x: WriteF[T]): Try[Writer[T]] = x match {
    case x: FormatFacade[T] => Success(x.rw)
    case x: WriteFacade[T]  => Success(x.w)
    case _                  => Failure(new Exception(s"WriteF of type ${x.getClass} is unknown from UJsonFacade perspective") with NoStackTrace)
  }
}