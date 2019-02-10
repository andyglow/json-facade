package json.facade

import scala.util.Try

trait ReadF[T] { def read(x: Value): Try[T] }
