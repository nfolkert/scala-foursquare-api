package org.scalafoursquare

import org.scalafoursquare.call.{UserlessApp}
import org.specs.SpecsMatchers
import org.junit.{Test, Ignore}

class UserlessEndpointTest extends SpecsMatchers {

  val P = TestUtil.propParams

  val caller = TestUtil.httpCaller
  val app = new UserlessApp(caller)

  // MAY NEED TO MODIFY THESE IF TIPS/SPECIALS/VENUES HAVE CHANGED!
  def VENUE_ID = "49d22274f964a5209a5b1fe3"
  def TIP_ID = "4e5d72bbbd41bd3bc3a94bda"
  def SPECIAL_ID = "4e5d778dbd41022d87273eac"

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
}
