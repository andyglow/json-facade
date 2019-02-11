package json.facade

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}

import org.scalatest._
import org.scalatest.Matchers._

import scala.math.BigDecimal
import scala.util.Success

abstract class JsonFacadeSuite(name: String)(implicit r: ReadF[Model], w: WriteF[Model]) extends WordSpec {
  import JsonFacadeSuite._

  name should {

    "write to string and read from string" in {
      val jsonStr = writeJson.asString(model)
      info(jsonStr)
      val restoredModel = readJson[Model](jsonStr)
      restoredModel shouldBe Success(model)
    }

    "write to bytes and read from bytes" in {
      val jsonBytes = writeJson.asBytes(model)
      info(new String(jsonBytes))
      val restoredModel = readJson[Model](jsonBytes)
      restoredModel shouldBe Success(model)
    }

    "write to output stream and read from input stream" in {
      val baos = new ByteArrayOutputStream
      writeJson.toOutputStream(model, baos)
      val jsonBytes = baos.toByteArray
      val bais = new ByteArrayInputStream(jsonBytes)
      info(new String(jsonBytes))
      val restoredModel = readJson[Model](bais)
      restoredModel shouldBe Success(model)
    }
  }
}
object JsonFacadeSuite {

  val model = Model(
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
}