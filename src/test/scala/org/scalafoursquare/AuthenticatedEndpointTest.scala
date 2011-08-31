package org.scalafoursquare

import org.scalafoursquare.call.{AuthApp}
import org.specs.SpecsMatchers
import org.junit.{Test, Ignore}

class AuthenticatedEndpointTest extends SpecsMatchers {

  val P = TestUtil.propParams

  val caller = TestUtil.httpCaller
  val app = new AuthApp(caller, P.USER_TOKEN)

  // MAY NEED TO MODIFY THESE IF TIPS/SPECIALS/VENUES HAVE CHANGED!
  def VENUE_ID = "49d22274f964a5209a5b1fe3"
  def ANOTHER_VENUE_ID = "4a468cd9f964a52015a91fe3"
  def TIP_ID = "4e5d72bbbd41bd3bc3a94bda"
  def SPECIAL_ID = "4e5d778dbd41022d87273eac"

  def USER_ID = "32"

  @Test
  def self() {
    val u = app.self.get
    println(u)
  }

  @Test
  def userDetail() {
    val u = app.userDetail(USER_ID).get
    println(u)
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
    val board = app.leaderboard().get
    println(board)
  }

  @Test
  def userSearch() {
    val us = app.userSearch(name=Some("Nate")).get
    println(us)
  }

  @Test
  def userRequests() {
    val reqs = app.userRequests.get
    println(reqs)
  }

  @Test
  def recentCheckins() {
    val chs = app.recentCheckins().get
    println(chs)
  }

  @Test
  def notifications() {
    val ns = app.notifications().get
    println(ns)
  }

  @Test
  def allSettings() {
    val as = app.allSettings.get
    println(as)
  }

  @Test
  def selfBadges() {
    val bs = app.selfBadges.get
    println(bs)
  }

  @Test
  def userBadges() {
    val bs = app.userBadges(USER_ID).get
    println(bs)
  }

  @Test
  def selfCheckins() {
    val chs = app.selfCheckins().get
    println(chs)
  }

  @Test
  def selfFriends() {
    val fs = app.selfFriends().get
    println(fs)
  }

  @Test
  def userFriends() {
    val fs = app.userFriends(USER_ID).get
    println(fs)
  }

  @Test
  def selfMayorships() {
    val ms = app.selfMayorships.get
    println(ms)
  }

  @Test
  def userMayorships() {
    val ms = app.userMayorships(USER_ID).get
    println(ms)
  }

  @Test
  def selfTips() {
    val ts = app.selfTips().get
    println(ts)
  }

  @Test
  def userTips() {
    val ts = app.userTips(USER_ID).get
    println(ts)
  }

  @Test
  def selfTodos() {
    val ts = app.selfTodos().get
    println(ts)
  }

  @Test
  def userTodos() {
    val ts = app.userTodos(USER_ID).get
    println(ts)
  }

  @Test
  def selfVenueHistory() {
    val vh = app.selfVenueHistory().get
    println(vh)
  }

  // USERLESS ENDPOINTS WITH THE AUTHENTICATED APP

  @Test
  def venueCategories() {
    val cats = app.venueCategories.get
    println(cats)
  }

  @Test
  def venueDetail() {
    val detail = app.venueDetail(VENUE_ID).get
    println(detail)
  }

  @Test
  def tipDetail() {
    val detail = app.tipDetail(TIP_ID).get
    println(detail)
  }

  @Test
  def specialDetail() {
    val detail = app.specialDetail(SPECIAL_ID, VENUE_ID).get
    println(detail)
  }

  @Test
  def venueHereNow() {
    val hereNow = app.venueHereNow(VENUE_ID).get
    println(hereNow)
  }

  @Test
  def venueTips() {
    val tips = app.venueTips(VENUE_ID).get
    println(tips)
  }

  @Test
  def venuePhotos() {
    val photos = app.venuePhotos(VENUE_ID, "venue").get
    println(photos)
  }

  @Test
  def venueLinks() {
    val links = app.venueLinks(VENUE_ID).get
    println(links)
  }

  @Test
  def exploreVenues() {
    val explore = app.exploreVenues(40.6748, -73.9721).get
    println(explore)
  }

  @Test
  def venueSearch() {
    val search = app.venueSearch(40.6748, -73.9721).get
    println(search)
  }

  @Test
  def venueTrending() {
    val trending = app.venueTrending(40.6748, -73.9721).get
    println(trending)
  }

  @Test
  def tipsSearch() {
    val tips = app.tipsSearch(40.6748, -73.9721).get
    println(tips)
  }

  @Test
  def specialsSearch() {
    val specials = app.specialsSearch(40.6748, -73.9721).get
    println(specials)
  }

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
