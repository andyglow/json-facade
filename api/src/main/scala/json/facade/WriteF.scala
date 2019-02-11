package json.facade

import java.io.OutputStream


trait WriteF[-T] {

  def asString(x: T): String

  def asBytes(x: T): Array[Byte]

  def toBytes(x: T, out: Array[Byte], offset: Int): Unit

  def toOutputStream(x: T, os: OutputStream): Unit
}

private[facade] abstract class WriteFBase[-T] extends WriteF[T] {

  override def asBytes(x: T): Array[Byte] = asString(x).getBytes()

  override def toBytes(
    x: T,
    out: Array[Byte],
    offset: Int): Unit = {

    val arr = asBytes(x)
    Array.copy(arr, 0, out, offset, arr.length)
  }

  override def toOutputStream(
    x: T,
    os: OutputStream): Unit = {

    val arr = asBytes(x)
    os.write(arr)
  }
}
