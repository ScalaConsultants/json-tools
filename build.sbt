name :="jsonTools"

scalaVersion :="2.10.3"

version :="0.1"

val liftVersion = "2.5"

resolvers ++= Seq(
  "Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository",
  "Maven Repo" at "http://repo1.maven.org/maven2/",
  "Typesafe Ivy Repo" at "http://repo.typesafe.com/typesafe/ivy-releases",
  "Typesafe Maven Repo" at "http://repo.typesafe.com/typesafe/releases/",
  "Scala Tools Releases" at "http://scala-tools.org/repo-releases/",
  "Java.net Maven2 Repository" at "http://download.java.net/maven/2/"
)

libraryDependencies ++= Seq(
  "net.liftweb" %% "lift-json" % liftVersion,
  "net.databinder.dispatch" %% "dispatch-core" % "0.11.0",
  "org.specs2" %% "specs2" % "2.2" % "test")
