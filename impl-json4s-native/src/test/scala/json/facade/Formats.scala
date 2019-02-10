package json.facade

import org.json4s.JsonAST._
import org.json4s.{CustomSerializer, DefaultFormats, Formats => Json4sFormats}


object Formats {

  class TagSerializer extends CustomSerializer[Tag](_ => (TagSerializer.reads, TagSerializer.writes))

  object TagSerializer {

    val reads: PartialFunction[JValue, Tag] = {
      case _: JArray    => Tag.Generic
      case JString(str) => Tag.Named(str)
    }

    val writes: PartialFunction[Any, JValue] = {
      case Tag.Generic    => JArray(Nil)
      case Tag.Named(str) => JString(str)
    }
  }

  implicit val formats: Json4sFormats = DefaultFormats + new TagSerializer
}
