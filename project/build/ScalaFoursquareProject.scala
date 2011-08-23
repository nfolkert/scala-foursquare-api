import sbt._

class ScalaJSProject(info: ProjectInfo) extends DefaultProject(info) {
  override def managedStyle = ManagedStyle.Maven

  val junit = "junit" % "junit" % "4.8.2" % "test" withSources()
  val liftCommon = "net.liftweb" %% "lift-common" % "2.4-M1" % "compile"
  val liftUtil = "net.liftweb" %% "lift-util" % "2.4-M1" % "compile"
  val liftJson = "net.liftweb" %% "lift-json" % "2.4-M1" % "compile"
  val specs = "org.scala-tools.testing" %% "specs" % "1.6.5" % "test" withSources()

  val bryanjswift = "Bryan J Swift Repository" at "http://repos.bryanjswift.com/maven2/"
  val junitInterface = "com.novocode" % "junit-interface" % "0.6" % "test"
  override def testFrameworks = super.testFrameworks ++ List(new TestFramework("com.novocode.junit.JUnitFrameworkNoMarker"))
}
