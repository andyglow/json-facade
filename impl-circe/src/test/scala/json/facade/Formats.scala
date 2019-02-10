package json.facade

import io.circe._, io.circe.generic.semiauto._


object Formats {

  implicit val tagR: Decoder[Tag] = deriveDecoder
  implicit val tagW: Encoder[Tag] = deriveEncoder

  implicit val subR: Decoder[Sub] = deriveDecoder
  implicit val subW: Encoder[Sub] = deriveEncoder

  implicit val modelR: Decoder[Model] = deriveDecoder
  implicit val modelW: Encoder[Model] = deriveEncoder
}
