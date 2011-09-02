package org.scalafoursquare

import org.scalafoursquare.call.{AuthApp}
import org.specs.SpecsMatchers
import org.junit.{Test, Ignore}

class MerchantEndpointTest extends SpecsMatchers {

  val P = TestUtil.propParams
  val E = EndpointTest

  val caller = TestUtil.httpCaller
  val app = new AuthApp(caller, P.USER_TOKEN)

  // MAY NEED TO MODIFY THESE IF TIPS/SPECIALS/VENUES HAVE CHANGED!  TODO: MAY WANT TO SET IN PROPERTIES TO ENSURE ACCESSIBILITY?
  def VENUE_ID = "42e82000f964a52085261fe3"
  def ANOTHER_VENUE_ID = "4a468cd9f964a52015a91fe3"
  def VENUE_GROUP_ID = "4e6070dfbd41125ddcd331b0"
  def CAMPAIGN_ID = "4e607126bd41125ddcd331d5"
  def SPECIAL_ID = "4e607138bd41125ddcd331e1"

  @Test
  def listCampaigns() {
    E.test(app.listCampaigns())
  }

  @Test
  def campaignTimeSeries() {
    E.test(app.campaignTimeSeries(CAMPAIGN_ID, startAt=Some(1314944938L), endAt=Some(1314945118L)))
  }

  @Test
  def campaignDetails() {
    E.test(app.campaignDetails(CAMPAIGN_ID))
  }

  @Test
  def listSpecials() {
    E.test(app.listSpecials())
  }

  @Test
  def specialConfigurationDetail() {
    E.test(app.specialConfigurationDetail(SPECIAL_ID))
  }

  @Test
  def listVenueGroups() {
    E.test(app.listVenueGroups())
  }

  @Test
  def venueGroupDetails() {
    E.test(app.venueGroupDetails(VENUE_GROUP_ID))
  }

  @Test
  def managedVenues() {
    E.test(app.managedVenues())
  }

  @Test
  def venueTimeSeries() {
    E.test(app.venuesTimeSeries(List(VENUE_ID, ANOTHER_VENUE_ID), 1309761118L))
  }

  @Test
  def venueStats() {
    E.test(app.venueStats(ANOTHER_VENUE_ID))
  }
}
