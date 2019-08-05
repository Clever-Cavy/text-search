
enablePlugins(PackPlugin)

val versions = new {
  val AkkaHttp = "10.1.9"
  val Akka = "2.5.23"
}

lazy val testtask = (project in file(".")).
  settings(

    organization := "task",
    name := "textsearch",
    version := "0.0.1",
    scalaVersion := "2.11.8",

    libraryDependencies ++= Seq(
      "com.typesafe.scala-logging" %% "scala-logging" % "3.8.0",
      "org.slf4j" % "slf4j-simple" % "1.7.26",

      "com.typesafe.akka" %% "akka-http"            % versions.AkkaHttp,
      "com.typesafe.akka" %% "akka-stream"          % versions.Akka,
      "com.typesafe.akka" %% "akka-remote"          % versions.Akka,
      "com.typesafe.akka" %% "akka-http-spray-json" % versions.AkkaHttp,

      "com.typesafe.akka" %% "akka-http-testkit"    % versions.AkkaHttp % Test,
      "com.typesafe.akka" %% "akka-testkit"         % versions.Akka     % Test,
      "org.scalatest"     %% "scalatest" % "3.0.8" % "test"
    ),

    javaOptions ++= Seq("-Xms512M", "-Xmx2048M", "-XX:MaxPermSize=2048M", "-XX:+CMSClassUnloadingEnabled"),

    packMain := Map(
      "textsearch-server" -> "task.textsearch.server.ServerRunner",
      "textsearch-client" -> "task.textsearch.client.ClientRunner"
    )
  )