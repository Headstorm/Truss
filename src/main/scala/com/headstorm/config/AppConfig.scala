package com.headstorm.config

import cats.effect.{ContextShift, Sync, Timer}
import com.headstorm.config.ConfigurationModel.AppConfiguration
import com.headstorm.logging.Logging
import pureconfig.ConfigSource
import pureconfig.generic.auto._
import pureconfig._

class AppConfig[F[_]](
                               implicit val sync: Sync[F],
                               val contextShift: ContextShift[F],
                               val timer: Timer[F]
                             ) extends Logging[F] {

  def loadConfig: AppConfiguration =
    ConfigSource.default.at(sys.env.getOrElse("TRUSS_ENV", "development")).load[AppConfiguration] match {

      case Right(configuration) =>
        configuration

      case Left(error) =>
        logError(error.prettyPrint())
        sys.exit(1)
    }

}
