package com.headstorm.logging

import cats.effect.{Sync, Timer}
import cats.implicits._
import com.headstorm.models.TrussError
import io.odin._
import io.odin.formatter.Formatter
import io.odin.meta._

/** Functional logging algebra that is compatible with any effect F[_]
 * Effect F is lazily evaluated, therefore calling the logger methods isn't enough to emit the actual log.
 * It has to be called in for ... yield comprehension, flatMap, map or *>/>> operators in cats library.
 * Example:
 *   for {
 *     acaiError <- EntityNotFoundError(s"Error Message")
 *                  .asInstanceOf[TrussError]
 *                   .pure[F]
 *     logger <- customLogger
 *      _     <- logger.error(trussError.message, locusError)  // the second argument is for stack trace
 *   }
 *
 *   Or just simply use logAndReturn function to log error messages and get back F[TrussError]
 */
trait Logging[F[_]] {
  implicit def timer: Timer[F]
  implicit def sync: Sync[F]
  implicit val render: Render[TrussError] = Render.fromToString

  object Position {
    implicit def derivePosition(
                                 implicit fileName: sourcecode.File,
                                 packageName: sourcecode.Pkg,
                                 line: sourcecode.Line
                               ): Position = io.odin.meta.Position(fileName.value, fileName.value, packageName.value, line.value)
  }

  lazy val customLogger: F[Logger[F]] = consoleLogger[F](Formatter.colorful).pure[F]

  def logAndReturn[E <: TrussError](error: E)(implicit pos: Position): F[TrussError] =
    for {
      logger <- customLogger
      _      <- logger.error(error.message, error)
    } yield error

  def logError(message: String)(implicit pos: Position): F[Unit] = customLogger.flatMap(logger => logger.error(message))
  def logLocusError(message: String, error: TrussError)(implicit pos: Position): F[Unit] =
    customLogger.flatMap(logger => logger.error(s"errorId=${error.errorId.toString} $message", error))
  def logDebug(message: String)(implicit pos: Position): F[Unit] = customLogger.flatMap(logger => logger.debug(message))
  def logInfo(message: String)(implicit pos: Position): F[Unit]  = customLogger.flatMap(logger => logger.info(message))
}

