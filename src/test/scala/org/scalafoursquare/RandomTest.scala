package org.scalafoursquare

import net.liftweb.common.Empty
import net.liftweb.util.Props
import org.specs.SpecsMatchers
import util.Random
import org.junit.{Ignore, Test}

class RandomTest extends SpecsMatchers {

  val USER_TOKEN = Props.get("access.token.user").open_!
  val CONSUMER_KEY = Props.get("consumer.key").open_!
  val CONSUMER_SECRET = Props.get("consumer.secret").open_!
  val TEST_URL = Props.get("foursquare.test.url").open_!
  val API_VERSION = Props.get("api.version").open_!

  @Ignore
  def randomVenues() {
    val r = new Random(1977)

    val caller = HttpFSCaller(CONSUMER_KEY, CONSUMER_SECRET, TEST_URL, API_VERSION)
    val app = FSApp(caller)
    for (i <- 1 to 10) {
      val venueId = r.nextInt(10000000)
      val venue = app.venueDetail(venueId.toString).get
      println(venue.toString)
    }
  }

  @Ignore
  def randomUsers() {
    val r = new Random(1977)

    val caller = HttpFSCaller(CONSUMER_KEY, CONSUMER_SECRET, TEST_URL, API_VERSION)
    val userApp = FSApp(caller).user(USER_TOKEN)
    for (i <- 1 to 10) {
      val userId = r.nextInt(10000000)
      val user = userApp.userDetail(userId.toString).get
      println(user.toString)
    }
  }
}