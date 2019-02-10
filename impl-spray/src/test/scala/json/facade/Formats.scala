package json.facade

import spray.json._


object Formats extends DefaultJsonProtocol {

  implicit val tagF: JsonFormat[Tag] = new JsonFormat[Tag] {

    override def write(obj: Tag): JsValue = obj match {
      case Tag.Generic      => JsArray()
      case Tag.Named(name)  => JsString(name)
    }

    override def read(json: JsValue): Tag = json match {
      case _: JsArray    => Tag.Generic
      case JsString(str) => Tag.Named(str)
      case _             => throw DeserializationException(s"unknown $json")
    }
  }

  implicit val subF: JsonFormat[Sub] = jsonFormat3(Sub)

  implicit val modelF: JsonFormat[Model] = jsonFormat5(Model)
}
