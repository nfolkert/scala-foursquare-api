name := "scalafoursquare"

organization := "org.scalafoursquare"

scalaVersion := "2.9.1"

resolvers += "Bryan J. Swift repo" at "http://repos.bryanjswift.com/maven2/"
 
libraryDependencies ++= Seq(
  "net.liftweb"             %% "lift-json"            % "2.4-M5",
  "net.liftweb"             %% "lift-util"            % "2.4-M5",
  "net.liftweb"             %% "lift-common"          % "2.4-M5",
  "org.scalaj"              %% "scalaj-collection"    % "1.2",
  "org.scalaj"              %% "scalaj-http"          % "0.2.9"   withSources(),
  "org.mockito"             % "mockito-all"           % "1.8.4"     % "test",
  "junit"                   % "junit"                 % "4.8.2"     % "test",
  "org.scala-tools.testing" %% "specs"                % "1.6.9"     % "test",
  "eu.henkelmann"  %  "junit_xml_listener"  % "0.2"   % "test->default",
  "com.novocode"   %  "junit-interface"     % "0.7"   % "test->default"
)


version := "0.0.4"
