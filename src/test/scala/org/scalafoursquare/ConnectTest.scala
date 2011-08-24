package org.scalafoursquare

import org.junit.Test
import net.liftweb.common.Empty
import net.liftweb.util.Props
import org.specs.SpecsMatchers

object TestCaller extends FSCaller {
  def makeCall(req: FSRequest): String = req.endpoint match {
    case "venues/categories" => {
      """{"meta":{"code":200},"response":{"categories":[{"id":"fakeId","name":"Fake Category","pluralName":"Fake Categories","icon":"noIcon","categories":[]}]}}"""

    }
  }
}

class ConnectTest extends SpecsMatchers {

  val USER_TOKEN = Props.get("access.token.user").open_!
  val CONSUMER_KEY = Props.get("consumer.key").open_!
  val CONSUMER_SECRET = Props.get("consumer.secret").open_!
  val TEST_URL = Props.get("foursquare.test.url").open_!
  val API_VERSION = Props.get("api.version").open_!


  @Test
  def venueCategories() {
    val mockCaller = TestCaller
    val mockApp = FSApp(mockCaller)

    val mockVenueCategories = mockApp.venueCategories.get
    mockVenueCategories.meta must_== Meta(200, None, None)
    mockVenueCategories.notifications must_== None
    mockVenueCategories.response.categories.length must_== 1
    mockVenueCategories.response.categories(0).name must_== "Fake Category"
    mockVenueCategories.response.categories(0).pluralName must_== "Fake Categories"
    mockVenueCategories.response.categories(0).id must_== "fakeId"
    mockVenueCategories.response.categories(0).icon must_== "noIcon"
    mockVenueCategories.response.categories(0).categories.length must_== 0

    // This one actually makes a web call!
    
    val caller = HttpFSCaller(CONSUMER_KEY, CONSUMER_SECRET, TEST_URL, API_VERSION)
    val app = FSApp(caller)

    val venueCategories = app.venueCategories.get

    println(venueCategories.toString)
  }
}