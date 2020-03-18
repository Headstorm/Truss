import sttp.tapir.Endpoint

import scala.concurrent.Future
import sttp.tapir.json.circe._
import akka.http.scaladsl.server.Route
import akka.stream.scaladsl.Source
import akka.util.ByteString
import sttp.tapir._
import sttp.tapir.server.akkahttp._
import sttp.model.StatusCode
import io.circe.generic.auto._, io.circe.syntax._

object RouteGenerator {

    sealed trait ErrorInfo
    case class NotFound(what: String) extends ErrorInfo
    case class Unauthorized(realm: String) extends ErrorInfo
    case class Unknown(code: Int, msg: String) extends ErrorInfo
    case object NoContent extends ErrorInfo
    //
    case class Messages(code: String, msg: String)
    val msgCodec = jsonBody[Messages]

    class Endpoints(paramName: String, endpointName: String, method: String) {

        val baseEndpoint = endpoint.errorOut(
            oneOf(
                statusMapping(StatusCode.NotFound, jsonBody[NotFound].description("not found")),
                statusMapping(StatusCode.Unauthorized, jsonBody[Unauthorized].description("unauthorized")),
                statusMapping(StatusCode.NoContent, emptyOutput.map(_ => NoContent)(_ => ())),
                statusDefaultMapping(jsonBody[Unknown].description("unknown"))
            )
        )

        val aEndpoint: Endpoint[String, ErrorInfo with Product with Serializable, Messages, Nothing] = {
            method match {
                case "get" => baseEndpoint.get.in(endpointName).in(query[String](paramName)).out(jsonBody[Messages])
                case "post" => baseEndpoint.post.in(endpointName).in(query[String](paramName)).out(jsonBody[Messages])
            }

        }

        val aRoute = aEndpoint.toRoute(in => Future.successful(Right(Messages("200", s"$endpointName : $paramName : $in"))))
    }

    class StreamEndpoints(paramName: String, endpointName: String, method: String) {

        val baseEndpoint = endpoint.errorOut(
            oneOf(
                statusMapping(StatusCode.NotFound, jsonBody[NotFound].description("not found")),
                statusMapping(StatusCode.Unauthorized, jsonBody[Unauthorized].description("unauthorized")),
                statusMapping(StatusCode.NoContent, emptyOutput.map(_ => NoContent)(_ => ())),
                statusDefaultMapping(jsonBody[Unknown].description("unknown"))
            )
        )

        val streamingEndpoint: Endpoint[String, ErrorInfo with Product with Serializable, Source[ByteString, Any], Source[ByteString, Any]] = {
            method match {
                case "get" => baseEndpoint.get.in(endpointName).in(query[String](paramName)).out(streamBody[Source[ByteString, Any]](schemaFor[Messages], CodecFormat.TextPlain()))
                case "post" => baseEndpoint.post.in(endpointName).in(query[String](paramName)).out(streamBody[Source[ByteString, Any]](schemaFor[Messages], CodecFormat.TextPlain()))
            }
        }

        def createStream(in: String) = {
            val stringMsg = Messages("200", s"$endpointName : $paramName : $in").asJson.toString()
            val streamMsg = Source.repeat(stringMsg).take(1).map(s => ByteString(s))
            Future.successful(Right(streamMsg))
        }
        val streamingRoute: Route = streamingEndpoint.toRoute(createStream _)

    }
}
