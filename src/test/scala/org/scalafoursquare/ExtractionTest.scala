package org.scalafoursquare

import net.liftweb.json.{JsonAST, Printer, Extraction, JsonParser}
import net.liftweb.json.JsonAST.{JValue, JObject}
import net.liftweb.json.JsonDSL._
import org.scalafoursquare.call.App
import org.scalafoursquare.response._
import org.specs.SpecsMatchers
import org.junit.{Ignore, Test}

class ExtractionTest extends SpecsMatchers {
  implicit val formats = APICustomSerializers.formats

  def testExtraction[T](jsonStr: String, checkMatch: Boolean = true)(implicit mf: Manifest[T]): T = {
    // println()
    // println(mf.erasure.getName)

    // println(jsonStr)
    val json = JsonParser.parse(jsonStr)
    // println(Printer.pretty(JsonAST.render(json)))
    val extracted = json.extract[T]
    val extJson = Extraction.decompose(extracted)

    val original = Printer.compact(JsonAST.render(json))
    val unparsed = Printer.compact(JsonAST.render(extJson))

    // println(original)
    // println(extracted)
    // println(unparsed)

    if (checkMatch && unparsed != original) {
      println("Original: " + Printer.pretty(JsonAST.render(json)))
      println("Extracted: " + Printer.pretty(JsonAST.render(extJson)))

      val cmp = TestUtil.JsonDiff.compare(json, extJson)
      cmp._1.map(j=>println("Same: " + Printer.pretty(JsonAST.render(j))))
      cmp._2.map(j=>println("Missed: " + Printer.pretty(JsonAST.render(j))))
      cmp._3.map(j=>println("Added: " + Printer.pretty(JsonAST.render(j))))

      unparsed == original must_== true
    }
    extracted
  }


  def C = Components

  @Test
  def userDetail() {
    testExtraction[UserDetailResponse](C.json(("user" -> C.userDetail1)))
    testExtraction[UserDetailResponse](C.json(("user" -> C.userDetail2)))
  }
  
  @Test
  def leaderboard() {
    testExtraction[LeaderboardResponse](C.json(("leaderboard" -> C.countList(2, List(C.leaderboardItem1, C.leaderboardItem2)))))
    testExtraction[LeaderboardResponse](C.json(("leaderboard" -> C.countList(0, Nil))))
  }

  @Test
  def userSearch() {
    testExtraction[UserSearchResponse](C.json(
      ("results" -> List(C.compactUser1, C.compactUser2)) ~
      ("unmatched" -> (("twitter" -> List("tw1", "tw2")) ~ ("facebook" -> List(1234, 2345))))))

    testExtraction[UserSearchResponse](C.json(
      ("results" -> List[JValue]()) ~
      ("unmatched" -> JObject(Nil))))
  }

  @Test
  def userRequest() {
    testExtraction[UserRequestResponse](C.json(("requests" -> List(C.compactUser1, C.compactUser2))))
    testExtraction[UserRequestResponse](C.json(("requests" -> List[JValue]())))
  }

  @Test
  def userBadges() {
    testExtraction[UserBadgesResponse](C.json(
      ("sets" -> C.badgeSet1) ~
      ("badges" -> C.badges1) ~
      ("defaultSetType" -> "aDefaultSetType")
    ))

    testExtraction[UserBadgesResponse](C.json(
      ("sets" -> C.badgeSet2) ~
      ("badges" -> C.badges2) ~
      ("defaultSetType" -> "")
    ))
  }

  @Test
  def userCheckins() {
    testExtraction[UserCheckinsResponse](C.json(("checkins" -> C.countList(2, List(C.checkinForFriend1, C.checkinForFriend2)))))
    testExtraction[UserCheckinsResponse](C.json(("checkins" -> C.countList(0, List[JValue]()))))
  }

  @Test
  def userFriends() {
    testExtraction[UserFriendsResponse](C.json(("friends" -> C.countList(2, List(C.compactUser1, C.compactUser2)))))
    testExtraction[UserFriendsResponse](C.json(("friends" -> C.countList(0, List[JValue]()))))
  }

  @Test
  def userMayorships() {
    testExtraction[UserMayorshipsResponse](C.json(("mayorships" -> C.countList(2, List(("venue" -> C.compactVenue1), ("venue" -> C.compactVenue2))))))
    testExtraction[UserMayorshipsResponse](C.json(("mayorships" -> C.countList(0, List[JValue]()))))
  }

  @Test
  def userTips() {
    testExtraction[UserTipsResponse](C.json(("tips" -> C.countList(2, List(C.tipForUser1, C.tipForUser2)))))
    testExtraction[UserTipsResponse](C.json(("tips" -> C.countList(0, List[JValue]()))))
  }

  @Test
  def userTodos() {
    testExtraction[UserTodosResponse](C.json(("todos" -> C.countList(2, List(C.todoForList1, C.todoForList2)))))
    testExtraction[UserTodosResponse](C.json(("todos" -> C.countList(0, List[JValue]()))))
  }

  @Test
  def userVenueHistory() {
    testExtraction[UserVenueHistoryResponse](C.json(("venues" -> C.countList(2, List(C.venueHistory1, C.venueHistory2)))))
    testExtraction[UserVenueHistoryResponse](C.json(("venues" -> C.countList(0, List[JValue]()))))
  }

  @Test
  def userFriendRequest() {
    testExtraction[UserFriendRequestResponse](C.json(("user" -> C.userDetail1)))
    testExtraction[UserFriendRequestResponse](C.json(("user" -> C.userDetail2)))
  }

  @Test
  def userUnfriend() {
    testExtraction[UserUnfriendResponse](C.json(("user" -> C.userDetail1)))
    testExtraction[UserUnfriendResponse](C.json(("user" -> C.userDetail2)))
  }

  @Test
  def userApproveRequest() {
    testExtraction[UserApproveFriendResponse](C.json(("user" -> C.userDetail1)))
    testExtraction[UserApproveFriendResponse](C.json(("user" -> C.userDetail2)))
  }

  @Test
  def userDenyRequest() {
    testExtraction[UserDenyFriendshipResponse](C.json(("user" -> C.userDetail1)))
    testExtraction[UserDenyFriendshipResponse](C.json(("user" -> C.userDetail2)))
  }

  @Test
  def userSetPings() {
    testExtraction[UserSetPingsResponse](C.json(("user" -> C.userDetail1)))
    testExtraction[UserSetPingsResponse](C.json(("user" -> C.userDetail2)))
  }

  @Test
  def userUpdate() {
    testExtraction[UserPhotoUpdateResponse](C.json(("user" -> C.userDetail1)))
    testExtraction[UserPhotoUpdateResponse](C.json(("user" -> C.userDetail2)))
  }

  @Test
  def venueDetail() {
    testExtraction[VenueDetailResponse](C.json(("venue" -> C.venueDetail1)))
    testExtraction[VenueDetailResponse](C.json(("venue" -> C.venueDetail2)))
  }

  @Test
  def venueAdd() {
    testExtraction[VenueAddResponse](C.json(("venue" -> C.venueDetail1)))
    testExtraction[VenueAddResponse](C.json(("venue" -> C.venueDetail2)))
  }

  @Test
  def venuesCategories() {
    testExtraction[VenueCategoriesResponse](C.json(("categories" -> List(C.categoryWithChildren1, C.categoryWithChildren2, C.categoryWithChildren3))))
    testExtraction[VenueCategoriesResponse](C.json(("categories" -> List[JValue]())))
  }

  @Test
  def venuesExplore() {
    testExtraction[VenueExploreResponse](C.json(
      ("keywords" -> C.countList(2, List(C.keyword1, C.keyword2))) ~
      ("warning" -> ("text" -> "a warning")) ~
      ("groups" -> List(C.exploreGroup1, C.exploreGroup2))
    ))

    testExtraction[VenueExploreResponse](C.json(
      ("keywords" -> C.countList(0, List[JValue]())) ~
      ("groups" -> List[JValue]())
    ))
  }

  @Test
  def venuesSearch() {
    testExtraction[VenueSearchResponse](C.json(("venues" -> List(C.compactVenue1, C.compactVenue2))))
    testExtraction[VenueSearchResponse](C.json(("venues" -> List[JValue]())))
  }

  @Test
  def venuesTrending() {
    testExtraction[VenueTrendingResponse](C.json(("venues" -> List(C.compactVenue1, C.compactVenue2))))
    testExtraction[VenueTrendingResponse](C.json(("venues" -> List[JValue]())))
  }

  @Test
  def venuesHereNow() {
    testExtraction[VenueHereNowResponse](C.json(("hereNow" -> C.countList(2, List(C.checkinForVenue1, C.checkinForVenue2)))))
    testExtraction[VenueHereNowResponse](C.json(("hereNow" -> C.countList(0, List[JValue]()))))
  }

  @Test
  def venuesTips() {
    testExtraction[VenueTipsResponse](C.json(("tips" -> C.countList(2, List(C.tipForVenue1, C.tipForVenue2)))))
    testExtraction[VenueTipsResponse](C.json(("tips" -> C.countList(0, List[JValue]()))))
  }

  @Test
  def venuesPhotos() {
    testExtraction[VenuePhotosResponse](C.json(("photos" -> C.countList(2, List(C.photoForList1, C.photoForList2)))))
    testExtraction[VenuePhotosResponse](C.json(("photos" -> C.countList(2, List(C.photoForVenueListWithCheckin1, C.photoForVenueListWithCheckin2)))))
    testExtraction[VenuePhotosResponse](C.json(("photos" -> C.countList(0, List[JValue]()))))
  }

  @Test
  def venuesLinks() {
    testExtraction[VenueLinksResponse](C.json(("links" -> C.countList(2, List(C.venueLink1, C.venueLink1)))))
    testExtraction[VenueLinksResponse](C.json(("links" -> C.countList(0, List[JValue]()))))
  }

  @Test
  def venueMarkTodo() {
    testExtraction[VenueMarkTodoResponse](C.json(("todo" -> C.todoForVenue1)))
    testExtraction[VenueMarkTodoResponse](C.json(("todo" -> C.todoForVenue2)))
  }

  @Test
  def venueFlag() {
    testExtraction[VenueFlagResponse](C.json(JObject(Nil)))
  }

  @Test
  def venueEdit() {
    testExtraction[VenueEditResponse](C.json(JObject(Nil)))
  }

  @Test
  def venueProposeEdit() {
    testExtraction[VenueProposeEditResponse](C.json(JObject(Nil)))
  }

  @Test
  def checkinDetail() {
    testExtraction[CheckinDetailResponse](C.json(("checkin" -> C.checkinDetail1)))
    testExtraction[CheckinDetailResponse](C.json(("checkin" -> C.checkinDetail2)))
  }

  @Test
  def checkinAdd() {
    testExtraction[AddCheckinResponse](C.json(("checkin" -> C.checkinForFriend1)))
    testExtraction[AddCheckinResponse](C.json(("checkin" -> C.checkinForFriend2)))
  }

  @Test
  def checkinsRecent() {
    testExtraction[RecentCheckinsResponse](C.json(("recent" -> List(C.checkinDetail1, C.checkinDetail2))))
    testExtraction[RecentCheckinsResponse](C.json(("recent" -> List[JValue]())))
  }

  @Test
  def addCheckinComment() {
    testExtraction[CheckinAddCommentResponse](C.json(("comment" -> C.commentsCore1)))
    testExtraction[CheckinAddCommentResponse](C.json(("comment" -> C.commentsCore2)))
  }

  @Test
  def deleteCheckinComment() {
    testExtraction[CheckinDeleteCommentResponse](C.json(("checkin" -> C.checkinDetail1)))
    testExtraction[CheckinDeleteCommentResponse](C.json(("checkin" -> C.checkinDetail2)))
  }

  @Test
  def tipDetail() {
    testExtraction[TipDetailResponse](C.json(("tip" -> C.tipDetail1)))
    testExtraction[TipDetailResponse](C.json(("tip" -> C.tipDetail2)))
  }

  @Test
  def addTip() {
    testExtraction[AddTipResponse](C.json(("tip" -> C.tipForVenue1)))
    testExtraction[AddTipResponse](C.json(("tip" -> C.tipForVenue2)))
  }

  @Test
  def tipSearch() {
    testExtraction[TipSearchResponse](C.json(("tips" -> List(C.tipForList1, C.tipForList2))))
    testExtraction[TipSearchResponse](C.json(("tips" -> List[JValue]())))
  }

  @Test
  def markTipTodo() {
    testExtraction[TipMarkTodoResponse](C.json(("todo" -> C.todoForList1)))
    testExtraction[TipMarkTodoResponse](C.json(("todo" -> C.todoForList2)))
  }

  @Test
  def markTipDone() {
    testExtraction[TipMarkDoneResponse](C.json(("tip" -> C.tipDetail1)))
    testExtraction[TipMarkDoneResponse](C.json(("tip" -> C.tipDetail2)))
  }

  @Test
  def unmarkTip() {
    testExtraction[TipUnmarkResponse](C.json(("tip" -> C.tipDetail1)))
    testExtraction[TipUnmarkResponse](C.json(("tip" -> C.tipDetail2)))
  }

  @Test
  def updateDetails() {
    testExtraction[UpdateDetailResponse](C.json(("notification" -> C.notificationForListUser1)))
    testExtraction[UpdateDetailResponse](C.json(("notification" -> C.notificationForListUser2)))

    testExtraction[UpdateDetailResponse](C.json(("notification" -> C.notificationForListCheckin)))

    testExtraction[UpdateDetailResponse](C.json(("notification" -> C.notificationForListVenue)))

    testExtraction[UpdateDetailResponse](C.json(("notification" -> C.notificationForListList1)))
    testExtraction[UpdateDetailResponse](C.json(("notification" -> C.notificationForListList2)))

    testExtraction[UpdateDetailResponse](C.json(("notification" -> C.notificationForListTip)))

    testExtraction[UpdateDetailResponse](C.json(("notification" -> C.notificationForListBadge)))

    testExtraction[UpdateDetailResponse](C.json(("notification" -> C.notificationForListSpecial)))

    testExtraction[UpdateDetailResponse](C.json(("notification" -> C.notificationForListUrl)))
  }


  @Test
  def notifications() {
    testExtraction[NotificationsResponse](C.json("notifications" -> C.countList(2, List(C.notificationForListUser1, C.notificationForListUser2))))
    testExtraction[NotificationsResponse](C.json("notifications" -> C.countList(0, List[JValue]())))
  }

  @Test
  def markNotificationsRead() {
    testExtraction[MarkNotificationsReadResponse](C.json(JObject(Nil)))
  }

  @Test
  def photoDetail() {
    testExtraction[PhotoDetailResponse](C.json(("photo" -> C.photoDetail1)))
    testExtraction[PhotoDetailResponse](C.json(("photo" -> C.photoDetail2)))
  }

  @Test
  def addPhoto() {
    testExtraction[AddPhotoResponse](C.json(("photo" -> C.photoCore1)))
    testExtraction[AddPhotoResponse](C.json(("photo" -> C.photoCore2)))
  }

  @Test
  def settingDetail() {
    testExtraction[SettingsDetailResponse](C.json(("value" -> true)))
    testExtraction[SettingsDetailResponse](C.json(("value" -> "yes")))
    testExtraction[SettingsDetailResponse](C.json(("value" -> null)))
    testExtraction[SettingsDetailResponse](C.json(("value" -> 1)))
    testExtraction[SettingsDetailResponse](C.json(("value" -> 2.5)))
  }

  @Test
  def allSettings() {
    testExtraction[AllSettingsResponse](C.json(("settings" -> C.allSettings1)))
    testExtraction[AllSettingsResponse](C.json(("settings" -> C.allSettings2)))
  }

  @Test
  def changeSettings() {
    testExtraction[ChangeSettingsResponse](C.json(("settings" -> C.allSettings1)))
    testExtraction[ChangeSettingsResponse](C.json(("settings" -> C.allSettings2)))
  }

  @Test
  def specialsDetail() {
    testExtraction[SpecialDetailResponse](C.json(("special" -> C.venueSpecial1)))
    testExtraction[SpecialDetailResponse](C.json(("special" -> C.venueSpecial2)))
  }

  @Test
  def specialsSearch() {
    testExtraction[SpecialsSearchResponse](C.json(("specials" -> C.countList(2, List[JValue](C.venueSpecial1, C.venueSpecial2)))))
    testExtraction[SpecialsSearchResponse](C.json(("specials" -> C.countList(0, List[JValue]()))))
  }

  @Test
  def flagSpecial() {
    testExtraction[FlagSpecialResponse](C.json(JObject(Nil)))
  }

  @Test
  def responseNotifications() {

    def N(t: String, i: JValue) = C.json(("type" -> t) ~ ("item" -> i))

    testExtraction[NotificationItem](N("badge", C.notifyBadge1))
    testExtraction[NotificationItem](N("badge", C.notifyBadge2))

    testExtraction[NotificationItem](N("tip", C.notifyTip1))
    testExtraction[NotificationItem](N("tip", C.notifyTip2))

    testExtraction[NotificationItem](N("tipAlert", C.notifyTipAlert1))
    testExtraction[NotificationItem](N("tipAlert", C.notifyTipAlert2))

    testExtraction[NotificationItem](N("leaderboard", C.notifyLeaderboard1))
    testExtraction[NotificationItem](N("leaderboard", C.notifyLeaderboard2))

    testExtraction[NotificationItem](N("mayorship", C.notifyMayor1))
    testExtraction[NotificationItem](N("mayorship", C.notifyMayor2))

    testExtraction[NotificationItem](N("specials", C.notifySpecial1))
    testExtraction[NotificationItem](N("specials", C.notifySpecial2))

    testExtraction[NotificationItem](N("message", C.notifyMessage1))
    testExtraction[NotificationItem](N("message", C.notifyMessage2))

    testExtraction[NotificationItem](N("score", C.notifyScore1))
    testExtraction[NotificationItem](N("score", C.notifyScore2))

    testExtraction[NotificationItem](N("notificationTray", C.notifyTray1))
    testExtraction[NotificationItem](N("notificationTray", C.notifyTray2))
  }

  @Test
  def multi() {
    val oneResponse = ("meta" -> ("code" -> 200)) ~ ("response" -> ("value" -> "yes"))
    val json = ("responses" -> List(oneResponse, oneResponse, oneResponse))

    val res = App.extractMultiResponse[SettingsDetailResponse, SettingsDetailResponse, SettingsDetailResponse, Nothing, Nothing](json)

    res._1.isDefined must_== true
    res._2.isDefined must_== true
    res._3.isDefined must_== true
    res._4.isDefined must_== false
    res._5.isDefined must_== false

    val resList = App.extractMultiListResponse[SettingsDetailResponse](json)
    resList.isDefined must_== true
    resList.get.length must_== 3
  }

  @Test
  def venueGroupDetail() {
    testExtraction[VenueGroupDetailResponse](C.json(("venueGroup" -> C.venueGroupDetail1)))
    testExtraction[VenueGroupDetailResponse](C.json(("venueGroup" -> C.venueGroupDetail2)))
  }

  @Test
  def listVenueGroups() {
    testExtraction[ListVenueGroupResponse](C.json(("venueGroups" -> C.venueGroupList1)))
    testExtraction[ListVenueGroupResponse](C.json(("venueGroups" -> C.venueGroupList2)))
  }

  @Test
  def addVenueToGroup() {
    testExtraction[AddVenueToGroupResponse](C.json(JObject(Nil)))
  }

  @Test
  def removeVenueFromGroup() {
    testExtraction[RemoveVenueFromGroupResponse](C.json(JObject(Nil)))
  }

  @Test
  def deleteVenueGroup() {
    testExtraction[DeleteVenueGroupResponse](C.json(JObject(Nil)))
  }

  @Test
  def addVenueGroup() {
    testExtraction[AddVenueGroupResponse](C.json(("venueGroup" -> C.venueGroupDetail1)))
    testExtraction[AddVenueGroupResponse](C.json(("venueGroup" -> C.venueGroupDetail2)))
  }

  @Test
  def campaignDetail() {
    testExtraction[CampaignDetailResponse](C.json(("campaign" -> C.campaignDetail1)))
    testExtraction[CampaignDetailResponse](C.json(("campaign" -> C.campaignDetail2)))
  }

  @Test
  def listCampaigns() {
    testExtraction[ListCampaignResponse](C.json(("campaigns" -> C.campaignList1)))
    testExtraction[ListCampaignResponse](C.json(("campaigns" -> C.campaignList2)))
  }


  @Test
  def addCampaign() {
    testExtraction[AddCampaignResponse](C.json(("campaign" -> C.campaignDetail1)))
    testExtraction[AddCampaignResponse](C.json(("campaign" -> C.campaignDetail2)))
  }

  @Test
  def deleteCampaign() {
    testExtraction[DeleteCampaignResponse](C.json(JObject(Nil)))
  }

  @Test
  def startCampaign() {
    testExtraction[StartCampaignResponse](C.json(JObject(Nil)))
  }

  @Test
  def endCampaign() {
    testExtraction[EndCampaignResponse](C.json(JObject(Nil)))
  }


  @Test
  def listSpecials() {
    testExtraction[ListSpecialResponse](C.json(("specials" -> C.specialsList1)))
    testExtraction[ListSpecialResponse](C.json(("specials" -> C.specialsList2)))
  }

  @Test
  def specialConfigurationDetail() {
    testExtraction[SpecialConfigurationDetailResponse](C.json(("special" -> C.specialDetail1)))
    testExtraction[SpecialConfigurationDetailResponse](C.json(("special" -> C.specialDetail2)))
  }

  @Test
  def addSpecial() {
    testExtraction[AddSpecialResponse](C.json(("special" -> C.specialDetail1)))
    testExtraction[AddSpecialResponse](C.json(("special" -> C.specialDetail2)))
  }


  @Test
  def managedVenues() {

  }

  @Test
  def venuesTimeSeries() {

  }

  @Test
  def venueStats() {

  }


  @Test
  def campaignTimeSeries() {

  }


  @Test
  def retireSpecial() {

  }
}
