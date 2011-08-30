package org.scalafoursquare

import net.liftweb.json.{JsonAST, Printer}
import net.liftweb.json.JsonAST.{JValue, JObject}
import net.liftweb.json.JsonDSL._

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

  def categoryWithChildren1 = categoryCore1 ~ ("categories" -> List(categoryWithChildren2, categoryWithChildren3))
  def categoryWithChildren2 = categoryCore2 ~ ("categories" -> List(categoryWithChildren3))
  def categoryWithChildren3 = categoryCore1 ~ ("categories" -> List[JValue]())

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

  def specialForNotification1 = venueSpecial1 ~ ("venue" -> venueCore1)
  def specialForNotification2 = venueSpecial2 ~ ("venue" -> venueCore2)

  def venueCore1 = ("id" -> "vid") ~ ("name" -> "venueName") ~ ("contact" -> venueContact1) ~
    ("location" -> venueLocation1) ~ ("categories" -> List(compactCategory1, compactCategory2)) ~
    ("verified" -> true) ~ ("stats" -> venueStats1) ~ ("url" -> "url.com")
  def venueCore2 = ("id" -> "vid") ~ ("name" -> "venueName") ~ ("contact" -> venueContact2) ~
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

  def checkinForFeed1 = checkinCore1 ~ ("venue" -> compactVenue1) ~ ("location" -> checkinLocation1) ~ ("user" -> compactUser1)
  def checkinForFeed2 = checkinCore2

  def userMentionEntity1 = ("indices" -> List(1, 5)) ~ ("type" -> "user") ~ ("user" -> compactUser1)
  def userMentionEntity2 = ("indices" -> List(1, 5)) ~ ("type" -> "user") ~ ("user" -> compactUser2)

  def commentsCore1 = ("id" -> "cmid") ~ ("createdAt" -> 1000) ~
    ("user" -> compactUser1) ~ ("text" -> "commentText") ~ ("entities" -> List(userMentionEntity1, userMentionEntity2))
  def commentsCore2 = ("id" -> "cmid") ~ ("createdAt" -> 1000) ~
    ("user" -> compactUser2) ~ ("text" -> "commentText") ~ ("entities" -> List[JValue]())

  def checkinDetail1 = checkinCore1 ~ ("entities" -> List(userMentionEntity1, userMentionEntity2)) ~
    ("user" -> compactUser1) ~
    ("venue" -> compactVenue1) ~
    ("location" -> checkinLocation1) ~
    ("source" -> oauthSrc1) ~
    ("distance" -> 25) ~
    ("photos" -> countList(2, List(photoForList1, photoForList2))) ~
    ("comments" -> countList(2, List(commentsCore1, commentsCore2))) ~
    ("event" -> compactEvent1) ~
    ("overlaps" -> countList(2, List(checkinForVenue1, checkinForVenue2)))
  def checkinDetail2 = checkinCore2 ~ ("entities" -> List[JValue]())

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

  def imagePath1 = ("fullPath" -> "imagePath")
  def imagePath2 = ("fullPath" -> "imagePath")

  def badge1 = ("id" -> "bh1id") ~ ("badgeId" -> "bid") ~ ("name" -> "badgeName") ~ ("description" -> "badgeText") ~
    ("hint" -> "badgeHint") ~ ("image" -> image1) ~ ("unlocks" -> List(("checkins") -> List(checkinForFriend1, checkinForFriend2)))
  def badge2 = ("id" -> "bh2id") ~ ("badgeId" -> "bid") ~ ("name" -> "badgeName") ~ ("image" -> image2) ~
    ("unlocks" -> List[JValue]())

  def badgePlusUser1 = badge1 ~ ("user" -> compactUser1)
  def badgePlusUser2 = badge2

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

  def photoDetail1 = photoCore1 ~ ("user" -> compactUser1) ~ ("venue" -> compactVenue1) ~
    ("checkin" -> checkinCore1) ~ ("tip" -> tipForPhoto1)
  def photoDetail2 = photoCore2

  def tipCore1 = ("id" -> "tid") ~ ("createdAt" -> 1000) ~ ("text" -> "tipText") ~
    ("url" -> "tipUrl") ~ ("status" -> "tipBindStatus") ~ ("photo" -> photoCore1) ~ ("photourl" -> "tipPhotoUrl")
  def tipCore2 = ("id" -> "tid") ~ ("createdAt" -> 1000) ~ ("text" -> "tipText")

  def tipStats1 = ("todo" -> ("count" -> 5)) ~ ("done" -> ("count" -> 5))
  def tipStats2 = ("todo" -> ("count" -> 0)) ~ ("done" -> ("count" -> 0))

  def tipForUser1 = tipCore1 ~ tipStats1 ~ ("venue" -> compactVenue1)
  def tipForUser2 = tipCore2 ~ tipStats2

  def tipForList1 = tipCore1 ~ tipStats1 ~ ("venue" -> compactVenue1) ~ ("user" -> compactUser1)
  def tipForList2 = tipCore2 ~ tipStats2

  def tipForVenue1 = tipCore1 ~ tipStats1 ~ ("user" -> compactUser1)
  def tipForVenue2 = tipCore2 ~ tipStats2

  def tipForPhoto1 = tipCore1 ~ tipStats1
  def tipForPhoto2 = tipCore2 ~ tipStats2

  def tipTodoGroup1 = ("type" -> "groupType") ~ ("name" -> "groupName") ~ ("count" -> 2) ~
    ("items" -> List(compactUser1, compactUser2))
  def tipTodoGroup2 = ("type" -> "groupType") ~ ("name" -> "groupName") ~
    ("items" -> List[JValue]())

  def tipTodoGroups1 = ("count" -> 2) ~ ("groups" -> List(tipTodoGroup1, tipTodoGroup2))
  def tipTodoGroups2 = ("count" -> 0) ~ ("groups" -> List[JValue]())

  def tipDetail1 = tipCore1 ~ ("venue" -> compactVenue1) ~ ("user" -> compactUser1) ~
    ("todo" -> tipTodoGroups1) ~ ("done" -> tipTodoGroups1)
  def tipDetail2 = tipCore1 ~ ("todo" -> tipTodoGroups2) ~ ("done" -> tipTodoGroups2)

  def todoCore1 = ("id" -> "todoId") ~ ("createdAt" -> 1000)
  def todoCore2 = ("id" -> "todoId") ~ ("createdAt" -> 1000)

  def todoListName1 = ("list" -> ("name" -> "listName"))
  def todoListName2 = ("list" -> ("name" -> "listName"))

  def todoForList1 = todoCore1 ~ todoListName1 ~ ("tip" -> tipForList1)
  def todoForList2 = todoCore2 ~ todoListName2

  def todoForVenue1 = todoCore1 ~ todoListName1 ~ ("tip" -> tipForVenue1)
  def todoForVenue2 = todoCore2 ~ todoListName2

  def listCore1 = ("id" -> "lid") ~ ("name" -> "name") ~ ("description" -> "description") ~
    ("user" -> compactUser1) ~ ("editable" -> true) ~ ("public" -> true) ~
    ("collaborative" -> true) ~ ("url" -> "url") ~ ("createdAt" -> 1000) ~ ("updatedAt" -> 1000) ~
    ("photo" -> photoForList1)
  def listCore2 = ("id" -> "lid") ~ ("name" -> "name") ~ ("description" -> "description") ~
    ("editable" -> true) ~ ("public" -> true) ~ ("collaborative" -> true) ~ ("url" -> "url")

  def listForList1 = listCore1 ~ ("followers" -> countList(2, List[JValue]())) ~
    ("listItems" -> countList(2, List[JValue]()))
  def listForList2 = listCore2 ~ ("followers" -> countList(0, List[JValue]())) ~
    ("listItems" -> countList(0, List[JValue]()))

  def venueHistory1 = ("beenHere" -> 10) ~ ("venue" -> compactVenue1)
  def venueHistory2 = ("beenHere" -> 10) ~ ("venue" -> compactVenue2)

  def venueLink1 = ("provider" -> ("id" -> "pid")) ~ ("linkedId" -> "lid") ~ ("url" -> "linkUrl")
  def venueLink2 = ("provider" -> ("id" -> "pid")) ~ ("linkedId" -> "lid")

  def keyword1 = ("displayName" -> "name") ~ ("keyword" -> "keyword")
  def keyword2 = ("displayName" -> "name") ~ ("keyword" -> "keyword")

  def reasonStructure1 = ("type" -> "reasonType") ~ ("message" -> "reasonMessage")
  def reasonStructure2 = ("type" -> "reasonType") ~ ("message" -> "reasonMessage")

  def coreRecommendation1 = ("reasons" -> countList(2, List(reasonStructure1, reasonStructure2))) ~
    ("venue" -> compactVenue1)
  def coreRecommendation2 = ("reasons" -> countList(0, List[JValue]())) ~ ("venue" -> compactVenue2)

  def compactRecommendation1 = coreRecommendation1 ~ ("todos" -> List(todoForVenue1, todoForVenue2)) ~
    ("tips" -> List(tipForVenue1, tipForVenue2))
  def compactRecommendation2 = coreRecommendation2

  def exploreGroup1 = ("type" -> "groupType") ~ ("name" -> "groupName") ~ ("count" -> 2) ~
    ("items" -> List(compactRecommendation1, compactRecommendation2))
  def exploreGroup2 = ("type" -> "groupType") ~ ("name" -> "groupName") ~ ("items" -> List[JValue]())

  def notificationCore1 = ("ids" -> List("id1", "id2")) ~ ("createdAt" -> 1000) ~ ("unread" -> true)
  def notificationCore2 = ("ids" -> List[JValue]()) ~ ("createdAt" -> 1000) ~ ("unread" -> false)

  def notificationImage1 = ("image" -> imagePath1) ~ ("imageType" -> "imageType") ~ ("icon" -> image1)
  def notificationImage2 = ("image" -> imagePath2) ~ ("imageType" -> "imageType")

  def notificationText1 = ("text" -> "notificationText") ~ entitiesAnnotation1
  def notificationText2 = ("text" -> "notificationText") ~ entitiesAnnotation2

  def entityAnnotation1 = ("indices" -> List(1, 2)) ~ ("type" -> "entityType")
  def entityAnnotation2 = ("indices" -> List(1, 2)) ~ ("type" -> "entityType")

  def entitiesAnnotation1 = ("entities" -> List(entityAnnotation1, entityAnnotation2))
  def entitiesAnnotation2 = ("entities" -> List[JValue]())

  def notificationTargetUser1 = ("type" -> "user") ~ ("object" -> compactUser1)
  def notificationTargetUser2 = ("type" -> "user") ~ ("object" -> compactUser2)

  def notificationTargetCheckin = ("type" -> "checkin") ~ ("object" -> checkinForFeed1)

  def notificationTargetVenue = ("type" -> "venue") ~ ("object" -> compactVenue1)

  def notificationTargetList1 = ("type" -> "list") ~ ("object" -> listForList1)
  def notificationTargetList2 = ("type" -> "list") ~ ("object" -> listForList2)

  // TODO
  def notificationTargetTip = ("type" -> "tip") ~ ("object" -> tipForList1)

  def notificationTargetBadge = ("type" -> "badge") ~ ("object" -> badgePlusUser1)

  def notificationTargetSpecial = ("type" -> "special") ~ ("object" -> specialForNotification1)

  def notificationTargetUrl = ("type" -> "url") ~ ("object" -> ("url" -> "someUrl"))

  def notificationForListUser1 = notificationCore1 ~ notificationImage1 ~
    ("target" -> notificationTargetUser1) ~ notificationText1
  def notificationForListUser2 = notificationCore2 ~ notificationImage2 ~
    ("target" -> notificationTargetUser2) ~ notificationText2

  def notificationForListCheckin = notificationCore1 ~ notificationImage1 ~
    ("target" -> notificationTargetCheckin) ~ notificationText1

  def notificationForListVenue = notificationCore1 ~ notificationImage1 ~
    ("target" -> notificationTargetVenue) ~ notificationText1

  def notificationForListList1 = notificationCore1 ~ notificationImage1 ~
    ("target" -> notificationTargetList1) ~ notificationText1
  def notificationForListList2 = notificationCore2 ~ notificationImage2 ~
    ("target" -> notificationTargetList2) ~ notificationText2

  def notificationForListTip = notificationCore1 ~ notificationImage1 ~
    ("target" -> notificationTargetTip) ~ notificationText1

  def notificationForListBadge = notificationCore1 ~ notificationImage1 ~
    ("target" -> notificationTargetBadge) ~ notificationText1

  def notificationForListSpecial = notificationCore1 ~ notificationImage1 ~
    ("target" -> notificationTargetSpecial) ~ notificationText1

  def notificationForListUrl = notificationCore1 ~ notificationImage1 ~
    ("target" -> notificationTargetUrl) ~ notificationText1

  def allSettings1 = ("receivePings" -> true) ~ ("receiveCommentPings" -> true) ~
    ("twitter" -> "twitter1") ~ ("sendToTwitter" -> false) ~
    ("sendMayorshipsToTwitter" -> true) ~ ("sendBadgesToTwitter" -> false) ~
    ("facebook" -> 1234) ~ ("sendToFacebook" -> false) ~ ("sendMayorshipsToFacebook" -> false) ~
    ("sendBadgesToFacebook" -> false) ~ ("foreignConsent" -> "undetermined")
  def allSettings2 = ("receivePings" -> true) ~ ("receiveCommentPings" -> true) ~
    ("sendToTwitter" -> false) ~ ("sendMayorshipsToTwitter" -> true) ~
    ("sendBadgesToTwitter" -> false) ~ ("sendToFacebook" -> false) ~
    ("sendMayorshipsToFacebook" -> false) ~ ("sendBadgesToFacebook" -> false) ~
    ("foreignConsent" -> "undetermined")

  def notifyBadge1 = ("badge" -> (("id" -> "bid") ~ ("name" -> "badgeName") ~ ("description" -> "badgeDesc") ~ ("image" -> image1)))
  def notifyBadge2 = ("badge" -> (("id" -> "bid") ~ ("name" -> "badgeName") ~ ("description" -> "badgeDesc") ~ ("image" -> image2)))

  def notifyTip1 = ("tip" -> tipForList1) ~ ("name" -> "tipName")
  def notifyTip2 = ("tip" -> tipForList2) ~ ("name" -> "tipName")

  def notifyTipAlert1 = ("tip" -> tipForList1)
  def notifyTipAlert2 = ("tip" -> tipForList2)

  def notifyLeaderboard1 = ("leaderboard" -> List(leaderboardItem1, leaderboardItem2)) ~ ("message" -> "leaderboardMessage") ~
    ("scores" -> List(scoreItem1, scoreItem2)) ~ ("total" -> 10)
  def notifyLeaderboard2 = ("leaderboard" -> List[JValue]()) ~ ("message" -> "leaderboardMessage") ~
    ("scores" -> List[JValue]()) ~ ("total" -> 0)

  def scoreItem1 = ("points" -> 6) ~ ("icon" -> "theIcon") ~ ("message" -> "scoreMessage")
  def scoreItem2 = ("points" -> 4) ~ ("icon" -> "theIcon") ~ ("message" -> "scoreMessage")

  def notifyScore1 = ("scores" -> List(scoreItem1, scoreItem2)) ~ ("total" -> 10)
  def notifyScore2 = ("scores" -> List[JValue]()) ~ ("total" -> 0)

  def notifyMayor1 = ("type" -> "type") ~ ("checkins" -> 5) ~ ("daysBehind" -> 5) ~
    ("user" -> compactUser1) ~ ("message" -> "message") ~ ("image" -> "image")
  def notifyMayor2 = ("type" -> "type") ~ ("message" -> "message") ~ ("image" -> "image")

  def notifySpecial1 = ("special" -> venueSpecial1)
  def notifySpecial2 = ("special" -> venueSpecial2)

  def notifyMessage1 = ("message" -> "message")
  def notifyMessage2 = ("message" -> "message")

  def notifyTray1 = ("unreadCount" -> 10)
  def notifyTray2 = ("unreadCount" -> 10)

  def countList(count: Int, items: List[JValue]) = ("count" -> count) ~ ("items" -> items)

  def json(v: JValue) = {
    Printer.compact(JsonAST.render(v))
  }

}