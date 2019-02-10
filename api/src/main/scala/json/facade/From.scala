package json.facade

import java.io.{ByteArrayOutputStream, InputStream}
import java.nio.charset.Charset

import scala.io.{BufferedSource, Codec}
import scala.language.implicitConversions


sealed trait From


object From {

  case class FromString(value: String) extends From

  case class FromBytes(value: Array[Byte], charset: Charset) extends From

  case class FromInputStream(value: InputStream, charset: Charset) extends From

  implicit def fromBytes(x: Array[Byte], charset: Charset = Charset.defaultCharset()): From = FromBytes(x, charset)

  implicit def fromInputStream(x: InputStream, charset: Charset = Charset.defaultCharset()): From = FromInputStream(x, charset)

  implicit def fromString(x: String): From = FromString(x)


  implicit class FromAs(val x: From) extends AnyVal {

    def string: String = x match {
      case FromString(v)          => v
      case FromBytes(v, cs)       => new String(v, cs)
      case FromInputStream(v, cs) => scala.io.Source.fromInputStream(v)(Codec(cs)).mkString
    }

    def bytes: (Array[Byte], Charset) = x match {
      case FromString(v)          => (v.getBytes, Charset.defaultCharset)
      case FromBytes(v, cs)       => (v, cs)
      case FromInputStream(v, cs) => (readBytes(v, 1024), cs)
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