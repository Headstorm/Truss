package com.headstorm

import sttp.tapir.Endpoint
import sttp.model.StatusCode
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import sttp.tapir.json.circe._
import cats.effect._
import cats.implicits._
import org.http4s.HttpRoutes
import sttp.tapir._
import sttp.tapir.server.http4s._
import fs2._
import sttp.model.HeaderNames
import doobie._
import doobie.implicits._
import cats.effect.IO
import scala.concurrent.ExecutionContext

object RouteGenerator {

    sealed trait ErrorInfo
    case class NotFound(what: String) extends ErrorInfo
    case class Unauthorized(realm: String) extends ErrorInfo
    case class Unknown(code: Int, msg: String) extends ErrorInfo
    case object NoContent extends ErrorInfo
    //
    case class Messages(code: String, msg: String)
    val msgCodec = jsonBody[Messages]

    implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global
    implicit val contextShift: ContextShift[IO] = IO.contextShift(ec)
    implicit val timer: Timer[IO] = IO.timer(ec)

    class Endpoints(paramName: String, endpointName: String, method: String) {

        val baseEndpoint = endpoint.errorOut(
            oneOf(
                statusMapping(StatusCode.NotFound, jsonBody[NotFound].description("not found")),
                statusMapping(StatusCode.Unauthorized, jsonBody[Unauthorized].description("unauthorized")),
                statusMapping(StatusCode.NoContent, emptyOutput.map(_ => NoContent)(_ => ())),
                statusDefaultMapping(jsonBody[Unknown].description("unknown"))
            )
        )

        val aEndpoint: Endpoint[String, ErrorInfo with Product with Serializable, Messages, Nothing] = {
            method match {
                case "get" => baseEndpoint.get.in(endpointName).in(query[String](paramName)).out(jsonBody[Messages])
                case "post" => baseEndpoint.post.in(endpointName).in(query[String](paramName)).out(jsonBody[Messages])
            }

        }

        val aRoute = aEndpoint.toRoutes(in => IO(Messages("200", s"$endpointName : $paramName : $in").asRight[ErrorInfo with Product with Serializable]))
    }

    class StreamEndpoints(paramName: String, endpointName: String, method: String) {

        val baseEndpoint = endpoint.errorOut(
            oneOf(
                statusMapping(StatusCode.NotFound, jsonBody[NotFound].description("not found")),
                statusMapping(StatusCode.Unauthorized, jsonBody[Unauthorized].description("unauthorized")),
                statusMapping(StatusCode.NoContent, emptyOutput.map(_ => NoContent)(_ => ())),
                statusDefaultMapping(jsonBody[Unknown].description("unknown"))
            )
        )

        val streamingEndpoint = {
            method match {
                case "get" => baseEndpoint.get.in(endpointName).in(query[String](paramName)).out(header[Long](HeaderNames.ContentLength)).out(streamBody[Stream[IO, Byte]](schemaFor[String], CodecFormat.TextPlain()))
                case "post" => baseEndpoint.post.in(endpointName).in(query[String](paramName)).out(header[Long](HeaderNames.ContentLength)).out(streamBody[Stream[IO, Byte]](schemaFor[String], CodecFormat.TextPlain()))
            }
        }

        def createStream(in: String) = {
            val size = 100L
            val responseMsg = Messages("200", in)
            val responseMsgJson = responseMsg.asJson.toString()
            val listChar = responseMsgJson.toList
            val streamProcess = Stream
                .emit(listChar)
//                .repeat
                .flatMap(list => Stream.chunk(Chunk.seq(list)))
//                .metered[IO](50.millis)
                .take(size)
                .covary[IO]
                .map(_.toByte)
                .pure[IO]
                .map(s => Right((size, s)))
            streamProcess
        }

        val streamingRoute: HttpRoutes[IO] = streamingEndpoint.toRoutes {createStream _}

    }

    class DatabaseEndpoints(dataSource: String, driver: String, url: String, user: String, password: String, paramName: String, endpointName: String, method: String) {

        val xa = Transactor.fromDriverManager[IO](driver, url, user, password)
        case class Country(code: String, name: String, pop: Int, gnp: Option[Double])
        case class City(id: Int, name: String, countryCode: String, district: String, population: Int)
        case class UpdateCity(idUpdate: Int, name: String, countryCode: String, district: String, population: Int)

        def select(n: String): ConnectionIO[Option[Country]] =
            sql"select code, name, population, gnp from country where name = $n".query[Country].option

        def insert(c: City) =
            sql"insert into city (id, name, countrycode, district, population) values (${c.id}, ${c.name}, ${c.countryCode}, ${c.district}, ${c.population})".update

        def update(c: UpdateCity) =
            sql"update city set name = ${c.name}, countrycode = ${c.countryCode}, district = ${c.district}, population = ${c.population} where id = ${c.idUpdate}".update

        val baseEndpoint = endpoint.errorOut(
            oneOf(
                statusMapping(StatusCode.NotFound, jsonBody[NotFound].description("not found")),
                statusMapping(StatusCode.Unauthorized, jsonBody[Unauthorized].description("unauthorized")),
                statusMapping(StatusCode.NoContent, emptyOutput.map(_ => NoContent)(_ => ())),
                statusDefaultMapping(jsonBody[Unknown].description("unknown"))
            )
        )

        val aEndpoint: Endpoint[String, ErrorInfo with Product with Serializable, Messages, Nothing] = {
            method match {
                case "select" => baseEndpoint.get.in(endpointName).in(query[String](paramName)).out(jsonBody[Messages])
                case "insert" => baseEndpoint.put.in(endpointName).in(stringBody).out(jsonBody[Messages])
                case "update" => baseEndpoint.post.in(endpointName).in(stringBody).out(jsonBody[Messages])
            }

        }

        def selectLogic(n: String) = {
            val qResult = select(n).transact(xa).unsafeRunSync match {
                case Some(x) =>
                    x match {
                        case in: Country => Messages("200", s"${in.asJson}")
                    }
                case _ => Messages("400", "Error")
            }
            qResult
        }

        def insertLogic(n: String) = {
            val qResult = decode[City](n) match {
                case Right(r) => insert(r).run.transact(xa).unsafeRunSync match {
                    case x: Int => Messages("200", s"Updated $x record(s).")
//                    case _ => Messages("400", "Error")
                }
                case Left(l) => Messages("400", "Error")
            }
            qResult
        }

        def updateLogic(n: String) = {
            val qResult = decode[UpdateCity](n) match {
                case Right(r) => update(r).run.transact(xa).unsafeRunSync match {
                    case x: Int => Messages("200", s"Updated $x record(s).")
//                    case _ => Messages("400", "Error")
                }
                case Left(l) => Messages("400", "Error")
            }
            qResult
        }

        def aRoute[F[_]: Sync](implicit serverOption: Http4sServerOptions[F], fcs: ContextShift[F]) = {
            method match {
                case "select" => aEndpoint.toRoutes ( in => selectLogic(in).asRight[ErrorInfo with Product with Serializable].pure[F] )
                case "insert" => aEndpoint.toRoutes ( in => insertLogic(in).asRight[ErrorInfo with Product with Serializable].pure[F] )
                case "update" => aEndpoint.toRoutes ( in => updateLogic(in).asRight[ErrorInfo with Product with Serializable].pure[F] )
            }
        }

    }
}