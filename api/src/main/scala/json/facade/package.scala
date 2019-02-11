package json

import java.io.OutputStream

import scala.language.implicitConversions
import scala.util.Try


package object facade {

  def readJson[T](x: From)(implicit r: ReadF[T]): Try[T] = r read x

  object writeJson {

    def asString[T](x: T)(implicit w: WriteF[T]): String = w asString x

    def asBytes[T](x: T)(implicit w: WriteF[T]): Array[Byte] = w.asBytes(x)

    def toBytes[T](x: T, out: Array[Byte], offset: Int)(implicit w: WriteF[T]): Unit = w.toBytes(x, out, offset)

    def toOutputStream[T](x: T, os: OutputStream)(implicit w: WriteF[T]): Unit = w.toOutputStream(x, os)
  }
}
