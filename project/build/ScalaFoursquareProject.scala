import sbt._

class ScalaFoursquareProject(info: ProjectInfo) extends DefaultProject(info) {
  override def managedStyle = ManagedStyle.Maven
  override def packageSrcJar= defaultJarPath("-sources.jar")

  val liftCommon =        "net.liftweb"  % "lift-common_2.9.1"       % "2.4"   % "compile" withSources()
  val liftUtil =          "net.liftweb"  % "lift-util_2.9.1"         % "2.4"   % "compile" withSources()
  val liftJson =          "net.liftweb"  % "lift-json_2.9.1"         % "2.4"   % "compile" withSources()
  val scalajCollection  = "org.scalaj"   % "scalaj-collection_2.9.1" % "1.2"
  val scalajHttp =        "org.scalaj"   % "scalaj-http_2.9.1"       % "0.3.1" % "compile" withSources()
  val junit =             "junit"        % "junit"                   % "4.8.2" % "test" withSources()
  val specs =             "org.scala-tools.testing" % "specs_2.9.1"  % "1.6.9" % "test" withSources()
  val junitInterface =    "com.novocode" % "junit-interface"         % "0.6"   % "test"

  val bryanjswift = "Bryan J Swift Repository" at "http://repos.bryanjswift.com/maven2/"
  override def testFrameworks = super.testFrameworks ++ List(new TestFramework("com.novocode.junit.JUnitFrameworkNoMarker"))
}
