import sbt._

object Dependencies {

  val playJson = "com.typesafe.play" %% "play-json" % "2.6.9"

  val sprayJson = "io.spray" %%  "spray-json" % "1.3.5"

  object akka {

    val http = "com.typesafe.akka" %% "akka-http" % "10.1.7"

    val actor = "com.typesafe.akka" %% "akka-actor" % "2.5.20"
    
    val stream = "com.typesafe.akka" %% "akka-stream" % "2.5.20"
  }

  object lihaoiy {
    val Version = "0.7.1"

    val uJson = "com.lihaoyi" %% "ujson" % Version

    val uPickle = "com.lihaoyi" %% "upickle" % Version
  }

  object jsoniter {
    val Version = "0.39.0"
    val libs = Seq(
      "com.github.plokhotnyuk.jsoniter-scala" %% "jsoniter-scala-core" % Version % Compile,
      "com.github.plokhotnyuk.jsoniter-scala" %% "jsoniter-scala-macros" % Version % Provided)
  }

  object json4s {
    val Version = "3.6.4"

    val ast = "org.json4s" %% "json4s-ast" % Version

    val native = "org.json4s" %% "json4s-native" % Version

    val jackson = "org.json4s" %% "json4s-jackson" % Version
  }

  object circe {
    val Version = "0.10.0"

    val libs = Seq(
      "io.circe" %% "circe-core",
      "io.circe" %% "circe-generic",
      "io.circe" %% "circe-parser") map { _ % Version }
  }

  val scalatest = "org.scalatest" %% "scalatest" % "3.0.5"
}
