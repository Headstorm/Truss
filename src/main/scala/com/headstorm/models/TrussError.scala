package com.headstorm.models

import java.util.UUID

sealed trait TrussError extends Throwable {
  def message: String
  val errorId = UUID.randomUUID()
}

final case class RepositoryError(message: String) extends TrussError
