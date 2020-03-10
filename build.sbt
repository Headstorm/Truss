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
libraryDependencies += "com.softwaremill.sttp.tapir" %% "tapir-sttp-client" % "0.12.23"
libraryDependencies += "com.softwaremill.sttp.client" %% "core" % "2.0.4"
libraryDependencies += "com.softwaremill.sttp" %% "core" % "1.0.2"

scalacOptions ++= Seq(
    "-deprecation", // Emit warning and location for usages of deprecated APIs.
    "-encoding",
    "utf-8", // Specify character encoding used by source files.
    "-explaintypes", // Explain type errors in more detail.
    "-feature", // Emit warning and location for usages of features that should be imported explicitly.
    "-language:existentials", // Existential types (besides wildcard types) can be written and inferred
    "-language:experimental.macros", // Allow macro definition (besides implementation and application)
    "-language:higherKinds", // Allow higher-kinded types
    "-language:implicitConversions", // Allow definition of implicit functions called views
    "-language:postfixOps", // Allow postfix operator
    "-unchecked", // Enable additional warnings where generated code depends on assumptions.
    "-Xcheckinit", // Wrap field accessors to throw an exception on uninitialized access.
    "-Xfatal-warnings", // Fail the compilation if there are any warnings.
    "-Xlint:adapted-args", // Warn if an argument list is modified to match the receiver.
    "-Xlint:constant", // Evaluation of a constant arithmetic expression results in an error.
    "-Xlint:delayedinit-select", // Selecting member of DelayedInit.
    "-Xlint:doc-detached", // A Scaladoc comment appears to be detached from its element.
    "-Xlint:inaccessible", // Warn about inaccessible types in method signatures.
    "-Xlint:infer-any", // Warn when a type argument is inferred to be `Any`.
    "-Xlint:missing-interpolator", // A string literal appears to be missing an interpolator id.
    "-Xlint:nullary-override", // Warn when non-nullary `def f()' overrides nullary `def f'.
    "-Xlint:nullary-unit", // Warn when nullary methods return Unit.
    "-Xlint:option-implicit", // Option.apply used implicit view.
    "-Xlint:package-object-classes", // Class or object defined in package object.
    "-Xlint:poly-implicit-overload", // Parameterized overloaded implicit methods are not visible as view bounds.
    "-Xlint:private-shadow", // A private field (or class parameter) shadows a superclass field.
    "-Xlint:stars-align", // Pattern sequence wildcard must align with sequence component.
    "-Xlint:type-parameter-shadow", // A local type parameter shadows a type already in scope.
    "-Xlint:unused", // Unused imports,privates,locals,implicits
    "-Ywarn-dead-code", // Warn when dead code is identified.
    "-Ywarn-extra-implicit", // Warn when more than one implicit parameter section is defined.
    //       "-Ywarn-unused:implicits", // Warn if an implicit parameter is unused.
    //       "-Ywarn-unused:locals", // Warn if a local definition is unused.
    //       "-Ywarn-unused:params", // Warn if a value parameter is unused.
    //       "-Ywarn-unused:patvars", // Warn if a variable bound in a pattern is unused.
    //       "-Ywarn-unused:privates", // Warn if a private member is unused.
    "-Ywarn-value-discard", // Warn when non-Unit expression results are unused.
    "-Ylog-classpath"
)
