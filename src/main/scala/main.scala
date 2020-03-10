
import sttp.tapir.openapi._
import scala.concurrent.ExecutionContext
import sttp.tapir.docs.openapi._
import cats.effect._
import org.http4s.HttpRoutes
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.syntax.kleisli._
import sttp.tapir._
import sttp.tapir.server.http4s._
import cats.implicits._

object main extends IOApp {
    // the endpoint: single fixed path input ("hello"), single query parameter
    //    // corresponds to: GET /hello?name=...
    val helloWorld: Endpoint[String, Unit, String, Nothing] = endpoint.get.in("hello").in(query[String]("name")).out(stringBody)

    // mandatory implicits
    implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global

    override implicit val contextShift: ContextShift[IO] = IO.contextShift(ec)
    override implicit val timer: Timer[IO] = IO.timer(ec)

    // converting an endpoint to a route (providing server-side logic); extension method comes from imported packages
    val helloWorldRoutes: HttpRoutes[IO] = helloWorld.toRoutes(name => IO(s"Hello, $name!".asRight[Unit]))

    // starting the server
    val openApi: OpenAPI = helloWorld.toOpenAPI("The tapir library", "1.0")

    override def run(args: List[String]): IO[ExitCode] =
        BlazeServerBuilder[IO]
          .bindHttp(8080)
          .withHttpApp(Router("/" -> helloWorldRoutes).orNotFound)
          .serve
          .compile.drain.as(ExitCode.Success)

}