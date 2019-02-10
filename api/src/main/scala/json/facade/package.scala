package json

import java.io.InputStream
import java.nio.charset.Charset
import java.util.ServiceLoader

import scala.collection.JavaConverters._
import scala.io.Codec
import scala.util.Try
import scala.language.implicitConversions


package object facade {

  lazy val impl: Implementation = {
    val loader = ServiceLoader.load(classOf[Implementation])
    val impls = loader.iterator().asScala.toSeq
    if (impls.isEmpty) sys.error("can't load json language implementation")
    else if (impls.size > 1) {
      sys.props.get("json.impl.preferred") match {
        case Some(className)  => impls.find(_.getClass.getName == className) getOrElse impls.head
        case None             => impls.head
      }
    } else
      impls.head
  }

  type Value = impl.Value

  def parseJs(x: Input): Try[impl.Value] = impl.parse(x)

  def stringifyJs(x: impl.Value): String = impl.stringify(x)

  def read[T](x: Input)(implicit r: ReadF[T]): Try[T] = impl match {
    case impl: Direct => impl.directParse(x)
    case _            => for { v <- parseJs(x); j <- r.read(v) } yield j
  }

  def write[T](x: T)(implicit w: WriteF[T]): String = impl match {
    case impl: Direct => impl.directStringify(x)
    case _            => stringifyJs(w.write(x))
  }
}
