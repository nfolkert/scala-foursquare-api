package org.scalafoursquare

import org.scalafoursquare.call.{AuthApp, UserlessApp, HttpCaller, Request}
import org.scalafoursquare.response.{Meta}
import org.junit.Test
import org.specs.SpecsMatchers
import net.liftweb.common.Empty
import net.liftweb.util.Props

class Example extends SpecsMatchers {

  val P = TestUtil.propParams

  @Test
  def example() {
    val caller = HttpCaller(P.CONSUMER_KEY, P.CONSUMER_SECRET, P.TEST_URL, P.API_VERSION)
    val app = new AuthApp(caller, P.USER_TOKEN)

    val meAndCategories = app.multi(app.self, app.venueCategories).get

    val me = meAndCategories.responses._1.get.response.get
    val cats = meAndCategories.responses._2.get.response.get

    println("Hi, I'm " + me.user.firstName)
    println("My favorite category is " + cats.categories(5).name)

    val venueId = me.user.checkins.items(0).venue.get.id

    val venue = app.venueDetail(venueId).get.response.get

    println("I'm at " + venue.venue.name)
  }
}