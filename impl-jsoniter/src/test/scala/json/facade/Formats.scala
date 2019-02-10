package json.facade

import com.github.plokhotnyuk.jsoniter_scala.macros._
import com.github.plokhotnyuk.jsoniter_scala.core._

object Formats  {

  implicit val subF: JsonValueCodec[Sub] = JsonCodecMaker.make[Sub](CodecMakerConfig())

  implicit val modelF: JsonValueCodec[Model] = JsonCodecMaker.make[Model](CodecMakerConfig())
}
