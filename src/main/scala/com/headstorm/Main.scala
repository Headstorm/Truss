package com.headstorm

import RouteGenerator._
import cats.effect._
import cats.implicits._
import com.typesafe.config.ConfigFactory
import javax.sound.sampled.Port
import org.http4s.HttpRoutes
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.syntax.kleisli._
import sttp.tapir.docs.openapi._
import sttp.tapir.openapi.OpenAPI
import sttp.tapir.openapi.circe.yaml._
import sttp.tapir.swagger.http4s.SwaggerHttp4s
import pureconfig.generic.auto._
import pureconfig._
import sttp.tapir.server.http4s.Http4sServerOptions


case class DBDetail(dataSource: String, driver: String, url: String, user: String, password: String)
case class ServerDetail(host: String, port: Int)

case class Config(database: DBDetail, server: ServerDetail)

case class DevConfig(development: Config)

object Main extends App {

    val config = ConfigSource.default.load[DevConfig]

    private val configuration = {
        ConfigSource.default.load[DevConfig] match {
            case Left(error) =>
                println(s"There was an error loading the config, shutting down: ${error.toString}")
                System.exit(1)
            case Right(config) => config
        }
    }

    val (serverHost: String,
        serverPort: Int,
        databaseDataSource: String,
        databaseDriver: String,
        databaseURL: String,
        databaseUser: String,
        databasePassword: String) = configuration match {
        case DevConfig(r) => (r.server.host.toString,
                            r.server.port,
                            r.database.dataSource,
                            r.database.driver,
                            r.database.url,
                            r.database.user,
                            r.database.password)
        case _ => println("Error Loading Config")
    }

    // the endpoints' routes
    val helloWorldInit = new Endpoints("name", "hello", "get")
    val helloWorldEndpoint = helloWorldInit.aEndpoint
    val helloWorldRoute = helloWorldInit.aRoute

    val byeWorldInit = new Endpoints("name", "bye", "post")
    val byeWorldEndpoint = byeWorldInit.aEndpoint
    val byeWorldRoute = byeWorldInit.aRoute

    val databaseSelectInit = new DatabaseEndpoints(databaseDataSource, databaseDriver, databaseURL, databaseUser, databasePassword, "name", "dbselect", "select")
    val databaseSelectEndpoint = databaseSelectInit.aEndpoint
    def databaseSelectRoute[F[_]: Sync](implicit serverOption: Http4sServerOptions[F], fcs: ContextShift[F]) = databaseSelectInit.aRoute

    val databaseInsertInit = new DatabaseEndpoints(databaseDataSource, databaseDriver, databaseURL, databaseUser, databasePassword, "name", "dbinsert", "insert")
    val databaseInsertEndpoint = databaseInsertInit.aEndpoint
    def databaseInsertRoute[F[_]: Sync](implicit serverOption: Http4sServerOptions[F], fcs: ContextShift[F]) = databaseInsertInit.aRoute

    val databaseUpdateInit = new DatabaseEndpoints(databaseDataSource, databaseDriver, databaseURL, databaseUser, databasePassword, "name", "dbupdate", "update")
    val databaseUpdateEndpoint = databaseUpdateInit.aEndpoint
    def databaseUpdateRoute[F[_]: Sync](implicit serverOption: Http4sServerOptions[F], fcs: ContextShift[F]) = databaseUpdateInit.aRoute

    val streamInit = new StreamEndpoints("text", "stream", "get")
    val streamEndpoint = streamInit.streamingEndpoint
    val streamRoute = streamInit.streamingRoute

    // generating the documentation in yml; extension methods come from imported packages
    val routes: HttpRoutes[IO] = helloWorldRoute <+> byeWorldRoute <+> streamRoute <+> databaseSelectRoute <+> databaseInsertRoute <+> databaseUpdateRoute

    // generating the documentation in yml; extension methods come from imported packages
    val openApiDocs: OpenAPI = List(helloWorldEndpoint, byeWorldEndpoint, streamEndpoint, databaseSelectEndpoint, databaseInsertEndpoint, databaseUpdateEndpoint).toOpenAPI("The tapir library", "1.0.0")
    val openApiYml: String = openApiDocs.toYaml

    // starting the server
    BlazeServerBuilder[IO]
        .bindHttp(serverPort, serverHost)
        .withHttpApp(Router("/" -> (routes <+> new SwaggerHttp4s(openApiYml).routes[IO])).orNotFound)
        .resource
        .use { _ =>
            IO {
                println(s"Go to: http://$serverHost:$serverPort/docs")
                println("Press any key to exit ...")
                scala.io.StdIn.readLine()
            }
        }
        .unsafeRunSync()


}