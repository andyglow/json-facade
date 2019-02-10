package json.facade

import akka.http.scaladsl.marshalling.{Marshaller, ToEntityMarshaller}
import akka.http.scaladsl.model.ContentTypeRange
import akka.http.scaladsl.model.MediaType
import akka.http.scaladsl.model.MediaTypes.`application/json`
import akka.http.scaladsl.server.{RejectionError, ValidationRejection}
import akka.http.scaladsl.unmarshalling.{FromByteStringUnmarshaller, FromEntityUnmarshaller, Unmarshaller}
import akka.http.scaladsl.util.FastFuture
import akka.util.ByteString

import scala.collection.immutable.Seq

import scala.util.Failure


trait AkkaHttpAdapter {

  def unmarshallerContentTypes: Seq[ContentTypeRange] = mediaTypes.map(ContentTypeRange.apply)

  def mediaTypes: Seq[MediaType.WithFixedCharset] = List(`application/json`)

  private val jsonStringUnmarshaller =
    Unmarshaller.byteStringUnmarshaller
      .forContentTypes(unmarshallerContentTypes: _*)
      .mapWithCharset {
        case (ByteString.empty, _) => throw Unmarshaller.NoContentException
        case (data, charset)       => data.decodeString(charset.nioCharset.name)
      }

  private val jsonStringMarshaller =
    Marshaller.oneOf(mediaTypes: _*)(Marshaller.stringMarshaller)

  private def tryReadJson[A: ReadF](x: String) = readJson(x) recoverWith {
    case err: JsonException => Failure(RejectionError(ValidationRejection(err.getMessage, Some(err))))
    case err: Throwable     => Failure(err)
  }

  private def tryReadJsonF[A: ReadF](bs: ByteString) =
    FastFuture { tryReadJson(bs.utf8String) }

  implicit def fromByteStringUnmarshaller[A: ReadF]: FromByteStringUnmarshaller[A] =
    Unmarshaller.withMaterializer[ByteString, A](_ => _ => tryReadJsonF(_))

  implicit def unmarshaller[A: ReadF]: FromEntityUnmarshaller[A] =
    jsonStringUnmarshaller map { tryReadJson(_).get }

  implicit def marshaller[A](implicit writes: WriteF[A]): ToEntityMarshaller[A] =
    jsonStringMarshaller compose { writeJson.asString(_) }
}

object AkkaHttpAdapter extends AkkaHttpAdapter