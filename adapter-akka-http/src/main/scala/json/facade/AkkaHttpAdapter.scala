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
import json.facade._

import scala.util.Failure


trait AkkaHttpAdapter {

  def unmarshallerContentTypes: Seq[ContentTypeRange] =
    mediaTypes.map(ContentTypeRange.apply)

  def mediaTypes: Seq[MediaType.WithFixedCharset] =
    List(`application/json`)

  private val jsonStringUnmarshaller =
    Unmarshaller.byteStringUnmarshaller
      .forContentTypes(unmarshallerContentTypes: _*)
      .mapWithCharset {
        case (ByteString.empty, _) => throw Unmarshaller.NoContentException
        case (data, charset)       => data.decodeString(charset.nioCharset.name)
      }

  private val jsonStringMarshaller =
    Marshaller.oneOf(mediaTypes: _*)(Marshaller.stringMarshaller)

  implicit def fromByteStringUnmarshaller[A: ReadF]: FromByteStringUnmarshaller[A] = {
    def parse(bs: ByteString) = FastFuture apply read(bs.utf8String)
    Unmarshaller.withMaterializer[ByteString, A](_ => _ => parse)
  }

  implicit def unmarshaller[A](implicit r: ReadF[A]): FromEntityUnmarshaller[A] = {
    jsonStringUnmarshaller map { data =>
      read(data).recoverWith {
        case err: Throwable => Failure(RejectionError(ValidationRejection(err.getMessage)))
      }.get
    }
  }

  implicit def marshaller[A](implicit writes: WriteF[A]): ToEntityMarshaller[A] =
    jsonStringMarshaller.compose(x => write(x))
}

object AkkaHttpAdapter extends AkkaHttpAdapter