package com.headstorm.service

import org.http4s.websocket.WebSocketFrame

class DataService[F[_]]() {

  def compose(wsf: WebSocketFrame): Unit =
    println(wsf.data.toString)

}
