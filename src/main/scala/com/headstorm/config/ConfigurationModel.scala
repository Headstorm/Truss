package com.headstorm.config

object ConfigurationModel {

  final case class AppConfiguration(
                                     server: Server,
                                     database: Database
                                   )

  final case class Server(
                           host: String,
                           port: Int
                         )

  final case class Database(
                             driver: String,
                             url: String,
                             user: String,
                             password: String
                           )

}
