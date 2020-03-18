import RouteGenerator._
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import sttp.tapir.docs.openapi._
import sttp.tapir.openapi.OpenAPI
import sttp.tapir.openapi.circe.yaml._
import sttp.tapir.swagger.akkahttp.SwaggerAkka

import scala.concurrent.duration._
import scala.concurrent.Await

object Main extends App {

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
    implicit val actorSystem: ActorSystem = ActorSystem()
    import actorSystem.dispatcher

    val routes = {
        import akka.http.scaladsl.server.Directives._
        helloWorldRoute ~ byeWorldRoute ~ streamRoute ~ new SwaggerAkka(openApiYml).routes
    }

    val bindAndCheck = Http().bindAndHandle(routes, "localhost", 8080).map { _ =>
        // testing
        println("Go to: http://localhost:8080/docs")
        println("Press any key to exit ...")
        scala.io.StdIn.readLine()
    }

    // cleanup
    Await.result(bindAndCheck.transformWith { r =>
        actorSystem.terminate().transform(_ => r)
    }, 1.minute)

}