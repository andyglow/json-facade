package json.facade

import play.api.libs.json._


object Formats {

  implicit val tagNamedF: Format[Tag.Named] = Json.format
  implicit val tagGenericR: Reads[Tag.Generic.type] = Reads pure Tag.Generic
  implicit val tagGenericW: Writes[Tag.Generic.type] = Writes[Tag.Generic.type](_ => JsArray())
  implicit val tagR: Reads[Tag] =
    __.read[Tag.Named].map(x => x: Tag) orElse __.read[Tag.Generic.type].map(_ => Tag.Generic)

  implicit val tagW: Writes[Tag] = Writes[Tag] {
    case named: Tag.Named => tagNamedF.writes(named)
    case Tag.Generic => tagGenericW.writes(Tag.Generic)
  }

  implicit val subF: Format[Sub] = Json.format

  implicit val modelF: Format[Model] = Json.format
}
