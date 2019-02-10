package json.facade

import scala.util.Try

trait ReadF[T] { def read(x: From): Try[T] }
