import Dependencies._

lazy val scala212 = "2.12.8"
lazy val scala211 = "2.11.12"

lazy val commons = Seq(
organization        := "com.github.andyglow",
name                := "json-facade",
version             := "0.1",
scalaVersion        := scala211,
publishMavenStyle   := true,
publishArtifact     := true,
crossScalaVersions  := List(scala212, scala211),
scalacOptions       ++= {
    val options = Seq(
      "-encoding", "UTF-8",
      "-feature",
      "-unchecked",
      "-deprecation",
      "-Xfatal-warnings",
      "-Xlint",
      "-Yno-adapted-args",
      "-Ywarn-dead-code",
      "-Ywarn-numeric-widen",
      "-Xfuture")
  
    // WORKAROUND https://github.com/scala/scala/pull/5402
    CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, 12)) => options.map {
        case "-Xlint"               => "-Xlint:-unused,_"
        case "-Ywarn-unused-import" => "-Ywarn-unused:imports,-patvars,-privates,-locals,-params,-implicits"
        case other                  => other
      }
      case _             => options
    }
  },
  libraryDependencies ++= Seq(scalatest % Test))

lazy val api = project.in(file("api"))
  .settings(
    commons,
    name := "json-facade-api")

lazy val playF = project.in(file("impl-play-json"))
  .dependsOn(api % "compile->compile;test->test")
  .settings(
    commons,
    name := "json-facade-play-json",
    libraryDependencies += playJson)

lazy val circeF = project.in(file("impl-circe"))
  .dependsOn(api % "compile->compile;test->test")
  .settings(
    commons,
    name := "json-facade-play-json",
    libraryDependencies ++= circe.libs)

lazy val sprayF = project.in(file("impl-spray"))
  .dependsOn(api % "compile->compile;test->test")
  .settings(
    commons,
    name := "json-facade-spray",
    libraryDependencies += sprayJson)

lazy val json4sNativeF = project.in(file("impl-json4s-native"))
  .dependsOn(api % "compile->compile;test->test")
  .settings(
    commons,
    name := "json-facade-json4s-native",
    libraryDependencies ++= Seq(json4s.ast, json4s.native))

lazy val json4sJacksonF = project.in(file("impl-json4s-jackson"))
  .dependsOn(api % "compile->compile;test->test")
  .settings(
    commons,
    name := "json-facade-json4s-jackson",
    libraryDependencies ++= Seq(json4s.ast, json4s.jackson))

lazy val uJsonF = project.in(file("impl-ujson"))
  .dependsOn(api % "compile->compile;test->test")
  .settings(
    commons,
    name := "json-facade-ujson",
    libraryDependencies ++= Seq(lihaoiy.uJson, lihaoiy.uPickle))

lazy val jsoniterF = project.in(file("impl-jsoniter"))
  .dependsOn(api % "compile->compile;test->test")
  .settings(
    commons,
    name := "json-facade-jsoniter",
    libraryDependencies ++= jsoniter.libs)

lazy val akkaHttpAdapter = project.in(file("adapter-akka-http"))
  .dependsOn(api % "compile->compile;test->test", playF % "compile->test")
  .settings(
    commons,
    name := "json-facade-akka-http",
    libraryDependencies ++= Seq(akka.http, akka.actor % Provided, akka.stream % Provided, playJson % Test))

lazy val root = project.in(file("."))
  .aggregate(api, playF, circeF, sprayF, json4sNativeF, json4sJacksonF, uJsonF, jsoniterF, akkaHttpAdapter)
  .settings(
    name := "json-facade",
    crossScalaVersions := Nil,
    publish / skip := true,
    publishArtifact := false,
    aggregate in update := false)
