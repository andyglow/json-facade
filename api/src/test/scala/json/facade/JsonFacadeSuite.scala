package json.facade

import org.scalatest._
import org.scalatest.Matchers._

import scala.math.BigDecimal
import scala.util.Success

abstract class JsonFacadeSuite(name: String)(implicit fmt: FormatF[Model]) extends WordSpec {

  name should {

    "write-read" in {

      val model =
        Model(
          "FOO",
          Sub(
            98234,
            Some("app"),
            Math.E),
          Map(
            "p1" -> "p1",
            "p2" -> "p2"),
        BigDecimal(Math.PI),
        List(Tag.Named("foo"), Tag.Generic, Tag.Named("bar")))

      val jsonStr = write(model)
      info(jsonStr)
      val restoredModel = read[Model](jsonStr)
      restoredModel shouldBe Success(model)
    }
  }
}
