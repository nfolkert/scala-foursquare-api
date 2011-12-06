package org.scalafoursquare

import org.scalafoursquare.call.{AuthApp, UserlessApp, Request}
import org.scalafoursquare.response.{Meta}
import org.junit.{Test}
import org.specs.SpecsMatchers

class CallTest extends SpecsMatchers {

  val P = TestUtil.propParams

  @Test
  def oauthUrl() {

    println(P.FS_URL + "/oauth2/authenticate?client_id=" + P.CONSUMER_KEY +
      "&response_type=token&redirect_uri=" + P.CALLBACK_URL)
  }

  @Test
  def errorHandling() {
    val mockCaller = TestCaller
    val mockApp = new UserlessApp(mockCaller)

    val failVenue = mockApp.venueDetail("missingId").get

    failVenue.meta.code must_== 400
    failVenue.meta.errorType must_== Some("param_error")
    failVenue.response.isDefined must_== false

    mockApp.venueDetail("missingId").fail must_== failVenue.meta

    case class ErrorTest()

    val failEndpoint = new Request[ErrorTest](mockApp, "/bad/endpoint/blargh").get
    failEndpoint.meta must_== Meta(404, Some("other"), Some("Endpoint not found"))

    new Request[ErrorTest](mockApp, "/bad/endpoint/blargh").fail must_== failEndpoint.meta
  }

  @Test
  def venueDetail() {
    val mockCaller = TestCaller
    val mockApp = new UserlessApp(mockCaller)

    val mockVenue = mockApp.venueDetail("someVenueId").get

    mockVenue.meta must_== Meta(200, None, None)
    mockVenue.notifications must_== None
    mockVenue.response.get.venue.name must_== "Fake Venue"
    mockVenue.response.get.venue.location.crossStreet must_== Some("At Fake Street")
    mockVenue.response.get.venue.mayor.count must_== 15
    mockVenue.response.get.venue.tags(0) must_== "a tag"

    mockApp.venueDetail("someVenueId").expect must_== mockVenue.response.get
  }

  @Test
  def userDetail() {
    val mockCaller = TestCaller
    val mockUserApp = new AuthApp(mockCaller, "Just Testing!")

    val mockSelf = mockUserApp.self.get
    val mockById = mockUserApp.userDetail("someUserId").get

    println(mockSelf.toString)
    println(mockById.toString)
    mockSelf must_== mockById
    mockSelf.meta must_== Meta(200, None, None)
    mockSelf.notifications must_== None
    mockSelf.response.get.user.firstName must_== "Fake"
    mockSelf.response.get.user.mayorships.count must_== 20
    mockSelf.response.get.user.checkins.items.get(0).venue.get.name must_== "Fake Venue"
    mockSelf.response.get.user.checkins.items.get(0).venue.get.location.lat must_== Some(40.0)
    mockSelf.response.get.user.checkins.items.get(0).venue.get.location.lng must_== Some(-73.5)
    mockSelf.response.get.user.following.get.count must_== 70
    mockSelf.response.get.user.followers.isDefined must_== false
    mockSelf.response.get.user.scores.checkinsCount must_== 30

    mockUserApp.self.expect must_== mockSelf.response.get
  }

  @Test
  def venueCategories() {
    val mockCaller = TestCaller
    val mockApp = new UserlessApp(mockCaller)

    val mockVenueCategories = mockApp.venueCategories.get
    mockVenueCategories.meta must_== Meta(200, None, None)
    mockVenueCategories.notifications must_== None
    mockVenueCategories.response.get.categories.length must_== 1
    mockVenueCategories.response.get.categories(0).name must_== "Fake Category"
    mockVenueCategories.response.get.categories(0).pluralName must_== "Fake Categories"
    mockVenueCategories.response.get.categories(0).id must_== Some("fakeId")
    mockVenueCategories.response.get.categories(0).icon.prefix must_== Some("noImage")
    mockVenueCategories.response.get.categories(0).categories.isDefined must_== false

    mockApp.venueCategories.expect must_== mockVenueCategories.response.get
  }

  @Test
  def multiNoAuth() {
    val mockCaller = TestCaller
    val mockApp = new UserlessApp(mockCaller)

    val mockVenueReq = mockApp.venueDetail("someVenueId")
    val mockCategoryReq = mockApp.venueCategories
    val mockMulti = mockApp.multi(mockVenueReq, mockCategoryReq).get
    val mockVenue = mockMulti.responses._1.get
    val mockVenueCategories = mockMulti.responses._2.get

    mockMulti.responses._3 must_== None
    mockMulti.responses._4 must_== None
    mockMulti.responses._5 must_== None

    mockVenue.meta must_== Meta(200, None, None)
    mockVenue.notifications must_== None
    mockVenue.response.get.venue.name must_== "Fake Venue"
    mockVenue.response.get.venue.location.crossStreet must_== Some("At Fake Street")
    mockVenue.response.get.venue.mayor.count must_== 15
    mockVenue.response.get.venue.tags(0) must_== "a tag"

    mockVenueCategories.meta must_== Meta(200, None, None)
    mockVenueCategories.notifications must_== None
    mockVenueCategories.response.get.categories.length must_== 1
    mockVenueCategories.response.get.categories(0).name must_== "Fake Category"
    mockVenueCategories.response.get.categories(0).pluralName must_== "Fake Categories"
    mockVenueCategories.response.get.categories(0).id must_== Some("fakeId")
    mockVenueCategories.response.get.categories(0).icon.prefix must_== Some("noImage")
    mockVenueCategories.response.get.categories(0).categories.isDefined must_== false

    mockApp.multi(mockVenueReq, mockCategoryReq).expect must_== mockMulti.responses
  }

  @Test
  def multiAuthed() {
    val mockCaller = TestCaller
    val mockUserApp = new AuthApp(mockCaller, "Just Testing!")

    val mockSelfReq = mockUserApp.self
    val mockByIdReq = mockUserApp.userDetail("someUserId")
    val mockMulti = mockUserApp.multi(mockSelfReq, mockByIdReq).get
    val mockSelf = mockMulti.responses._1.get
    val mockById = mockMulti.responses._2.get

    mockMulti.responses._3 must_== None
    mockMulti.responses._4 must_== None
    mockMulti.responses._5 must_== None

    println(mockSelf.toString)
    println(mockById.toString)
    mockSelf must_== mockById
    mockSelf.meta must_== Meta(200, None, None)
    mockSelf.notifications must_== None
    mockSelf.response.get.user.firstName must_== "Fake"
    mockSelf.response.get.user.mayorships.count must_== 20
    mockSelf.response.get.user.checkins.items.get(0).venue.get.name must_== "Fake Venue"
    mockSelf.response.get.user.checkins.items.get(0).venue.get.location.lat must_== Some(40.0)
    mockSelf.response.get.user.checkins.items.get(0).venue.get.location.lng must_== Some(-73.5)
    mockSelf.response.get.user.following.get.count must_== 70
    mockSelf.response.get.user.followers.isDefined must_== false
    mockSelf.response.get.user.scores.checkinsCount must_== 30

    mockUserApp.multi(mockSelfReq, mockByIdReq).expect must_== mockMulti.responses
  }
}