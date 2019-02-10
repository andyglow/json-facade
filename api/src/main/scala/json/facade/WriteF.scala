package json.facade

import java.io.OutputStream
import java.nio.charset.Charset

trait WriteF[-T] {

  def asString(x: T): String

  def asBytes(x: T, charset: Charset = Charset.defaultCharset()): Array[Byte]

  def toBytes(x: T, out: Array[Byte], offset: Int, charset: Charset = Charset.defaultCharset()): Unit

  def toOutputStream(x: T, os: OutputStream, charset: Charset= Charset.defaultCharset()): Unit
}

private[facade] abstract class WriteFBase[-T] extends WriteF[T] {

  override def asBytes(x: T, charset: Charset = Charset.defaultCharset()): Array[Byte] = asString(x) getBytes charset

  override def toBytes(
    x: T,
    out: Array[Byte],
    offset: Int,
    charset: Charset = Charset.defaultCharset()): Unit = {

    val arr = asBytes(x, charset)
    Array.copy(arr, 0, out, offset, arr.length)
  }

  override def toOutputStream(
    x: T,
    os: OutputStream,
    charset: Charset = Charset.defaultCharset()): Unit = {

    val arr = asBytes(x, charset)
    os.write(arr)
  }
}
