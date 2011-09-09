package org.scalafoursquare

import org.scalafoursquare.call.{AuthApp}
import org.specs.SpecsMatchers
import org.junit.{Test, Ignore}
import net.liftweb.util.Props
import net.liftweb.util.Helpers._

class MerchantEndpointTest extends SpecsMatchers {

  val P = TestUtil.propParams
  val E = EndpointTest

  val caller = TestUtil.httpCaller
  val app = new AuthApp(caller, P.USER_TOKEN)

  def IGNORE = "IGNORE"
  def VENUE_ID = Props.get("test.merch.venue1.id").openOr(IGNORE)
  def ANOTHER_VENUE_ID = Props.get("test.merch.venue2.id").openOr(IGNORE)
  def VENUE_GROUP_ID = Props.get("test.merch.venueGroup.id").openOr(IGNORE)
  def CAMPAIGN_ID = Props.get("test.merch.campaign.id").openOr(IGNORE)
  def SPECIAL_ID = Props.get("test.merch.special.id").openOr(IGNORE)
  def SPECIAL_START = Props.get("test.merch.special.start").flatMap(v=>tryo(v.toLong)).openOr(-1L)
  def SPECIAL_END = Props.get("test.merch.special.end").flatMap(v=>tryo(v.toLong)).openOr(-1L)

  @Test
  def listCampaigns() {
    E.test(app.listCampaigns())
  }

  @Test
  def campaignTimeSeries() {
    if (CAMPAIGN_ID != IGNORE && SPECIAL_START > 0L && SPECIAL_END > 0L)
      E.test(app.campaignTimeSeries(CAMPAIGN_ID, startAt=Some(SPECIAL_START), endAt=Some(SPECIAL_END)))
  }

  @Test
  def campaignDetails() {
    if (CAMPAIGN_ID != IGNORE)
      E.test(app.campaignDetails(CAMPAIGN_ID))
  }

  @Test
  def listSpecials() {
    E.test(app.listSpecials())
  }

  @Test
  def specialConfigurationDetail() {
    if (SPECIAL_ID != IGNORE)
      E.test(app.specialConfigurationDetail(SPECIAL_ID))
  }

  @Test
  def listVenueGroups() {
    E.test(app.listVenueGroups())
  }

  @Test
  def venueGroupDetails() {
    if (VENUE_GROUP_ID != IGNORE)
      E.test(app.venueGroupDetails(VENUE_GROUP_ID))
  }

  @Test
  def managedVenues() {
    E.test(app.managedVenues())
  }

  @Test
  def venueTimeSeries() {
    if (VENUE_ID != IGNORE && ANOTHER_VENUE_ID != IGNORE && SPECIAL_START > 0)
      E.test(app.venuesTimeSeries(List(VENUE_ID, ANOTHER_VENUE_ID), SPECIAL_START))
  }

  @Test
  def venueStats() {
    if (ANOTHER_VENUE_ID != IGNORE)
      E.test(app.venueStats(ANOTHER_VENUE_ID))
  }
}
