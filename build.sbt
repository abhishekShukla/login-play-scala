name := "loginTemplate"

version := "1.0-SNAPSHOT"

resolvers += Resolver.url("sbt-plugin-releases", new URL("http://repo.scala-sbt.org/scalasbt/sbt-plugin-releases/"))(Resolver.ivyStylePatterns)

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  "ws.securesocial" %% "securesocial" % "2.1.3",
  "org.squeryl" % "squeryl_2.10" % "0.9.5-6",
  "mysql" % "mysql-connector-java" % "5.1.27",
  "org.scalatest" %% "scalatest" % "2.1.0" % "test"
)     
 

play.Project.playScalaSettings
