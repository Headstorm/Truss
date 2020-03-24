name := "Truss"

version := "0.1"

scalaVersion := "2.12.10"

val Http4sVersion = "0.21.0"
val CirceVersion = "0.13.0"
val Specs2Version = "4.8.3"
val LogbackVersion = "1.2.3"

libraryDependencies ++= Seq(

    // Start with this one
    "org.tpolecat" %% "doobie-core"      % "0.8.6",

    // And add any of these as needed
    "org.tpolecat" %% "doobie-h2"        % "0.8.6",          // H2 driver 1.4.200 + type mappings.
    "org.tpolecat" %% "doobie-hikari"    % "0.8.6",          // HikariCP transactor.
    "org.tpolecat" %% "doobie-postgres"  % "0.8.6",          // Postgres driver 42.2.9 + type mappings.
    "org.tpolecat" %% "doobie-quill"     % "0.8.6",          // Support for Quill 3.4.10
    "org.tpolecat" %% "doobie-specs2"    % "0.8.6" % "test", // Specs2 support for typechecking statements.
    "org.tpolecat" %% "doobie-scalatest" % "0.8.6" % "test"  // ScalaTest support for typechecking statements.

)

libraryDependencies ++= Seq(
    "org.http4s"      %% "http4s-blaze-server" % Http4sVersion,
    "org.http4s"      %% "http4s-blaze-client" % Http4sVersion,
    "org.http4s"      %% "http4s-circe"        % Http4sVersion,
    "org.http4s"      %% "http4s-dsl"          % Http4sVersion,
    "io.circe"        %% "circe-generic"       % CirceVersion,
    "org.specs2"      %% "specs2-core"         % Specs2Version % "test",
    "ch.qos.logback"  %  "logback-classic"     % LogbackVersion
)

libraryDependencies += "com.softwaremill.sttp.tapir" %% "tapir-core" % "0.12.23"
libraryDependencies += "com.softwaremill.sttp.tapir" %% "tapir-akka-http-server" % "0.12.23"
libraryDependencies += "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % "0.12.23"
libraryDependencies += "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % "0.12.23"
libraryDependencies += "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs" % "0.12.23"
libraryDependencies += "com.softwaremill.sttp.tapir" %% "tapir-openapi-circe-yaml" % "0.12.23"
libraryDependencies += "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-akka-http" % "0.12.23"
libraryDependencies += "com.softwaremill.sttp.tapir" %% "tapir-sttp-client" % "0.12.23"
libraryDependencies += "com.softwaremill.sttp.client" %% "core" % "2.0.4"
libraryDependencies += "com.softwaremill.sttp" %% "core" % "1.0.2"
libraryDependencies += "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-http4s" % "0.12.23"

val circeVersion = "0.12.3"

libraryDependencies ++= Seq(
    "io.circe" %% "circe-core",
    "io.circe" %% "circe-generic",
    "io.circe" %% "circe-parser"
).map(_ % circeVersion)

val silencerVersion = "1.4.2"

libraryDependencies += "com.github.ghik" % "silencer-plugin_2.12" % "1.4.2"
libraryDependencies += "net.liftweb" %% "lift-json" % "3.4.1"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.8" % "test"

libraryDependencies += "com.github.pureconfig" %% "pureconfig" % "0.12.3"

libraryDependencies ++= Seq(

    // Start with this one
    "org.tpolecat" %% "doobie-core"      % "0.8.6",

    // And add any of these as needed
    "org.tpolecat" %% "doobie-h2"        % "0.8.6",          // H2 driver 1.4.200 + type mappings.
    "org.tpolecat" %% "doobie-hikari"    % "0.8.6",          // HikariCP transactor.
    "org.tpolecat" %% "doobie-postgres"  % "0.8.6",          // Postgres driver 42.2.9 + type mappings.
    "org.tpolecat" %% "doobie-quill"     % "0.8.6",          // Support for Quill 3.4.10
    "org.tpolecat" %% "doobie-specs2"    % "0.8.6" % "test", // Specs2 support for typechecking statements.
    "org.tpolecat" %% "doobie-scalatest" % "0.8.6" % "test"  // ScalaTest support for typechecking statements.

)

scalacOptions += "-Ypartial-unification"
