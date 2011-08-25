package org.scalafoursquare

import org.scalafoursquare.call.{AuthApp, UserlessApp, HttpCaller, Request}
import org.scalafoursquare.response.{Meta}
import org.junit.Test
import org.specs.SpecsMatchers
import net.liftweb.common.Empty
import net.liftweb.util.Props

class Example extends SpecsMatchers {

  val USER_TOKEN = Props.get("access.token.user").open_!
  val CONSUMER_KEY = Props.get("consumer.key").open_!
  val CONSUMER_SECRET = Props.get("consumer.secret").open_!
  val TEST_URL = Props.get("foursquare.test.url").open_!
  val API_VERSION = Props.get("api.version").open_!

  @Test
  def example() {
    val caller = HttpCaller(CONSUMER_KEY, CONSUMER_SECRET, TEST_URL, API_VERSION)
    val app = new AuthApp(caller, USER_TOKEN)

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