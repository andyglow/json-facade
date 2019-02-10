package json.facade

import Input._

import scala.language.implicitConversions
import scala.util.{Failure, Success, Try}
import com.github.plokhotnyuk.jsoniter_scala.core._

import scala.util.control.NoStackTrace


class JsoniterFacade extends Implementation with Direct {
  import JsoniterFacade._

  override type Value = String

  override def parse(x: Input): Try[Value] = Success(x.mkString)

  override def stringify(x: Value): String = x

  override def directParse[T](x: Input)(implicit r: ReadF[T]): Try[T] = for {
    codec <- codecOf[T](r)
    t     <- Try {
                x match {
                  case StringInput(str)           => readFromString(str)(codec)
                  case ByteArrayInput(bytes, cs)  => readFromArray(bytes)(codec)
                  case InputStreamInput(is, cs)   => readFromStream(is)(codec)
                }
             }
  } yield t

  override def directStringify[T](t: T)(implicit w: WriteF[T]): String = {
    val codec = codecOf[T](w).get
    writeToString(t)(codec)
  }

  implicit def lookupReads[T](implicit codec: JsonValueCodec[T]): ReadF[T] = new FormatFacade[T](codec)

  implicit def lookupWrites[T](implicit codec: JsonValueCodec[T]): WriteF[T] = new FormatFacade[T](codec)

  implicit def lookupFormat[T](implicit codec: JsonValueCodec[T]): FormatF[T] = new FormatFacade[T](codec)
}

object JsoniterFacade extends JsoniterFacade {

  val notSupported = new Exception("only direct transformation supported by this lib") with NoStackTrace

  class FormatFacade[T](val rw: JsonValueCodec[T]) extends FormatF[T] {
    def read(x: json.facade.Value): Try[T] = Failure(notSupported)
    def write(x: T): json.facade.Value = throw notSupported
  }

  private def codecOf[T](implicit x: ReadF[T]): Try[JsonValueCodec[T]] = x match {
    case x: FormatFacade[T] => Success(x.rw)
    case _                  => Failure(new Exception(s"WriteF of type ${x.getClass} is unknown from JsoniterFacade perspective") with NoStackTrace)
  }

  private def codecOf[T](implicit x: WriteF[T]): Try[JsonValueCodec[T]] = x match {
    case x: FormatFacade[T] => Success(x.rw)
    case _                  => Failure(new Exception(s"WriteF of type ${x.getClass} is unknown from JsoniterFacade perspective") with NoStackTrace)
  }
}