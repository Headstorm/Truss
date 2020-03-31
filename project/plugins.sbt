// for autoplugins
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.7.0")

addSbtPlugin("org.xerial.sbt" % "sbt-pack" % "0.12")

libraryDependencies += "org.scala-sbt" %% "scripted-plugin" % sbtVersion.value