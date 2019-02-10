package json.facade

import java.io.OutputStream
import java.nio.charset.Charset

import From._
import ujson.{BaseRenderer, BytesRenderer, StringRenderer}
import upickle.core.Visitor

import scala.language.implicitConversions
import scala.util.{Failure, Success, Try}
import upickle.default._

import scala.util.control.NoStackTrace

class UJsonFacade {
  import UJsonFacade._

  implicit def lookupReads[T](implicit r: Reader[T]): ReadF[T] = new ReadFacade[T](r)

  implicit def lookupWrites[T](implicit w: Writer[T]): WriteF[T] = new WriteFacade[T](w)

}

object UJsonFacade extends UJsonFacade {

  class ReadFacade[T](val r: Reader[T]) extends ReadF[T] {
    def read(x: From): Try[T] = Try { upickle.default.read(x.bytes._1)(r) }
  }

  class WriteFacade[T](val w: Writer[T]) extends WriteFBase[T] {

    private def render[O](x: T, r: Visitor[_, O]): O = writeJs(x)(w).transform(r)

    def asString(x: T): String = render(x, StringRenderer(-1, false)).toString

    override def asBytes(x: T, charset: Charset): Array[Byte] = render(x, BytesRenderer()).toBytes
  }
}