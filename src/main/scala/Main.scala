import RouteGenerator._
import cats.effect.{IO, _}
import cats.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.syntax.kleisli._

object Main extends IOApp {

    // the endpoints' routes
    val helloWorldRoutes = new Endpoints("name", "hello", "get").getRoute
    val byeWorldRoutes = new Endpoints("name", "bye", "post").getRoute
    val streamRoutes = new StreamEndpoints("text", "stream").getRoute

    val route = Router(
        ("/" -> helloWorldRoutes),
        ("/" -> byeWorldRoutes),
        ("/" -> streamRoutes)).orNotFound

    // starting the server
    override def run(args: List[String]): IO[ExitCode] =
        BlazeServerBuilder[IO]
          .bindHttp(8080)
          .withHttpApp(route)
          .serve
          .compile.drain.as(ExitCode.Success)

}