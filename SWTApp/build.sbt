name := "SWTApp"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  filters,
  "postgresql" % "postgresql" % "9.1-901.jdbc4",
  "com.github.twitter" % "bootstrap" % "2.0.2",
  "net.arnx" % "jsonic" % "1.3.3",  
  "org.webjars" % "jquery" % "2.1.0-2",
  "org.webjars" % "bootstrap" % "3.1.1",
  "org.webjars" %% "webjars-play" % "2.2.1-2"
)     

play.Project.playJavaSettings

resolvers += "webjars" at "http://webjars.github.com/m2"