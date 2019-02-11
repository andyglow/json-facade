package json.facade

import java.io.{ByteArrayOutputStream, InputStream}
import java.nio.charset.Charset

import scala.io.{BufferedSource, Codec}
import scala.language.implicitConversions


sealed trait From


object From {

  case class FromString(value: String) extends From

  case class FromBytes(value: Array[Byte]) extends From

  case class FromInputStream(value: InputStream) extends From

  implicit def fromBytes(x: Array[Byte]): From = FromBytes(x)

  implicit def fromInputStream(x: InputStream): From = FromInputStream(x)

  implicit def fromString(x: String): From = FromString(x)

  implicit class FromAs(val x: From) extends AnyVal {

    def string: String = x match {
      case FromString(v)      => v
      case FromBytes(v)       => new String(v)
      case FromInputStream(v) => scala.io.Source.fromInputStream(v)(Codec(Charset.defaultCharset)).mkString
    }

    def bytes: Array[Byte] = x match {
      case FromString(v)      => v.getBytes
      case FromBytes(v)       => v
      case FromInputStream(v) => readBytes(v, 1024)
    }
  }

  private val EOF: Int = -1

  private def readBytes(is: InputStream, bufferSize: Int): Array[Byte] = {
    val buf = Array.ofDim[Byte](bufferSize)
    val out = new ByteArrayOutputStream(bufferSize)

    Stream.continually(is.read(buf)) takeWhile { _ != EOF } foreach { n =>
      out.write(buf, 0, n)
    }

    out.toByteArray
  }
}