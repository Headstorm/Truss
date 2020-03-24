import com.headstorm.RouteGenerator._
import org.scalatest.FunSuite
import cats.effect._
import cats.implicits._
import com.headstorm.Main.{openApiYml, routes}
import org.http4s.HttpRoutes
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.syntax.kleisli._
import sttp.tapir.docs.openapi._
import sttp.tapir.openapi.OpenAPI
import sttp.tapir.openapi.circe.yaml._
import sttp.tapir.swagger.http4s.SwaggerHttp4s

class MainTest extends FunSuite {

    // the endpoints' routes
    val helloWorldInit = new Endpoints("name", "hello", "get")
    val helloWorldEndpoint = helloWorldInit.aEndpoint
    val helloWorldRoute = helloWorldInit.aRoute

    val byeWorldInit = new Endpoints("name", "bye", "post")
    val byeWorldEndpoint = byeWorldInit.aEndpoint
    val byeWorldRoute = byeWorldInit.aRoute

    val streamInit = new StreamEndpoints("text", "stream", "get")
    val streamEndpoint = streamInit.streamingEndpoint
    val streamRoute = streamInit.streamingRoute

    // generating the documentation in yml; extension methods come from imported packages
    val openApiDocs: OpenAPI = List(helloWorldEndpoint, byeWorldEndpoint, streamEndpoint).toOpenAPI("The tapir library", "1.0.0")
    val openApiYml: String = openApiDocs.toYaml

    // starting the server
    val routes: HttpRoutes[IO] = byeWorldRoute <+> helloWorldRoute <+> streamRoute

    // starting the server
    BlazeServerBuilder[IO]
        .bindHttp(8080, "localhost")
        .withHttpApp(Router("/" -> (routes <+> new SwaggerHttp4s(openApiYml).routes[IO])).orNotFound)
        .resource
        .use { _ =>
            IO {
                println("Go to: http://localhost:8080/docs")
                println("Press any key to exit ...")
                scala.io.StdIn.readLine()
            }
        }
        .unsafeRunSync()

//    val bindAndCheck = Http().bindAndHandle(routes, "localhost", 8080).map { _ =>
//        implicit val backend: SttpBackend[Identity, Nothing, NothingT] = HttpURLConnectionBackend()
//
//        //test hello GET endpoint
//        test("hello GET Endpoint"){
//            val result: String = basicRequest.response(asStringAlways).get(uri"http://localhost:8080/hello?name=Frodo").send().body
//            //        println("Got result: " + result)
//            assert(result == "{\"code\":\"200\",\"msg\":\"hello : name : Frodo\"}")
//        }
//
//        test("bye POST Endpoint") {
//            //test bye POST endpoint
//            val result2: String = basicRequest.response(asStringAlways).post(uri"http://localhost:8080/bye?name=Frodo").send().body
//            //        println("Got result: " + result2)
//            assert(result2 == "{\"code\":\"200\",\"msg\":\"bye : name : Frodo\"}")
//        }
//
//        test("test streaming GET endpoint") {
//            //test streaming GET endpoint
//            val result3: String = basicRequest.response(asStringAlways).get(uri"http://localhost:8080/stream?text=Frodo").send().body
//            //        println("Got result: " + result3)
//            assert(result3 == "{\n  \"code\" : \"200\",\n  \"msg\" : \"stream : text : Frodo\"\n}")
//        }
//
//        test("test Not Found") {
//            //test Not Found
//            val result4: String = basicRequest.response(asStringAlways).get(uri"http://localhost:8080/stream2?text=Frodo").send().statusText
//            //        println("Got result: " + result4)
//            assert(result4 == "Not Found")
//        }
//
//        //running server
//        println("Go to: http://localhost:8080/docs")
//        println("Press any key to exit ...")
//        scala.io.StdIn.readLine()
//    }

}
