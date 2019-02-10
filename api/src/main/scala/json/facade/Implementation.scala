package json.facade

import scala.util.Try


trait Implementation {

  type Value

  def parse(x: Input): Try[Value]

  def stringify(x: Value): String
}

trait Direct { this: Implementation =>

  def directParse[T: ReadF](x: Input): Try[T]

  def directStringify[T: WriteF](x: T): String
}