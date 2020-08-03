import scala.sys.process._

name := """play-mongo-template"""
organization := "com.ww"
version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala, PlayAkkaHttp2Support, SwaggerPlugin)

scalaVersion := "2.13.1"

libraryDependencies ++= Seq(
  guice,
  "org.scalatestplus.play" % "scalatestplus-play_2.13" % "5.0.0",
  "org.mockito"            % "mockito-core"            % "3.2.4",
  "org.reactivemongo"      %% "reactivemongo"          % "0.20.3",
  "org.webjars"            % "swagger-ui"              % "3.24.3",
  "com.pauldijou"          %% "jwt-core"               % "4.2.0"
)

swaggerDomainNameSpaces := Seq("com.ww.requests", "com.ww.models")
swaggerV3 := true
swaggerTarget := baseDirectory.value / "public"

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature", "-Xfatal-warnings")

//scalafmtAll := true

coverageEnabled := true
coverageMinimum := 40
coverageFailOnMinimum := true

lazy val myCustomTask =
  taskKey[Unit]("description of myCustomTask. Run it using 'sbt myCustomTask'")
myCustomTask := {
  "ls -la" !
}
