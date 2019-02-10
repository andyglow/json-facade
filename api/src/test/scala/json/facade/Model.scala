package json.facade

sealed trait Tag
object Tag {
  case class Named(value: String) extends Tag
  case object Generic extends Tag
}

case class Sub(ts: Long, app: Option[String], amount: Double)

case class Model(
  name: String,
  sub: Sub,
  params: Map[String, String],
  money: BigDecimal,
  tags: List[Tag])
