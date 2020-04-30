package com.headstorm

import cats.effect.{ ExitCode, IO, IOApp }
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.implicits._

import scala.concurrent.duration._

object HTTPServer extends IOApp {


    private val module = new InjectionModule[cats.effect.IO]

    def run(args: List[String]): cats.effect.IO[ExitCode] =
        BlazeServerBuilder[IO]
          .bindHttp(module.configuration.server.port, module.configuration.server.host)
          .withWebSockets(true)
          .withNio2(true)
          .withIdleTimeout(300.seconds)
          .withSocketKeepAlive(true)
          .withHttpApp(module.routes.orNotFound)
          .serve
          .compile
          .lastOrError

}