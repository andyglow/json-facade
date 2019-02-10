package json

import java.io.OutputStream
import java.nio.charset.Charset

import scala.language.implicitConversions
import scala.util.Try


package object facade {

  def readJson[T](x: From)(implicit r: ReadF[T]): Try[T] = r read x

  object writeJson {

    def asString[T](x: T)(implicit w: WriteF[T]): String = w asString x

    def asBytes[T](x: T, ch: Charset = Charset.defaultCharset)(implicit w: WriteF[T]): Array[Byte] = w.asBytes(x, ch)

    def toBytes[T](x: T, out: Array[Byte], offset: Int, ch: Charset = Charset.defaultCharset)(implicit w: WriteF[T]): Unit = w.toBytes(x, out, offset, ch)

    def toOutputStream[T](x: T, os: OutputStream, ch: Charset= Charset.defaultCharset)(implicit w: WriteF[T]): Unit = w.toOutputStream(x, os, ch)
  }
}
