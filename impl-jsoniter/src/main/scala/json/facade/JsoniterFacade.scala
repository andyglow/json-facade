package json.facade

import java.io.OutputStream

import com.github.plokhotnyuk.jsoniter_scala.core._
import json.facade.From._

import scala.language.implicitConversions
import scala.util.Try


class JsoniterFacade {
  import JsoniterFacade._

  implicit def lookupReads[T](implicit codec: JsonValueCodec[T]): ReadF[T] = new ReadFacade[T](codec)

  implicit def lookupWrites[T](implicit codec: JsonValueCodec[T]): WriteF[T] = new WriteFacade[T](codec)
}

object JsoniterFacade extends JsoniterFacade {

  class ReadFacade[T](val codec: JsonValueCodec[T]) extends ReadF[T] {
    def read(x: From): Try[T] = Try {
      x match {
        case FromString(str)     => readFromString(str)(codec)
        case FromBytes(bytes)    => readFromArray(bytes)(codec)
        case FromInputStream(is) => readFromStream(is)(codec)
      }
    }
  }

  class WriteFacade[T](val codec: JsonValueCodec[T]) extends WriteF[T] {
    override def asString(x: T): String = writeToString(x)(codec)

    override def asBytes(x: T): Array[Byte] = writeToArray(x)(codec)

    override def toBytes(
      x: T,
      out: Array[Byte],
      offset: Int): Unit = writeToSubArray(x, out, offset, out.length)(codec)

    override def toOutputStream(
      x: T,
      os: OutputStream): Unit = writeToStream(x, os)(codec)
  }
}