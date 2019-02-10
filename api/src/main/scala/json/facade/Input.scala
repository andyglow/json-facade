package json.facade

import java.io.InputStream
import java.nio.charset.Charset

import scala.io.Codec
import scala.language.implicitConversions


sealed trait Input {

  def mkString: String
}

object Input {

  case class StringInput(mkString: String) extends Input

  case class ByteArrayInput(value: Array[Byte], charset: Charset) extends Input {

    def mkString: String = new String(value, charset)
  }

  case class InputStreamInput(value: InputStream, charset: Charset) extends Input {

    def mkString: String = {
      val source = scala.io.Source.fromInputStream(value)(Codec(charset))
      source.mkString
    }
  }

  def apply(x: String): Input = StringInput(x)

  def fromBytes(x: Array[Byte], charset: Charset = Charset.defaultCharset()): Input = ByteArrayInput(x, charset)

  def fromInputStream(x: InputStream, charset: Charset = Charset.defaultCharset()): Input = InputStreamInput(x, charset)

  implicit def fromString(x: String): Input = Input(x)
}