package org.scalafoursquare

import org.junit.Test
import net.liftweb.common.Empty
import net.liftweb.util.Props

class ConnectTest {

  val USERLESS_TOKEN = Props.get("access.token.userless").open_!
  val USER_TOKEN = Props.get("access.token.user").open_!
  val CONSUMER_KEY = Props.get("consumer.key").open_!
  val CONSUMER_SECRET = Props.get("consumer.secret").open_!
  val TEST_URL = Props.get("foursquare.test.url").open_!
  val API_VERSION = Props.get("api.version").open_!


  @Test
  def simpleTest() {

    println(List(USERLESS_TOKEN, USER_TOKEN, CONSUMER_KEY, CONSUMER_SECRET, TEST_URL, API_VERSION))

    val json = App(CONSUMER_KEY, CONSUMER_SECRET, API_VERSION, TEST_URL).call(USERLESS_TOKEN).venueCategories.get
    println(json)
  }

}