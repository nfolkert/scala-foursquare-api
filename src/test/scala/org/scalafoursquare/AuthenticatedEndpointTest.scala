package org.scalafoursquare

import org.scalafoursquare.call.{AuthApp}
import org.specs.SpecsMatchers
import org.junit.{Test, Ignore}

class AuthenticatedEndpointTest extends SpecsMatchers {

  val P = TestUtil.propParams

  val E = EndpointTest

  val caller = TestUtil.httpCaller
  val app = new AuthApp(caller, P.USER_TOKEN)

  // MAY NEED TO MODIFY THESE IF TIPS/SPECIALS/VENUES HAVE CHANGED!  TODO: MAY WANT TO SET IN PROPERTIES TO ENSURE ACCESSIBILITY?
  def VENUE_ID = "49d22274f964a5209a5b1fe3"
  def ANOTHER_VENUE_ID = "4a468cd9f964a52015a91fe3"
  def TIP_ID = "4e5d72bbbd41bd3bc3a94bda"
  def SPECIAL_ID = "4e5d778dbd41022d87273eac"
  def USER_ID = "32"
  def SELF_ID = "10002336"

  @Test
  def self() {
    E.test(app.self)
  }

  @Test
  def userDetail() {
    E.test(app.userDetail(USER_ID))
  }

  @Test
  def updateDetail() {
    // TODO
  }

  @Test
  def photoDetail() {
    // TODO
  }

  @Test
  def settingsDetail() {
    // TODO
  }

  @Test
  def checkinDetail() {

  }

  @Test
  def leaderboard() {
    E.test(app.leaderboard())
  }

  @Test
  def userSearch() {
    E.test(app.userSearch(name=Some("Nate")))
  }

  @Test
  def userRequests() {
    E.test(app.userRequests)
  }

  @Test
  def recentCheckins() {
    E.test(app.recentCheckins())
  }

  @Test
  def notifications() {
    E.test(app.notifications())
  }

  @Test
  def allSettings() {
    E.test(app.allSettings)
  }

  @Test
  def selfBadges() {
    E.test(app.selfBadges)
  }

  @Test
  def userBadges() {
    E.test(app.userBadges(USER_ID))
  }

  @Test
  def selfCheckins() {
    E.test(app.selfCheckins())
  }

  @Test
  def selfFriends() {
    E.test(app.selfFriends())
  }

  @Test
  def userFriends() {
    E.test(app.userFriends(USER_ID))
  }

  @Test
  def selfMayorships() {
    E.test(app.selfMayorships)
  }

  @Test
  def userMayorships() {
    E.test(app.userMayorships(USER_ID))
  }

  @Test
  def selfTips() {
    E.test(app.selfTips())
  }

  @Test
  def userTips() {
    E.test(app.userTips(USER_ID))
  }

  @Test
  def selfTodos() {
    E.test(app.selfTodos(sort=Some("recent")))
  }

  @Test
  def userTodos() {
    E.test(app.userTodos(SELF_ID, sort=Some("recent"))) // TODO: need to check recent
  }

  @Test
  def selfVenueHistory() {
    E.test(app.selfVenueHistory())
  }

  // USERLESS ENDPOINTS WITH THE AUTHENTICATED APP

  @Test
  def venueCategories() {
    E.test(app.venueCategories)
  }

  @Test
  def venueDetail() {
    E.test(app.venueDetail(VENUE_ID))
  }

  @Test
  def tipDetail() {
    E.test(app.tipDetail(TIP_ID))
  }

  @Test
  def specialDetail() {
    E.test(app.specialDetail(SPECIAL_ID, VENUE_ID))
  }

  @Test
  def venueHereNow() {
    E.test(app.venueHereNow(VENUE_ID))
  }

  @Test
  def venueTips() {
    E.test(app.venueTips(VENUE_ID))
  }

  @Test
  def venuePhotos() {
    E.test(app.venuePhotos(VENUE_ID, "venue"))
  }

  @Test
  def venueLinks() {
    E.test(app.venueLinks(VENUE_ID))
  }

  @Test
  def exploreVenues() {
    E.test(app.exploreVenues(40.6748, -73.9721))
  }

  @Test
  def venueSearch() {
    E.test(app.venueSearch(40.6748, -73.9721))
  }

  @Test
  def venueTrending() {
    E.test(app.venueTrending(40.6748, -73.9721))
  }

  @Test
  def tipsSearch() {
    E.test(app.tipsSearch(40.6748, -73.9721))
  }

  @Test
  def specialsSearch() {
    E.test(app.specialsSearch(40.6748, -73.9721))
  }

  // TODO: Do more detailed comparison (a la E.test) for multi responses

  @Test
  def multi() {
    val mult = app.multi(app.venueCategories, app.venueDetail(VENUE_ID), app.venueDetail(ANOTHER_VENUE_ID), app.venueTips(ANOTHER_VENUE_ID)).get
    println(mult)
  }

  @Test
  def multiList() {
    val mult = app.multi(List(app.venueDetail(VENUE_ID), app.venueDetail(ANOTHER_VENUE_ID))).get
    println(mult)
  }
}
