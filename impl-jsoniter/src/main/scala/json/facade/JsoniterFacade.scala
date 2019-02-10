package json.facade

import java.io.OutputStream
import java.nio.charset.Charset

import From._

import scala.language.implicitConversions
import scala.util.{Failure, Success, Try}
import com.github.plokhotnyuk.jsoniter_scala.core._

import scala.util.control.NoStackTrace


class JsoniterFacade {
  import JsoniterFacade._

  implicit def lookupReads[T](implicit codec: JsonValueCodec[T]): ReadF[T] = new ReadFacade[T](codec)

  implicit def lookupWrites[T](implicit codec: JsonValueCodec[T]): WriteF[T] = new WriteFacade[T](codec)
}

object JsoniterFacade extends JsoniterFacade {

  class ReadFacade[T](val codec: JsonValueCodec[T]) extends ReadF[T] {
    def read(x: From): Try[T] = Try {
      x match {
        case FromString(str)         => readFromString(str)(codec)
        case FromBytes(bytes, cs)    => readFromArray(bytes)(codec)
        case FromInputStream(is, cs) => readFromStream(is)(codec)
      }
    }
  }

  class WriteFacade[T](val codec: JsonValueCodec[T]) extends WriteF[T] {
    override def asString(x: T): String = writeToString(x)(codec)

    override def asBytes(x: T, charset: Charset): Array[Byte] = writeToArray(x)(codec)

    override def toBytes(
      x: T,
      out: Array[Byte],
      offset: Int,
      charset: Charset): Unit = writeToSubArray(x, out, offset, out.length)(codec)

    override def toOutputStream(
      x: T,
      os: OutputStream,
      charset: Charset): Unit = writeToStream(x, os)(codec)
  }
}