name := "ScalaFS"

version := "1.0"

scalaVersion := "2.8.1"

libraryDependencies ++= Seq(
  "junit" % "junit" % "4.8" % "test",
  "net.liftweb" %% "lift-common" % "2.4-M1" % "compile",
  "net.liftweb" %% "lift-util" % "2.4-M1" % "compile",
  "net.liftweb" %% "lift-json" % "2.4-M1" % "compile"
)

resolvers += "Scala-Tools Maven2 Snapshots Repository" at "http://scala-tools.org/repo-snapshots"
