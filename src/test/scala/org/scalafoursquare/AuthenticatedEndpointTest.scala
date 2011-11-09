package org.scalafoursquare

import net.liftweb.util.Props
import org.scalafoursquare.call.{AuthApp}
import org.specs.SpecsMatchers
import org.junit.{Test, Ignore}

class AuthenticatedEndpointTest extends SpecsMatchers {

  val P = TestUtil.propParams

  val E = EndpointTest

  val caller = TestUtil.httpCaller
  val app = new AuthApp(caller, P.USER_TOKEN)

  def IGNORE = "IGNORE"
  def VENUE_ID = Props.get("test.venue1.id").openOr(IGNORE)
  def ANOTHER_VENUE_ID = Props.get("test.venue2.id").openOr(IGNORE)
  def TIP_ID = Props.get("test.tip.id").openOr(IGNORE)
  def SPECIAL_ID = Props.get("test.special.id").openOr(IGNORE)
  def USER_ID = Props.get("test.user.id").openOr(IGNORE)
  def SELF_ID = Props.get("test.self.id").openOr(IGNORE)

  @Test
  def self() {
    E.test(app.self)
  }

  @Test
  def userDetail() {
    if (USER_ID != IGNORE)
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
    if (USER_ID != IGNORE)
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
    if (USER_ID != IGNORE)
      E.test(app.userFriends(USER_ID))
  }

  @Test
  def selfMayorships() {
    E.test(app.selfMayorships)
  }

  @Test
  def userMayorships() {
    if (USER_ID != IGNORE)
      E.test(app.userMayorships(USER_ID))
  }

  @Test
  def selfTips() {
    E.test(app.selfTips())
  }

  @Test
  def userTips() {
    if (USER_ID != IGNORE)
      E.test(app.userTips(USER_ID))
  }

  @Test
  def selfTodos() {
    E.test(app.selfTodos(sort=Some("recent")))
  }

  @Test
  def userTodos() {
    if (SELF_ID != IGNORE)
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
    if (VENUE_ID != IGNORE)
      E.test(app.venueDetail(VENUE_ID))
  }

  @Test
  def tipDetail() {
    if (TIP_ID != IGNORE)
      E.test(app.tipDetail(TIP_ID))
  }

  @Test
  def specialDetail() {
    if (SPECIAL_ID != IGNORE && VENUE_ID != IGNORE)
      E.test(app.specialDetail(SPECIAL_ID, VENUE_ID))
  }

  @Test
  def venueHereNow() {
    if (VENUE_ID != IGNORE)
      E.test(app.venueHereNow(VENUE_ID))
  }

  @Test
  def venueTips() {
    if (VENUE_ID != IGNORE)
      E.test(app.venueTips(VENUE_ID))
  }

  @Test
  def venuePhotos() {
    if (VENUE_ID != IGNORE)
      E.test(app.venuePhotos(VENUE_ID, "venue"))
  }

  @Test
  def venueLinks() {
    if (VENUE_ID != IGNORE)
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

  /*
  TODO: LISTS
  @Test
  def selfLists() {
    EndpointTest.printDebug = true
    println("LISTS!!!!!!!!!")
    E.test(app.selfLists(40.6748, -73.9721))
    EndpointTest.printDebug = false
  }

  @Test
  def userLists() {
    EndpointTest.printDebug = true
    println("LISTS!!!!!!!!!")
    E.test(app.userLists(USER_ID, 40.6748, -73.9721))
    EndpointTest.printDebug = false
  }
  */

  // TODO: Do more detailed comparison (a la E.test) for multi responses

  @Test
  def multi() {
    if (VENUE_ID != IGNORE && ANOTHER_VENUE_ID != IGNORE) {
      val mult = app.multi(app.venueCategories, app.venueDetail(VENUE_ID), app.venueDetail(ANOTHER_VENUE_ID), app.venueTips(ANOTHER_VENUE_ID)).get
      println(mult)
    }
  }

  @Test
  def multiList() {
    if (VENUE_ID != IGNORE && ANOTHER_VENUE_ID != IGNORE) {
      val mult = app.multi(List(app.venueDetail(VENUE_ID), app.venueDetail(ANOTHER_VENUE_ID))).get
      println(mult)
    }
  }
}
