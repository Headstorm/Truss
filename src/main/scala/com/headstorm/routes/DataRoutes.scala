package com.headstorm.routes

import cats.effect.{Concurrent, ContextShift, Sync, Timer}
import cats.implicits._
import com.headstorm.service.DataService
import fs2.concurrent.Queue
import io.circe.syntax._
import cats.effect._
import io.circe.literal._
import org.http4s._
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.http4s.server.websocket.WebSocketBuilder
import org.http4s.websocket.WebSocketFrame

class ActivityRoutes[F[_]]

class DataRoutes[F[_]](service: DataService[F])(
  implicit val sync: Sync[F],
  val concurrent: Concurrent[F],
  val contextShift: ContextShift[F],
  val timer: Timer[F]
) extends Http4sDsl[F] {

  def routes: HttpRoutes[F] = HttpRoutes.of[F] {

    case GET -> Root / "health" => Ok()

    case GET -> Root / "socket" =>
      for {
        messagePipe <- Queue.unbounded[F, WebSocketFrame]
        socket <- WebSocketBuilder[F].build(
          send = messagePipe.dequeue,
          receive = _.map(service.compose)
        )
      } yield socket

    case GET -> Root / "counties" => Ok(
      service.counties.asJson.spaces4
    )

    case GET -> Root / "counties" / "weather" => Ok(
      service.countyWeatherDemo
    )

  }
}
