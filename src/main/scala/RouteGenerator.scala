import cats.effect.{ContextShift, IO, _}
import cats.implicits._
import sttp.tapir._
import sttp.tapir.server.http4s._

import scala.concurrent.ExecutionContext

object RouteGenerator {
    class Endpoints(paramName: String, endpointName: String, method: String) {
        // mandatory implicits
        implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global

        implicit val contextShift: ContextShift[IO] = IO.contextShift(ec)
        implicit val timer: Timer[IO] = IO.timer(ec)

        def logic(in: String): IO[Either[Unit, String]] = IO(s"$endpointName : $paramName : $in".asRight[Unit])

        def getRoute = {
            method match {
                case "get" => endpoint.get.in(endpointName).in(query[String](paramName)).out(stringBody).toRoutes((logic _))
                case "post" => endpoint.post.in(endpointName).in(query[String](paramName)).out(stringBody).toRoutes((logic _))
            }
        }
        
        //        val aEndpoint = endpoint.get.in(endpointName).in(query[String](paramName)).out(stringBody)
        //        val aRoutes: HttpRoutes[IO] = aEndpoint.toRoutes((logic _))

//        def getRoute: HttpRoutes[IO] = aRoutes
    }
}
