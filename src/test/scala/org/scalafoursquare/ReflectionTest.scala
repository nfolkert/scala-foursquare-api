package org.scalafoursquare

import call.{App, AuthApp, UserlessApp}
import org.specs.SpecsMatchers
import org.junit.Test
import java.lang.reflect.{TypeVariable, ParameterizedType}

class ReflectionTest extends SpecsMatchers {

  @Test
  def testReflect() {
    val userlessApp = new UserlessApp(TestCaller)
    val authApp = new AuthApp(TestCaller, "FakeToken")

    val userless = App.deriveInterface(userlessApp)
    val auth = App.deriveInterface(authApp)

    /*
    // Uncomment to see structure
    println("Userless App:")
    userless.map(_.pretty)
    println("Authenticated App:")
    auth.map(_.pretty)
    */
    
  }

}