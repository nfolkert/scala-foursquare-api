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
    val vtips = app.venueTips("id", Some("recent"), Some(10), Some(10))
    val vpho = app.venuePhotos("id", "venue", Some(10), Some(10))
    val vlnk = app.venueLinks("id")

    val venexp = app.exploreVenues(10.0, 30.0, Some(1000.0), Some(100.0), Some(50.0), Some(2000), Some("shops"),
      Some("search for this venue"), Some(10), Some("specials"))

    val vsearch = app.venueSearch(10.0, 20.0, Some(100.0), Some(50.0), Some(100.0), Some("query for this venue"),
      Some(10), Some("checkin"), Some("catId"), Some("url"), Some("providerId"), Some("linkedId"))

    val vtrend = app.venueTrending(10.0, 20.0, Some(10), Some(1000))

    val tipsearch = app.tipsSearch(10.0, 20.0, Some(10), Some(10), Some("friends"), Some("find me a tip"))

    val spsearch = app.specialsSearch(10.0, 20.0, Some(10.0), Some(20.0), Some(30.0), Some(10))

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

    val leader = app.leaderboard(Some(2))
    val usearch = app.userSearch(Some(List("867-5309", "555-5555")), Some(List("fake@email.com", "another@fake.com")),
      Some(List("twitter1", "twitter2")), Some("twitterSource"), Some(List("fbid1", "fbid2")), Some("search for this name"))
    val ureqs = app.userRequests

    val chrec = app.recentCheckins(Some(10.0, 20.0), Some(10), Some(5000L))

    val notes = app.notifications(Some(10), Some(20))

    val setall = app.allSettings

    val selfbadges = app.selfBadges
    val ubadges = app.userBadges("id")

    val chself = app.selfCheckins(Some(10), Some(20), Some(1000L), Some(2000L))

    val selffriends = app.selfFriends(Some(10), Some(20))
    val ufriends = app.userFriends("id", Some(10), Some(20))

    val selfmayor = app.selfMayorships
    val umayor = app.userMayorships("id")

    val selftips = app.selfTips(Some("recent"), Some((10.0, 20.0)), Some(10), Some(20))
    val utips = app.userTips("id", Some("recent"), Some((10.0, 20.0)), Some(10), Some(20))

    val selftodos = app.selfTodos(Some("nearby"), Some((10.0, 20.0)))
    val utodos = app.userTodos("id", Some("nearby"), Some((10.0, 20.0)))

    val selfvh = app.selfVenueHistory(Some(1000L), Some(2000L), Some("categoryId"))


    // POST
    val venadd = app.addVenue("venue", 10.0, -20.0, Some("55 5th Street"), Some("5th Avenue"), Some("city"),
      Some("state"), Some("58701"), Some("867-5309"), Some("twitter1"), Some("catId"))

    val chadd = app.addCheckin(Some("venueId"), Some("venue"), Some("shout"), Some(List("twitter", "facebook")),
      Some((10.0, 20.0)), Some(10.0), Some(20.0), Some(30.0))

    val tipadd = app.addTip("venueId", "this is a tip", Some("url"), Some(List("twitter", "facebook")))

    val phoadd = app.addPhoto(TestPhotoData, Some("checkinId"), Some("tipId"), Some("venueId"), Some(List("facebook", "twitter")),
      Some(true), Some((10.0, 20.0)), Some(30.0), Some(40.0), Some(50.0))

    val freq = app.friendRequest("fid")
    val funfn = app.unfriend("fid")
    val fappr = app.approveFriendship("fid")
    val fdeny = app.denyFriendship("fid")
    val spings = app.setPings("fid", true)

    val setchg = app.changeSetting("sendToTwitter", false)

    val uppho = app.updatePhoto(TestPhotoData)

    val vtodo = app.markVenueTodo("id", Some("should do this"))

    val vflag = app.flagVenue("id", "mislocated", Some("vid"))

    val vedit = app.editVenue("id", Some("name"), Some("11 1st Street"), Some("1st Avenue"), Some("city"), Some("state"),
      Some("58701"), Some("867-5309"), Some((10.0, 20.0)), Some(List("categoryId1", "categoryId2")))

    val vpedit = app.proposeEditVenue("id", Some("name"), Some("11 1st Street"), Some("1st Avenue"), Some("city"), Some("state"),
      Some("58701"), Some("867-5309"), Some((10.0, 20.0)), Some("primaryCategoryId"))

    val chcomment = app.addCheckinComment("id", "check it out")
    val chdelcomm = app.deleteCheckinComment("id", "commId")

    val tiptodo = app.markTipTodo("id")
    val tipdone = app.markTipDone("id")
    val tipundo = app.unmarkTip("id")

    val marknote = app.markNotificationsRead(1000L)

    val spflag = app.flagSpecial("id", "vid", "not_redeemable", Some("wtf special"))
  }
}