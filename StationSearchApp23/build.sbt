name := """StationSearchApp23"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  javaWs,
  filters,
  "postgresql" % "postgresql" % "9.1-901.jdbc4",
  "net.arnx" % "jsonic" % "1.3.3",  
  "org.webjars" % "jquery" % "2.1.1",
  "org.webjars" % "bootstrap" % "3.1.1",
  "org.webjars" %% "webjars-play" % "2.3.0"
)
