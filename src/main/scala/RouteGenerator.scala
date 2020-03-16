import cats.effect.{ContextShift, IO, _}
import cats.implicits._
import io.circe.generic.auto._
import io.circe.syntax._
import fs2._
import org.http4s.HttpRoutes
import sttp.model.HeaderNames
import sttp.tapir._
import sttp.tapir.json.circe._
import sttp.tapir.server.http4s._

import scala.concurrent.ExecutionContext



object RouteGenerator {

    // mandatory implicits
    implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global
    implicit val contextShift: ContextShift[IO] = IO.contextShift(ec)
    implicit val timer: Timer[IO] = IO.timer(ec)

    case class Messages(code: String, msg: String)
    val msgCodec = jsonBody[Messages]

    class Endpoints(paramName: String, endpointName: String, method: String) {

        def logic(in: String): IO[Either[Unit, Messages]] = {
            IO(Messages("200", s"$endpointName : $paramName : $in").asRight[Unit])
        }

        def getRoute = {
            val initialEndpoint = method match {
                case "get" => endpoint.get
                case "post" => endpoint.post
            }
            initialEndpoint.in(endpointName).in(query[String](paramName)).out(msgCodec).toRoutes((logic _))

        }

    }

    class StreamEndpoints(paramName: String, endpointName: String, method: String) {

        val size = 100L

        def logic(in: String) = {
            val responseMsg = Messages("200", in)
            val responseMsgJson = responseMsg.asJson.toString()
            val listChar = responseMsgJson.toList
            val streamProcess = Stream
              .emit(listChar)
              .flatMap(list => Stream.chunk(Chunk.seq(list)))
              .take(size)
              .covary[IO]
              .map(_.toByte)
              .pure[IO]
              .map(s => Right((size, s)))
            streamProcess
        }

        def getRoute = {
            val initialEndpoint = method match {
                case "get" => endpoint.get
                case "post" => endpoint.post
            }
            val streamingEndpoint = initialEndpoint
              .in(endpointName)
              .in(query[String](paramName))
              .out(header[Long](HeaderNames.ContentLength))
              .out(streamBody[Stream[IO, Byte]](schemaFor[String], CodecFormat.TextPlain()))
            val streamingRoutes: HttpRoutes[IO] = streamingEndpoint.toRoutes (logic _)
            streamingRoutes
        }

    }
}
