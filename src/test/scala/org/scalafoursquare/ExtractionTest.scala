package org.scalafoursquare

import net.liftweb.json.{JsonAST, Printer, Extraction, JsonParser}
import net.liftweb.json.JsonAST.{JValue, JObject}
import net.liftweb.json.JsonDSL._
import org.scalafoursquare.response._
import org.specs.SpecsMatchers
import org.junit.{Ignore, Test}

class ExtractionTest extends SpecsMatchers {
  implicit val formats = APICustomSerializers.formats

  def testExtraction[T](jsonStr: String, checkMatch: Boolean = true)(implicit mf: Manifest[T]): T = {
    println()
    println(mf.erasure.getName)

    // println(jsonStr)
    val json = JsonParser.parse(jsonStr)
    // println(Printer.pretty(JsonAST.render(json)))
    val extracted = json.extract[T]
    val extJson = Extraction.decompose(extracted)

    val original = Printer.compact(JsonAST.render(json))
    val unparsed = Printer.compact(JsonAST.render(extJson))

    println(original)
    println(extracted)
    println(unparsed)

    if (checkMatch && unparsed != original) {
      println("Original: " + Printer.pretty(JsonAST.render(json)))
      println("Extracted: " + Printer.pretty(JsonAST.render(extJson)))

      val cmp = TestUtil.JsonDiff.compare(json, extJson)
      cmp._1.map(j=>println("Same: " + Printer.pretty(JsonAST.render(j))))
      cmp._2.map(j=>println("Missed: " + Printer.pretty(JsonAST.render(j))))
      cmp._3.map(j=>println("Added: " + Printer.pretty(JsonAST.render(j))))

      // unparsed == original must_== true
    }
    extracted
  }

  object Components {
    def userContact1 = ("phone" -> "7018675309") ~ ("email" -> "user@email.com") ~ ("twitter" -> "handle") ~ ("facebook" -> "fbid")
    def userContact2 = JObject(Nil)

    def venueContact1 = ("phone" -> "5555555555") ~ ("formattedPhone" -> "(555) 555-5555") ~ ("twitter" -> "handle")
    def venueContact2 = JObject(Nil)

    def venueLocation1 = ("address" -> "5 5th St") ~ ("crossStreet" -> "4th St") ~ ("city" -> "City") ~ ("state" -> "State") ~
      ("postalCode" -> "11111") ~ ("country" -> "USA") ~ ("lat" -> 25.5) ~ ("lng" -> -75.5) ~ ("distance" -> 500)
    def venueLocation2 = JObject(Nil)

    def compactUser1 = ("id" -> "uid") ~ ("firstName" -> "First") ~ ("lastName" -> "Last") ~
      ("photo" -> "photo.jpg") ~ ("gender" -> "male") ~ ("homeCity" -> "New York, NY") ~ ("relationship" -> "self")

    def compactUser2 = ("id" -> "uid") ~ ("firstName" -> "First") ~ ("photo" -> "photo.jpg") ~
      ("gender" -> "male") ~ ("homeCity" -> "New York, NY")

    def userScores1 = ("recent" -> 10) ~ ("max" -> 20) ~ ("goal" -> 30) ~ ("checkinsCount" -> 10)
    def userScores2 = ("recent" -> 10) ~ ("max" -> 20) ~ ("checkinsCount" -> 10)

    def userDetail1 = compactUser1 ~ ("type" -> "user") ~ ("pings" -> false) ~ ("contact" -> userContact1) ~
      ("badges" -> ("count" -> 10)) ~ ("mayorships" -> countList(3, List(compactVenue1, compactVenue2))) ~
      ("checkins" -> countList(2, List(checkinForFriend1, checkinForFriend2))) ~ ("friends" -> userFriends1) ~
      ("followers" -> ("count" -> 4)) ~ ("following" -> ("count" -> 5)) ~ ("requests" -> ("count" -> 6)) ~
      ("tips" -> ("count" -> 7)) ~ ("todos" -> ("count" -> 8)) ~ ("scores" -> userScores1)

    def userDetail2 = compactUser2 ~ ("type" -> "user") ~ ("contact" -> userContact2) ~ ("badges" -> ("count" -> 10)) ~
      ("mayorships" -> countList(3, Nil)) ~ ("checkins" -> countList(0, List[JValue]())) ~ ("friends" -> userFriends2) ~
      ("tips" -> ("count" -> 7)) ~ ("todos" -> ("count" -> 8)) ~ ("scores" -> userScores2)

    def userFriends1 = ("count" -> 10) ~ ("groups" -> List(
      ("type" -> "friends") ~ ("name" -> "mutual friends") ~ ("count" -> 6) ~ ("items" -> List(compactUser1)),
      ("type" -> "others") ~ ("name" -> "other friends") ~ ("count" -> 4) ~ ("items" -> List(compactUser2, compactUser1))
    ))

    def userFriends2 = ("count" -> 0) ~ ("groups" -> List(
      ("type" -> "others") ~ ("name" -> "other friends") ~ ("count" -> 0) ~ ("items" -> List[JValue]())
    ))

    def categoryCore1 = ("id" -> "cid") ~ ("name" -> "Cat") ~ ("pluralName" -> "Cats") ~ ("icon" -> "picture.jpg")
    def categoryCore2 = ("name" -> "Cat") ~ ("pluralName" -> "Cats") ~ ("icon" -> "picture.jpg")

    def compactCategory1 = categoryCore1 ~ ("parents" -> List("Parent", "GrandParent")) ~ ("primary" -> true)
    def compactCategory2 = categoryCore2 ~ ("parents" -> List[JValue]()) ~ ("primary" -> true)

    def venueStats1 = ("checkinsCount" -> 10) ~ ("usersCount" -> 11) ~ ("tipCount" -> 12)
    def venueStats2 = ("checkinsCount" -> 10) ~ ("usersCount" -> 11) ~ ("tipCount" -> 12)

    def unlockedInfo1 = ("state" -> "aState") ~ ("progress" -> 5) ~ ("progressDescription" -> "progress") ~
      ("detail" -> "details") ~ ("target" -> 10) ~ ("friendsHere" -> List(compactUser1, compactUser2))
    def unlockedInfo2 = ("state" -> "aState") ~ ("progressDescription" -> "progress") ~ ("detail" -> "details") ~
      ("progress" -> 5) ~ ("target" -> 10) ~ ("friendsHere" -> List(compactUser1, compactUser2))

    def redemptionInfo1 = ("provider" -> "p") ~ ("iconUrl" -> "url.com") ~ ("redemption" -> "method") ~
      ("interaction" -> ("prompt" -> "prompt1") ~ ("waitingPrompt" -> "prompt2") ~ ("timeoutPrompt" -> "timeout") ~
         ("timeoutExplanation" -> "explain") ~ ("input" -> "state") ~ ("entryUrl" -> "url1") ~
         ("purchaseUrl" -> "url2") ~ ("exitUrl" -> "url"))
    def redemptionInfo2 = ("provider" -> "p") ~ ("redemption" -> "method")

    def venueSpecial1 = ("id" -> "sid") ~ ("type" -> "Special") ~ ("message" -> "Special!") ~ ("description" -> "Yay!") ~
      ("finePrint" -> "read this") ~ ("unlocked" -> true) ~ ("icon" -> "theIcon") ~ ("title" -> "theTitle") ~
      unlockedInfo1 ~ redemptionInfo1
    def venueSpecial2 = ("id" -> "sid") ~ ("type" -> "Special") ~ ("message" -> "Special!") ~
      ("icon" -> "theIcon") ~ ("title" -> "theTitle") ~
      redemptionInfo2

    def venueCore1 = ("id" -> "vid") ~ ("name" -> "venueName") ~ ("itemId" -> "tlid") ~ ("contact" -> venueContact1) ~
      ("location" -> venueLocation1) ~ ("categories" -> List(compactCategory1, compactCategory2)) ~
      ("verified" -> true) ~ ("stats" -> venueStats1) ~ ("url" -> "url.com")
    def venueCore2 = ("id" -> "vid") ~ ("name" -> "venueName") ~ ("itemId" -> "tlid") ~ ("contact" -> venueContact2) ~
      ("location" -> venueLocation2) ~ ("categories" -> List[JValue]()) ~ ("verified" -> true) ~ ("stats" -> venueStats2)

    def compactVenue1 = venueCore1 ~ ("specials" -> List(venueSpecial1, venueSpecial2)) ~ ("hereNow" -> ("count" -> 10)) ~ 
      ("events" -> List(compactEvent1, compactEvent2))
    def compactVenue2 = venueCore2

    def venueFriendGroup1 = ("type" -> "groupType") ~ ("name" -> "groupName") ~ ("count" -> 2) ~ ("items" -> List(checkinForVenue1, checkinForVenue2))
    def venueFriendGroup2 = ("type" -> "groupType") ~ ("name" -> "groupName") ~ ("count" -> 0) ~ ("items" -> List[JValue]())

    def venueHereNow1 = ("count" -> 2) ~ ("groups" -> List(venueFriendGroup1, venueFriendGroup2))
    def venueHereNow2 = ("count" -> 0) ~ ("groups" -> List[JValue]())

    def venueMayor1 = ("count" -> 5) ~ ("user" -> compactUser1)
    def venueMayor2 = ("count" -> 2)

    def venueTipGroup1 = ("type" -> "groupType") ~ ("name" -> "groupName") ~ ("count" -> 2) ~ ("items" -> List(tipForVenue1, tipForVenue2))
    def venueTipGroup2 = ("type" -> "groupType") ~ ("name" -> "groupName") ~ ("count" -> 0) ~ ("items" -> List[JValue]())

    def venueTipData1 = ("count" -> 2) ~ ("groups" -> List(venueTipGroup1, venueTipGroup2))
    def venueTipData2 = ("count" -> 0) ~ ("groups" -> List[JValue]())

    def venuePhotosData1 = ("count" -> 4) ~ ("groups" -> List(
      ("type" -> "checkin") ~ ("name" -> "friends' checkin photos") ~ ("count" -> 2) ~ ("items" -> List(photoForVenueListWithCheckin1, photoForVenueListWithCheckin2)),
      ("type" -> "venue") ~ ("name" -> "venue photos") ~ ("count" -> 2) ~ ("items" -> List(photoForList1, photoForList2))
    ))
    def venuePhotosData2 = ("count" -> 0) ~ ("groups" -> List(
      ("type" -> "checkin") ~ ("name" -> "friends' checkin photos") ~ ("count" -> 0) ~ ("items" -> List[JValue]()),
      ("type" -> "venue") ~ ("name" -> "venue photos") ~ ("count" -> 0) ~ ("items" -> List[JValue]())
    ))

    def specialNearby1 = venueSpecial1 ~ ("venue" -> venueCore1)
    def specialNearby2 = venueSpecial2 ~ ("venue" -> venueCore2)

    def genericVenueDetail1 = venueCore1 ~ ("createdAt" -> 1000) ~ ("hereNow" -> venueHereNow1) ~
      ("mayor" -> venueMayor1) ~ ("tips" -> venueTipData1) ~ ("tags" -> List("tag1", "tag2")) ~
      ("specials" -> List(venueSpecial1, venueSpecial2)) ~ ("specialsNearby" -> List(specialNearby1, specialNearby2)) ~
      ("shortUrl" -> "venueShortUrl") ~ ("timeZone" -> "venueTimeZone") ~ ("beenHere" -> ("count" -> 5)) ~
      ("photos" -> venuePhotosData1) ~ ("description" -> "venueDescription") ~
      ("events" -> List(compactEvent1, compactEvent2)) ~ ("lists" -> countList(2, List("List1", "List2")))

    def genericVenueDetail2 = venueCore2 ~ ("createdAt" -> 1000) ~ ("hereNow" -> venueHereNow2) ~
      ("mayor" -> venueMayor2) ~ ("tips" -> venueTipData2) ~ ("tags" -> List("tag1", "tag2")) ~
      ("specials" -> List[JValue]()) ~ ("specialsNearby" -> List[JValue]()) ~
      ("shortUrl" -> "venueShortUrl") ~ ("timeZone" -> "venueTimeZone")

    def venueDetail1 = genericVenueDetail1 ~ ("todos" -> countList(2, List(todoForVenue1, todoForVenue2)))
    def venueDetail2 = genericVenueDetail2 ~ ("todos" -> countList(0, List[JValue]()))

    def checkinCore1 = ("id" -> "chid") ~ ("createdAt" -> 1000) ~ ("type" -> "checkin") ~ ("private" -> true) ~
      ("shout" -> "Shout!") ~ ("isMayor" -> false) ~ ("timeZone" -> "Time/Zone")
    def checkinCore2 = ("id" -> "chid") ~ ("createdAt" -> 1000) ~ ("type" -> "checkin") ~ ("isMayor" -> false) ~ ("timeZone" -> "Time/Zone")
    
    def checkinLocation1 = ("name" -> "place") ~ ("lat" -> 23.5) ~ ("lng" -> -44.5)
    def checkinLocation2 = JObject(Nil)
    
    def compactEvent1 = ("id" -> "evid") ~ ("name" -> "Event")
    def compactEvent2 = ("id" -> "evid") ~ ("name" -> "Event")

    def checkinForFriend1 = checkinCore1 ~ ("venue" -> compactVenue1) ~ ("location" -> checkinLocation1) ~ ("event" -> compactEvent1)
    def checkinForFriend2 = checkinCore2

    def checkinForVenue1 = checkinCore1 ~ ("user" -> compactUser1)
    def checkinForVenue2 = checkinCore2

    def leaderboardItem1 = ("user" -> compactUser1) ~ ("scores" -> userScores1) ~ ("rank" -> 1)
    def leaderboardItem2 = ("user" -> compactUser2) ~ ("scores" -> userScores2) ~ ("rank" -> 2)

    def badgeGroup1 = ("type" -> "typeName") ~ ("name" -> "setName") ~ ("image" -> image1) ~
      ("items" -> List("bh1id", "bh2id")) ~ ("groups" -> List(badgeGroup2))
    def badgeGroup2 = ("type" -> "typeName") ~ ("name" -> "setName") ~ ("image" -> image2) ~
      ("items" -> List[JValue]()) ~ ("groups" -> List[JValue]())

    def badgeSet1 = ("groups" -> List(badgeGroup1, badgeGroup2))
    def badgeSet2 = ("groups" -> List[JValue]())

    def image1 = ("prefix" -> "imgPrefix") ~ ("sizes" -> List(100, 200)) ~ ("name" -> "imgName")
    def image2 = ("prefix" -> "imgPrefix") ~ ("sizes" -> List[JValue]()) ~ ("name" -> "imgName")

    def badge1 = ("id" -> "bh1id") ~ ("badgeId" -> "bid") ~ ("name" -> "badgeName") ~ ("description" -> "badgeText") ~
      ("hint" -> "badgeHint") ~ ("image" -> image1) ~ ("unlocks" -> List(("checkins") -> List(checkinForFriend1, checkinForFriend2)))
    def badge2 = ("id" -> "bh2id") ~ ("badgeId" -> "bid") ~ ("name" -> "badgeName") ~ ("image" -> image2) ~
      ("unlocks" -> List[JValue]())

    def badges1 = ("bh1id" -> badge1) ~ ("bh2id" -> badge2)
    def badges2 = JObject(Nil)

    def oauthSrc1 = ("name" -> "oauthName") ~ ("url" -> "oauthUrl")
    def oauthSrc2 = ("name" -> "oauthName") ~ ("url" -> "oauthUrl")

    def photoDims1 = ("url" -> "dimUrl") ~ ("width" -> 100) ~ ("height" -> 100)
    def photoDims2 = ("url" -> "dimUrl") ~ ("width" -> 200) ~ ("height" -> 200)

    def photoCore1 = ("id" -> "phid") ~ ("createdAt" -> 2000) ~ ("url" -> "photoUrl") ~
      ("sizes" -> countList(2, List(photoDims1, photoDims2))) ~ ("source" -> oauthSrc1)
    def photoCore2 = ("id" -> "phid") ~ ("createdAt" -> 2000) ~ ("url" -> "photoUrl") ~
      ("sizes" -> countList(0, List[JValue]()))

    def photoForList1 = photoCore1 ~ ("user" -> compactUser1) ~ ("visibility" -> "public")
    def photoForList2 = photoCore2 ~ ("visibility" -> "friends")

    def photoForVenueListWithCheckin1 = photoForList1 ~ ("checkin" -> checkinCore1)
    def photoForVenueListWithCheckin2 = photoForList2

    def tipCore1 = ("id" -> "tid") ~ ("createdAt" -> 1000) ~ ("itemId" -> "eid") ~ ("text" -> "tipText") ~
      ("url" -> "tipUrl") ~ ("status" -> "tipBindStatus") ~ ("photo" -> photoCore1) ~ ("photourl" -> "tipPhotoUrl")
    def tipCore2 = ("id" -> "tid") ~ ("createdAt" -> 1000) ~ ("itemId" -> "eid") ~ ("text" -> "tipText")

    def tipStats1 = ("todo" -> ("count" -> 5)) ~ ("done" -> ("count" -> 5))
    def tipStats2 = ("todo" -> ("count" -> 0)) ~ ("done" -> ("count" -> 0))

    def tipForUser1 = tipCore1 ~ tipStats1 ~ ("venue" -> compactVenue1)
    def tipForUser2 = tipCore2 ~ tipStats2

    def tipForList1 = tipCore1 ~ tipStats1 ~ ("venue" -> compactVenue1) ~ ("user" -> compactUser1)
    def tipForList2 = tipCore2 ~ tipStats2

    def tipForVenue1 = tipCore1 ~ tipStats1 ~ ("user" -> compactUser1)
    def tipForVenue2 = tipCore2 ~ tipStats2

    def todoCore1 = ("id" -> "todoId") ~ ("createdAt" -> 1000)
    def todoCore2 = ("id" -> "todoId") ~ ("createdAt" -> 1000)

    def todoListName1 = ("list" -> ("name" -> "listName"))
    def todoListName2 = ("list" -> ("name" -> "listName"))

    def todoForList1 = todoCore1 ~ todoListName1 ~ ("tip" -> tipForList1)
    def todoForList2 = todoCore2 ~ todoListName2

    def todoForVenue1 = todoCore1 ~ todoListName1 ~ ("tip" -> tipForVenue1)
    def todoForVenue2 = todoCore2 ~ todoListName2

    def venueHistory1 = ("beenHere" -> 10) ~ ("venue" -> compactVenue1)
    def venueHistory2 = ("beenHere" -> 10) ~ ("venue" -> compactVenue2)

    def countList(count: Int, items: List[JValue]) = ("count" -> count) ~ ("items" -> items)

    def json(v:JValue) = {
      Printer.compact(JsonAST.render(v))
    }

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
    testExtraction[UserMayorshipsResponse](C.json(("mayorships" -> C.countList(2, List(C.compactVenue1, C.compactVenue2)))))
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
    val jsonStr = """
    """
    testExtraction[VenueAddResponse](jsonStr)
  }

  @Test
  def venuesCategories() {
    val jsonStr = """
    """
    testExtraction[VenueCategoriesResponse](jsonStr)
  }

  @Test
  def venuesExplore() {
    val jsonStr = """
    """
    testExtraction[VenueExploreResponse](jsonStr)
  }

  @Test
  def venuesSearch() {
    val jsonStr = """
    """
    testExtraction[VenueSearchResponse](jsonStr)
  }

  @Test
  def venuesTrending() {
    val jsonStr = """
    """
    testExtraction[VenueTrendingResponse](jsonStr)
  }

  @Test
  def venuesHereNow() {
    val jsonStr = """
    """
    testExtraction[VenueHereNowResponse](jsonStr)
  }

  @Test
  def venuesTips() {
    val jsonStr = """
    """
    testExtraction[VenueTipsResponse](jsonStr)
  }

  @Test
  def venuesPhotos() {
    val jsonStr = """
    """
    testExtraction[VenuePhotosResponse](jsonStr)
  }

  @Test
  def venuesLinks() {
    val jsonStr = """
    """
    testExtraction[VenueLinksResponse](jsonStr)
  }

  @Test
  def venueMarkTodo() {
    val jsonStr = """
    """
    testExtraction[VenueMarkTodoResponse](jsonStr)
  }

  @Test
  def venueFlag() {
    val jsonStr = """
    """
    testExtraction[VenueFlagResponse](jsonStr)
  }

  @Test
  def venueEdit() {
    val jsonStr = """
    """
    testExtraction[VenueEditResponse](jsonStr)
  }

  @Test
  def venueProposeEdit() {
    val jsonStr = """
    """
    testExtraction[VenueProposeEditResponse](jsonStr)
  }

  @Test
  def checkinDetail() {
    val jsonStr = """
    """
    testExtraction[CheckinDetailResponse](jsonStr)
  }

  @Test
  def checkinAdd() {
    val jsonStr = """
    """
    testExtraction[AddCheckinResponse](jsonStr)
  }

  @Test
  def checkinsRecent() {
    val jsonStr = """
    """
    testExtraction[RecentCheckinsResponse](jsonStr)
  }

  @Test
  def addCheckinComment() {
    val jsonStr = """
    """
    testExtraction[CheckinAddCommentResponse](jsonStr)
  }

  @Test
  def deleteCheckinComment() {
    val jsonStr = """
    """    
    testExtraction[CheckinDeleteCommentResponse](jsonStr)
  }

  @Test
  def tipDetail() {
    val jsonStr = """
    """
    testExtraction[TipDetailResponse](jsonStr)
  }

  @Test
  def addTip() {
    val jsonStr = """
    """
    testExtraction[AddTipResponse](jsonStr)
  }

  @Test
  def tipSearch() {
    val jsonStr = """
    """
    testExtraction[TipSearchResponse](jsonStr)
  }

  @Test
  def markTipTodo() {
    val jsonStr = """
    """
    testExtraction[TipMarkTodoResponse](jsonStr)
  }

  @Test
  def markTipDone() {
    val jsonStr = """
    """
    testExtraction[TipMarkDoneResponse](jsonStr)
  }

  @Test
  def unmarkTip() {
    val jsonStr = """
    """
    testExtraction[TipUnmarkResponse](jsonStr)
  }

  @Test
  def updateDetails() {
    val jsonStr = """
    """
    testExtraction[UpdateDetailResponse](jsonStr)
  }


  @Test
  def notifications() {
    val jsonStr = """
    """
    testExtraction[NotificationsResponse](jsonStr)
  }

  @Test
  def markNotificationsRead() {
    val jsonStr = """
    """
    testExtraction[MarkNotificationsReadResponse](jsonStr)
  }

  @Test
  def photoDetail() {
    val jsonStr = """
    """
    testExtraction[PhotoDetailResponse](jsonStr)
  }

  @Test
  def addPhoto() {
    val jsonStr = """
    """
    testExtraction[AddPhotoResponse](jsonStr)
  }

  @Test
  def settingDetail() {
    testExtraction[SettingsDetailResponse]("""{"value":true}""")
    testExtraction[SettingsDetailResponse]("""{"value":"yes"}""")
    testExtraction[SettingsDetailResponse]("""{"value":1}""")
  }

  @Test
  def allSettings() {
    val jsonStr = """
      {"settings":{"receivePings":true,"receiveCommentPings":true,"twitter":"twitter1","sendToTwitter":false,"sendMayorshipsToTwitter":true,
      "sendBadgesToTwitter":false,"facebook":1234,"sendToFacebook":false,"sendMayorshipsToFacebook":false,
      "sendBadgesToFacebook":false,"foreignConsent":"undetermined"}}
    """
    testExtraction[AllSettingsResponse](jsonStr)
  }

  @Test
  def changeSettings() {
    val jsonStr = """
    """
    testExtraction[ChangeSettingsResponse](jsonStr)
  }

  @Test
  def specialsDetail() {
    val jsonStr = """
    """
    testExtraction[SpecialDetailResponse](jsonStr)
  }

  @Test
  def specialsSearch() {
    val jsonStr = """
    """
    testExtraction[SpecialsSearchResponse](jsonStr)
  }

  @Test
  def flagSpecial() {
    val jsonStr = """
    """
    testExtraction[FlagSpecialResponse](jsonStr)
  }

  @Test
  def multi() {
    val jsonStr = """
    """
    testExtraction[MultiResponse[SettingsDetailResponse, SettingsDetailResponse, SettingsDetailResponse, SettingsDetailResponse, SettingsDetailResponse]](jsonStr)
  }
}
