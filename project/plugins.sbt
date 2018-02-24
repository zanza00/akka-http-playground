resolvers += Classpaths.typesafeReleases
resolvers += "Artima Maven Repository" at "http://repo.artima.com/releases"

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.4")

addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.2.0-M8")

addSbtPlugin("com.typesafe.sbt" % "sbt-multi-jvm" % "0.3.11")

addSbtPlugin("com.artima.supersafe" % "sbtplugin" % "1.1.3")
