package json.facade

import upickle.default._


object Formats  {

  implicit val tagNamedF: ReadWriter[Tag.Named] = macroRW
  implicit val tagGenericF: ReadWriter[Tag.Generic.type] = macroRW
  implicit val tagF: ReadWriter[Tag] = ReadWriter.merge(tagNamedF, tagGenericF)

  implicit val subF: ReadWriter[Sub] = macroRW

  implicit val modelF: ReadWriter[Model] = macroRW
}
