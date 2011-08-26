package org.scalafoursquare.response

// Venue Details

case class VenueContact(phone: Option[String], formattedPhone: Option[String], twitter: Option[String])
case class VenueLocation(address: Option[String], crossStreet: Option[String],  city: Option[String],
                         state: Option[String], postalCode: Option[String], country: Option[String],
                         lat: Option[Double], lng: Option[Double], distance: Option[Int])
case class VenueStats(checkinsCount: Int, usersCount: Int, tipCount: Int)

case class VenueHereNowGroup(`type`: String, name: String, count: Int /*, items: List[String]*/)
case class VenueHereNow(count: Int, groups: List[VenueHereNowGroup])
case class VenueMayor(count: Int)

case class VenueTipGroup(`type`: String, name: String, count: Int /*, items: List[String]*/)
case class VenueTips(count: Int, groups: List[VenueTipGroup])
case class VenueBeenHere(count: Int)

case class VenuePhotoGroup(`type`: String, name: String, count: Int /*, items: List[String]*/)
case class VenuePhotos(count: Int, groups: List[VenuePhotoGroup])
case class VenueTodos(count: Int /*, items: List[String]*/)
case class VenueHereNowCompact(count: Int)

trait VenueKernel {
  def id: String
  def name: String
  def itemId: String
  def contact: VenueContact
  def location: VenueLocation
  def categories: List[VenueCategoryCompact]
  def verified: Boolean
  def stats: VenueStats
  def url: Option[String]
}

case class VenueCore(id: String, name: String, itemId: String, contact: VenueContact, location: VenueLocation,
                     categories: List[VenueCategoryCompact], verified: Boolean, stats: VenueStats, url: Option[String]) extends VenueKernel

case class VenueCompact(id: String, name: String, itemId: String, contact: VenueContact, location: VenueLocation,
                        categories: List[VenueCategoryCompact], verified: Boolean, stats: VenueStats, url: Option[String],
                        specials: Option[List[String]], hereNow: Option[VenueHereNowCompact], events: Option[List[String]]) extends VenueKernel

case class VenueDetail(id: String, name: String, itemId: String, contact: VenueContact, location: VenueLocation,
                       categories: List[VenueCategoryCompact], verified: Boolean, stats: VenueStats, url: Option[String],
                       createdAt: Long, hereNow: VenueHereNow, mayor: VenueMayor, tips: VenueTips,
                       tags: List[String], /*specials: List[String],*/ /*specialsNearby: List[String], */
                       shortUrl: String, timeZone: String, beenHere: Option[VenueBeenHere],
                       photos: VenuePhotos, description: Option[String], /*events: List[String],*/
                       todos: Option[VenueTodos]) extends VenueKernel

case class VenueDetailResponse(venue: VenueDetail)


// User Details
// TODO: find which of these can be merged; which items are optional
case class UserContact(phone: Option[String], email: Option[String], twitter: Option[String], facebook: Option[String])
case class UserBadges(count: Int)
case class UserMayorships(count: Int /*, items: List[String]*/)

case class UserCheckins(count: Int, items: List[CheckinForFriend])
case class UserFriendGroup(`type`: String, name: String, count: Int /*, items: List[String] */)
case class UserFriends(count: Int, groups: List[UserFriendGroup])
case class UserFollowers(count: Int)
case class UserFollowing(count: Int)
case class UserRequests(count: Int)
case class UserTips(count: Int)
case class UserTodos(count: Int)
case class UserScores(recent: Int, max: Int, checkinsCount: Int)
case class UserDetail(id: String, firstName: String, lastName: Option[String], photo: String,
                      gender: String, homeCity: String, relationship: Option[String],
                      `type`: String, pings: Option[Boolean], contact: UserContact, badges: UserBadges,
                      mayorships: UserMayorships, checkins: UserCheckins, friends: UserFriends,
                      followers: Option[UserFollowers], following: Option[UserFollowing],
                      requests: Option[UserRequests],
                      tips: UserTips, todos: UserTodos, scores: UserScores) extends UserKernel
case class UserDetailResponse(user: UserDetail)

case class UserCompact(id: String, firstName: String, lastName: Option[String], photo: String,
                          gender: String, homeCity: String, relationship: Option[String]) extends UserKernel

case class UserCore(id: String, firstName: String, lastName: Option[String], photo: String,
                          gender: String, homeCity: String, relationship: Option[String],
                          `type`: Option[String]) extends UserKernel

case class UserRequestResponse(requests: List[UserCompact])

trait Primitive
case class IntPrimitive(v: Int) extends Primitive
case class DoublePrimitive(v: Double) extends Primitive
case class StringPrimitive(v: String) extends Primitive
case object NothingPrimitive extends Primitive

class UserSearchUnmatched(val map: Map[String, List[Primitive]])
case class UserSearchResponse(results: List[UserCompact], unmatched: UserSearchUnmatched)

trait UserKernel {
  def id: String
  def firstName: String
  def lastName: Option[String]
  def photo: String
  def gender: String
  def homeCity: String
  def relationship: Option[String]
}

// Venue Categories

trait VenueCategoryKernel {
  def id: String
  def name: String
  def pluralName: String
  def icon: String
}
case class VenueCategoryCore(id: String, name: String, pluralName: String, icon: String) extends VenueCategoryKernel
case class VenueCategoryCompact(id: String, name: String, pluralName: String, icon: String, parents: List[String], primary: Option[Boolean]) extends VenueCategoryKernel
case class VenueCategoryWithChildren(id: String, name: String, pluralName: String, icon: String, categories: List[VenueCategoryWithChildren]) extends VenueCategoryKernel

case class VenueCategoriesResponse(categories: List[VenueCategoryWithChildren])

case class UserPhotoUpdateResponse(user: UserDetail)

case class CheckinLocation(name: String, lat: Double, lng: Double)
case class CheckinForFriend(id: String,
                            createdAt: Long,
                            `type`: String,
                            `private`: Option[Boolean],
                            shout: Option[String],
                            isMayor: Option[Boolean],
                            timeZone: String,
                            venue: Option[VenueCompact],
                            location: Option[CheckinLocation],
                            event: Option[CompactEvent])

case class CompactEvent(id: String, name: Option[String])

case class AddCheckinResponse(checkin: CheckinForFriend)

case class LeaderboardScore(recent: Int, max: Int, checkinsCount: Int)
case class LeaderboardItem(user: UserCompact, scores: LeaderboardScore, rank: Int)
case class Leaderboard(count: Int, items: List[LeaderboardItem])
case class LeaderboardResponse(leaderboard: Leaderboard)

case class BadgeImage(prefix: String, sizes: List[Int], name: String)
case class BadgeGroup(`type`: String, name: String, image: BadgeImage, items: List[String], groups: List[BadgeGroup])
case class BadgeSets(groups: List[BadgeGroup])
case class BadgeUnlocks(checkins: List[CheckinForFriend])
case class Badge(id: String, badgeId: String, name: String, description: String, image: BadgeImage , unlocks:List[BadgeUnlocks])
case class Badges(map: Map[String, Badge])
case class UserBadgesResponse(sets: BadgeSets, badges: Badges, defaultSetType: String)

// Settings
case class SettingsDetailResponse(value: Boolean) // TODO: Are these always boolean?  If not, how to convert?
case class ChangeSettingsResponse() // (message: String)
// {"meta":{"code":200},"notifications":[{"type":"notificationTray","item":{"unreadCount":0}}],"response":{"settings":{"receivePings":true,"receiveCommentPings":true,"twitter":"nfolkert","sendToTwitter":true,"sendMayorshipsToTwitter":true,"sendBadgesToTwitter":true,"facebook":203195,"sendToFacebook":true,"sendMayorshipsToFacebook":false,"sendBadgesToFacebook":true,"enableDebug":true,"foreignConsent":"undetermined"}}}

case class Meta(code: Int, errorType: Option[String], errorDetail: Option[String])

case class Notification(`type`: String /*, item: DIFFERENT TYPES */)

case class Response[T](meta: Meta, notifications: Option[List[Notification]], response: Option[T])

case class MultiResponse[A,B,C,D,E](meta: Meta, notifications: Option[List[Notification]], responses: (Option[Response[A]], Option[Response[B]], Option[Response[C]], Option[Response[D]], Option[Response[E]]))
case class MultiResponseList[A](meta: Meta, notifications: Option[List[Notification]], responses: Option[List[Response[A]]])

// TODO:
case class UpdateDetailResponse()
case class CheckinDetailResponse()
case class TipDetailResponse()
case class PhotoDetailResponse()
case class SpecialDetailResponse()
case class VenueAddResponse()
case class VenueExploreResponse()
case class VenueSearchResponse()
case class VenueTrendingResponse()
case class RecentCheckinsResponse()
case class AddTipResponse()
case class TipSearchResponse()
case class NotificationsResponse()
case class AddPhotoResponse()
case class AllSettingsResponse()
case class SpecialsSearchResponse()
case class VenueHereNowResponse()
case class VenueTipsResponse()
case class VenuePhotosResponse()
case class VenueLinksResponse()
case class UserCheckinsResponse()
case class UserFriendsResponse()
case class UserMayorshipsResponse()
case class UserTipsResponse()
case class UserTodosResponse()
case class UserVenueHistoryResponse()
case class UserFriendRequestResponse()
case class UserUnfriendResponse()
case class UserApproveFriendResponse()
case class UserDenyFriendshipResponse()
case class UserSetPingsResponse()
case class FlagSpecialResponse()
case class MarkNotificationsReadResponse()
case class TipUnmarkResponse()
case class TipMarkDoneResponse()
case class TipMarkTodoResponse()
case class CheckinDeleteCommentResponse()
case class CheckinAddCommentResponse()
case class VenueProposeEditResponse()
case class VenueEditResponse()
case class VenueMarkTodoResponse()
case class VenueFlagResponse()

