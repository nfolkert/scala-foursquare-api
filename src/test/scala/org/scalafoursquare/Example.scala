package org.scalafoursquare

import org.scalafoursquare.call.{AuthApp}
import org.junit.Test
import org.specs.SpecsMatchers

class Example extends SpecsMatchers {

  val P = TestUtil.propParams

  @Test
  def example() {
    val caller = TestUtil.httpCaller
    val app = new AuthApp(caller, P.USER_TOKEN)

    val meAndCategories = app.multi(app.self, app.venueCategories).get

    val me = meAndCategories.responses._1.get.response.get
    val cats = meAndCategories.responses._2.get.response.get

    println("Hi, I'm " + me.user.firstName)
    println("My favorite category is " + cats.categories(5).name)

    if (me.user.checkins.items.isEmpty)
      println("I've never been anywhere")
    else {
      val venueId = me.user.checkins.items(0).venue.get.id
      val venue = app.venueDetail(venueId).get.response.get
      println("I'm at " + venue.venue.name)
    }
  }
}
