enablePlugins(JavaServerAppPackaging)

name := "drone-service"

version := "0.1"

organization := "net.zanzapla"

scalaVersion := "2.12.2"

resolvers ++= Seq(
  "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
  "Artima Maven Repository" at "http://repo.artima.com/releases",
  Resolver.bintrayRepo("hseeberger", "maven"))

libraryDependencies ++= {
  val AkkaVersion = "2.4.18"
  val AkkaHttpVersion = "10.0.6"
  val Json4sVersion = "3.5.2"
  val ScalaTestVersion = "3.0.4"
  Seq(
    "com.typesafe.akka" %% "akka-slf4j" % AkkaVersion,
    "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-testkit" % AkkaHttpVersion,
    "ch.qos.logback" % "logback-classic" % "1.2.3",
    "org.json4s" %% "json4s-native" % Json4sVersion,
    "org.json4s" %% "json4s-ext" % Json4sVersion,
    "de.heikoseeberger" %% "akka-http-json4s" % "1.16.0",
    "org.scalactic" %% "scalactic" % ScalaTestVersion,
    "org.scalatest" %% "scalatest" % ScalaTestVersion % "test",
    "org.scalamock" %% "scalamock" % "4.1.0" % "test"
  )
}

// Assembly settings
mainClass in Global := Some("net.zanzapla.drone.management.Main")

assemblyJarName in assembly := "drone-service-server.jar"
