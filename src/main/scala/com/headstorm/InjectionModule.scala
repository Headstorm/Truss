package com.headstorm

import cats.effect.{ConcurrentEffect, ContextShift, Sync, Timer}
import com.headstorm.config.AppConfig
import com.headstorm.config.ConfigurationModel.AppConfiguration
import com.headstorm.routes.DataRoutes
import com.headstorm.service.DataService
import org.http4s.HttpRoutes

class InjectionModule[F[_]: ConcurrentEffect](
                                               implicit val sync: Sync[F],
                                               val contextShift: ContextShift[F],
                                               val timer: Timer[F]
                                             ) {

  val configuration: AppConfiguration = new AppConfig[F].loadConfig

  val dataService: DataService[F] = new DataService[F]()

  val routes: HttpRoutes[F] = new DataRoutes[F](dataService).routes

}