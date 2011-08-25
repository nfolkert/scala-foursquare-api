package org.scalafoursquare

import org.junit.Test
import net.liftweb.common.Empty
import net.liftweb.util.Props
import org.specs.SpecsMatchers

class ConnectTest extends SpecsMatchers {

  val USER_TOKEN = Props.get("access.token.user").open_!
  val CONSUMER_KEY = Props.get("consumer.key").open_!
  val CONSUMER_SECRET = Props.get("consumer.secret").open_!
  val TEST_URL = Props.get("foursquare.test.url").open_!
  val API_VERSION = Props.get("api.version").open_!

  @Test
  def errorHandling() {
    val mockCaller = TestCaller
    val mockApp = FSApp(mockCaller)

    val failVenue = mockApp.venueDetail("missingId")

    failVenue.meta.code must_== 400
    failVenue.meta.errorType must_== Some("param_error")
    failVenue.response.isDefined must_== false

    val failEndpoint = mockApp.caller.makeCall(FSRequest("bad/endpoint/blargh"))
    failEndpoint must_== """{"meta":{"code":404, "errorType":"other", "errorDetail":"Endpoint not found"},"response":{}}"""
  }

  @Test
  def venueDetail() {
    val mockCaller = TestCaller
    val mockApp = FSApp(mockCaller)

    val mockVenue = mockApp.venueDetail("someVenueId")

    mockVenue.meta must_== Meta(200, None, None)
    mockVenue.notifications must_== None
    mockVenue.response.get.venue.name must_== "Fake Venue"
    mockVenue.response.get.venue.location.crossStreet must_== Some("At Fake Street")
    mockVenue.response.get.venue.mayor.count must_== 15
    mockVenue.response.get.venue.tags(0) must_== "a tag"

    // This one actually makes a web call!

    val caller = HttpFSCaller(CONSUMER_KEY, CONSUMER_SECRET, TEST_URL, API_VERSION)
    val app = FSApp(caller)

    val venue = app.venueDetail("1234")
    println(venue.toString)
  }

  @Test
  def userDetail() {
    val mockCaller = TestCaller
    val mockApp = FSApp(mockCaller)
    val mockUserApp = mockApp.user("Just Testing!")

    val mockSelf = mockUserApp.self
    val mockById = mockUserApp.userDetail("someUserId")

    println(mockSelf.toString)
    println(mockById.toString)
    mockSelf must_== mockById
    mockSelf.meta must_== Meta(200, None, None)
    mockSelf.notifications must_== None
    mockSelf.response.get.user.firstName must_== "Fake"
    mockSelf.response.get.user.mayorships.count must_== 20
    mockSelf.response.get.user.checkins.items(0).venue.get.name must_== "Fake Venue"
    mockSelf.response.get.user.checkins.items(0).venue.get.location.lat must_== Some(40.0)
    mockSelf.response.get.user.checkins.items(0).venue.get.location.lng must_== Some(-73.5)
    mockSelf.response.get.user.following.get.count must_== 70
    mockSelf.response.get.user.followers.isDefined must_== false
    mockSelf.response.get.user.scores.checkinsCount must_== 30

    // These actually make a web call!

    val caller = HttpFSCaller(CONSUMER_KEY, CONSUMER_SECRET, TEST_URL, API_VERSION)
    val userApp = FSApp(caller).user(USER_TOKEN)

    val self = userApp.self
    println(self.toString)

    val mtv = userApp.userDetail(660771.toString)
    println(mtv.toString)
  }

  @Test
  def venueCategories() {
    val mockCaller = TestCaller
    val mockApp = FSApp(mockCaller)

    val mockVenueCategories = mockApp.venueCategories
    mockVenueCategories.meta must_== Meta(200, None, None)
    mockVenueCategories.notifications must_== None
    mockVenueCategories.response.get.categories.length must_== 1
    mockVenueCategories.response.get.categories(0).name must_== "Fake Category"
    mockVenueCategories.response.get.categories(0).pluralName must_== "Fake Categories"
    mockVenueCategories.response.get.categories(0).id must_== "fakeId"
    mockVenueCategories.response.get.categories(0).icon must_== "noIcon"
    mockVenueCategories.response.get.categories(0).categories.length must_== 0

    // This one actually makes a web call!
    
    val caller = HttpFSCaller(CONSUMER_KEY, CONSUMER_SECRET, TEST_URL, API_VERSION)
    val app = FSApp(caller)

    val venueCategories = app.venueCategories

    println(venueCategories.toString)
  }
}