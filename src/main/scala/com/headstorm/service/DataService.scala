package com.headstorm.service

import org.http4s.websocket.WebSocketFrame

class DataService[F[_]]() {

  def compose(wsf: WebSocketFrame): Unit =
    println(wsf.data.toString)

  def counties(): List[String] = List("QA","DE","GB","MJ","PT","DG","BD","AS","PE")
}
