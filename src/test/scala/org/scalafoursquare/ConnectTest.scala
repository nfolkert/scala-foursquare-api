package org.scalafoursquare

import org.junit.Test
import net.liftweb.common.Empty

class ConnectTest {

  val TOKEN = "Test Token" // TODO: read test token from properties
  val FOURSQUARE_URL = "https://api.foursquare.com/v2/"

  @Test
  def simpleTest() {

    val json = new VenueCategories(Call(TOKEN, url=FOURSQUARE_URL)).makeCall
    println(json)
  }

}