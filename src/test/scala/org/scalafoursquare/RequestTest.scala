package org.scalafoursquare

import org.scalafoursquare.call.{AuthApp}
import org.specs.SpecsMatchers
import org.junit.Test

class RequestTest extends SpecsMatchers {
  @Test
  def testRequests() {

    val app = new AuthApp(TestCaller, "A Fake Token")

    // USERLESS

    val vcat = app.venueCategories
    val vdet = app.venueDetail("id")
    val tdet = app.tipDetail("id")
    val sdet = app.specialDetail("id", "venueId")

    val vhn = app.venueHereNow("id", Some(10), Some(10), Some(1000L))
    val vhn2 = app.venueHereNow("id")
    val vtips = app.venueTips("id", Some("recent"), Some(10), Some(10))
    val vtips2 = app.venueTips("id")
    val vpho = app.venuePhotos("id", "venue", Some(10), Some(10))
    val vpho2 = app.venuePhotos("id", "venue")
    val vlnk = app.venueLinks("id")

    val venexp = app.exploreVenues(10.0, 30.0, Some(1000.0), Some(100.0), Some(50.0), Some(2000), Some("shops"),
      Some("search for this venue"), Some(10), Some("specials"), Some("new"))
    val venexp2 = app.exploreVenues(10.0, 30.0)

    val vsearch = app.venueSearch(10.0, 20.0, Some(100.0), Some(50.0), Some(100.0), Some("query for this venue"),
      Some(10), Some("checkin"), Some("catId"), Some("url"), Some("providerId"), Some("linkedId"))
    val vsearch2 = app.venueSearch(10.0, 20.0)

    val vtrend = app.venueTrending(10.0, 20.0, Some(10), Some(1000))
    val vtrend2 = app.venueTrending(10.0, 20.0)

    val tipsearch = app.tipsSearch(10.0, 20.0, Some(10), Some(10), Some("friends"), Some("find me a tip"))
    val tipsearch2 = app.tipsSearch(10.0, 20.0)

    val spsearch = app.specialsSearch(10.0, 20.0, Some(10.0), Some(20.0), Some(30.0), Some(10))
    val spsearch2 = app.specialsSearch(10.0, 20.0)

    val multi1 = app.multi(vhn)
    val multi2 = app.multi(vtips, venexp)
    val multi3 = app.multi(vsearch, sdet, spsearch)
    val multi4 = app.multi(vcat, vdet, vhn, vpho)
    val multi5 = app.multi(vtrend, vpho, tipsearch, vcat, sdet)
    val multiL1 = app.multi(vlnk, app.venueLinks("id2"), app.venueLinks("id3"))

    // AUTHENTICATED

    // GET
    val selfDet = app.self
    val udet = app.userDetail("id")

    val updet = app.updateDetail("id")
    val phodet = app.photoDetail("id")
    val setdet = app.settingsDetail("id")
    val chdet = app.checkinDetail("id", Some("signature"))
    val chdet2 = app.checkinDetail("id")

    val leader = app.leaderboard(Some(2))
    val leader2 = app.leaderboard()

    val usearch = app.userSearch(Some(List("867-5309", "555-5555")), Some(List("fake@email.com", "another@fake.com")),
      Some(List("twitter1", "twitter2")), Some("twitterSource"), Some(List("fbid1", "fbid2")), Some("search for this name"))
    val usearch2 = app.userSearch() // TODO: probably need to programmatically require some arguments

    val ureqs = app.userRequests

    val chrec = app.recentCheckins(Some(10.0, 20.0), Some(10), Some(5000L))
    val chrec2 = app.recentCheckins()

    val notes = app.notifications(Some(10), Some(20))
    val notes2 = app.notifications()

    val setall = app.allSettings

    val selfbadges = app.selfBadges
    val ubadges = app.userBadges("id")

    val chself = app.selfCheckins(Some(10), Some(20), Some(1000L), Some(2000L))
    val chself2 = app.selfCheckins()

    val selffriends = app.selfFriends(Some(10), Some(20))
    val selffriends2 = app.selfFriends()

    val ufriends = app.userFriends("id", Some(10), Some(20))
    val ufriends2 = app.userFriends("id")

    val selfmayor = app.selfMayorships
    val umayor = app.userMayorships("id")

    val selftips = app.selfTips(Some("recent"), Some((10.0, 20.0)), Some(10), Some(20))
    val selftips2 = app.selfTips()

    val utips = app.userTips("id", Some("recent"), Some((10.0, 20.0)), Some(10), Some(20))
    val utips2 = app.userTips("id", Some("recent"))

    val selftodos = app.selfTodos(Some("nearby"), Some((10.0, 20.0)))
    val selftodos2 = app.selfTodos()

    val utodos = app.userTodos("id", Some("nearby"), Some((10.0, 20.0)))
    val utodos2 = app.userTodos("id")

    val selfvh = app.selfVenueHistory(Some(1000L), Some(2000L), Some("categoryId"))
    val selfvh2 = app.selfVenueHistory()

    // POST
    val venadd = app.addVenue("venue", 10.0, -20.0, Some("55 5th Street"), Some("5th Avenue"), Some("city"),
      Some("state"), Some("58701"), Some("867-5309"), Some("twitter1"), Some("catId"))
    val venadd2 = app.addVenue("venue", 10.0, -20.0)

    val chadd = app.addCheckin(Some("venueId"), Some("venue"), Some("shout"), Some(List("twitter", "facebook")),
      Some((10.0, 20.0)), Some(10.0), Some(20.0), Some(30.0))
    val chadd2 = app.addCheckin() // TODO: probably need to programatically require some arguments

    val tipadd = app.addTip("venueId", "this is a tip", Some("url"), Some(List("twitter", "facebook")))
    val tipadd2 = app.addTip("venueId", "this is a tip")

    val phoadd = app.addPhoto(TestPhotoData, Some("checkinId"), Some("tipId"), Some("venueId"), Some(List("facebook", "twitter")),
      Some(true), Some((10.0, 20.0)), Some(30.0), Some(40.0), Some(50.0))
    val phoadd2 = app.addPhoto(TestPhotoData) // TODO: programatically require some parameters

    val freq = app.friendRequest("fid")
    val funfn = app.unfriend("fid")
    val fappr = app.approveFriendship("fid")
    val fdeny = app.denyFriendship("fid")
    val spings = app.setPings("fid", true)

    val setchg = app.changeSetting("sendToTwitter", false)

    val uppho = app.updateSelf(TestPhotoData) // TODO: is this all we can do here?

    val vtodo = app.markVenueTodo("id", Some("should do this"))
    val vtodo2 = app.markVenueTodo("id")

    val vflag = app.flagVenue("id", "mislocated", Some("vid"))
    val vflag2 = app.flagVenue("id", "mislocated")

    val vedit = app.editVenue("id", Some("name"), Some("11 1st Street"), Some("1st Avenue"), Some("city"), Some("state"),
      Some("58701"), Some("867-5309"), Some((10.0, 20.0)), Some(List("categoryId1", "categoryId2")))
    val vedit2 = app.editVenue("id")

    val vpedit = app.proposeEditVenue("id", Some("name"), Some("11 1st Street"), Some("1st Avenue"), Some("city"), Some("state"),
      Some("58701"), Some("867-5309"), Some((10.0, 20.0)), Some("primaryCategoryId"))
    val vpedit2 = app.proposeEditVenue("id")

    /*
    val vlstd = app.venueListed("id")
    val vlstdg = app.venueListedGroup("id", "group")

    val tlstd = app.tipListed("id")
    val tlstdg = app.tipListedGroup("id", "group")

    val slflstd = app.selfLists(10.0, 20.0)
    val ulstd = app.userLists("id", 10.0, 20.0)

    val slflstdg = app.selfListGroup("other", 10.0, 20.0)
    val ulstdg = app.userListGroup("id", "other", 10.0, 20.0)

    val addlst = app.addList("name", Some("desc"), Some(true), Some("photo"))
    val addlst2 = app.addList("name")

    val addlsti = app.addListItem("id", Some("venueId"), Some("text"), Some("url"), Some("tip"), Some("list"), Some("item"))
    val addlsti2 = app.addListItem("id")
    */

    val chcomment = app.addCheckinComment("id", "check it out")
    val chdelcomm = app.deleteCheckinComment("id", "commId")

    val tiptodo = app.markTipTodo("id")
    val tipdone = app.markTipDone("id")
    val tipundo = app.unmarkTip("id")

    val marknote = app.markNotificationsRead(1000L)

    val spflag = app.flagSpecial("id", "vid", "not_redeemable", Some("wtf special"))
    val spflag2 = app.flagSpecial("id", "vid", "not_redeemable")

    // MERCHANT GET

    val vgdet = app.venueGroupDetails("id")
    val cmpdet = app.campaignDetails("id")

    val lscmp = app.listCampaigns(Some("id"), Some("gid"), Some("pending"))
    val lscmp2 = app.listCampaigns()

    val lssps = app.listSpecials(Some(List("vid1", "vid2")), Some("active"))
    val lssps2 = app.listSpecials()

    val lsvg = app.listVenueGroups()
    val manven = app.managedVenues()

    val vents = app.venuesTimeSeries(List("vid1", "vid2"), 1000L, Some(2000L))
    val vents2 = app.venuesTimeSeries(List("vid1", "vid2"), 1000L)

    val venstats = app.venueStats("id", Some(1000L), Some(2000L))
    val venstats2 = app.venueStats("id")

    val spcnf = app.specialConfigurationDetail("id")

    val cmpts = app.campaignTimeSeries("id", Some(1000L), Some(2000L))
    val cmpts2 = app.campaignTimeSeries("id")

    // MERCHANT POST
    val addcmp = app.addCampaign("id", Some(List("gid1", "gid2")), Some(List("vid1", "vid2")), Some(1000L), Some(2000L))
    val addcmp2 = app.addCampaign("id")

    val addsp = app.addSpecial("mayor", "text", "unlockText", Some("finePrint"), Some(1), Some(2), Some(3))
    val addsp2 = app.addSpecial("mayor", "text", "unlockText")

    val addvg = app.addVenueGroup("name")
    val delvg = app.deleteVenueGroup("id")

    val strcmp = app.startCampaign("id")
    val endcmp = app.endCampaign("id")
    val delcmp = app.deleteCampaign("id")
    val retsp = app.retireSpecial("id")
    val avtg = app.addVenueToGroup("id", List("vid1", "vid2"))
    val rmvfg = app.removeVenueFromGroup("id", List("vid1", "vid2"))
  }
}