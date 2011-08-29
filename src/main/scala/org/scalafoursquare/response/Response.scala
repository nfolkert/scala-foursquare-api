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
                     categories: List[VenueCategoryCompact], verified: Boolean, stats: VenueStats, url: Option[String])

case class VenueCompact(id: String, name: String, itemId: String, contact: VenueContact, location: VenueLocation,
                        categories: List[VenueCategoryCompact], verified: Boolean, stats: VenueStats, url: Option[String],
                        specials: Option[List[Special]], hereNow: Option[VenueHereNowCompact],
                        events: Option[List[CompactEvent]]) extends VenueKernel

case class VenueDetail(
  id: String, name: String, itemId: String, contact: VenueContact, location: VenueLocation,
  categories: List[VenueCategoryCompact], verified: Boolean, stats: VenueStats, url: Option[String],
  createAt: Long,
  hereNow: VenueHereNow,
  mayor: VenueMayor,
  tips: VenueTips,
  tags: List[String],
  specials: List[Special],
  // specialsNearby: List[Special], // TODO: need to decompose this and do custom serde since case classes only support 22 fields
  shortUrl: String,
  timeZone: String,
  beenHere: Option[VenueBeenHere],
  photos: VenuePhotos,
  description: Option[String],
  events: List[CompactEvent],
  todos: Option[VenueTodos]) extends VenueKernel

case class VenueDetailResponse(venue: VenueDetail)


// User Details
// TODO: find which of these can be merged; which items are optional
case class UserContact(phone: Option[String], email: Option[String], twitter: Option[String], facebook: Option[String])
case class UserBadgesCount(count: Int)
case class UserMayorshipsSummary(count: Int, items: List[VenueCompact]) // items sometimes returned as empty list here

case class UserCheckinsSummary(count: Int, items: List[CheckinForFriend]) // This is only ever the most recent checkin

case class FriendGroupCompact(`type`: String, name: String, count: Int, items: List[UserCompact]) // mutual friends vs. other friends for self/friends/following/others
case class FriendsOthersCompactView(count: Int, groups: List[FriendGroupCompact])

case class UserFollowersCount(count: Int)
case class UserFollowingCount(count: Int)
case class UserRequestsCount(count: Int)
case class UserTipsCount(count: Int)
case class UserTodosCount(count: Int)
case class UserScores(recent: Int, max: Int, goal: Option[Int], checkinsCount: Int)
case class UserDetail(id: String, firstName: String, lastName: Option[String], photo: String,
                      gender: String, homeCity: String, relationship: Option[String],
                      `type`: String, pings: Option[Boolean], contact: UserContact,
                      badges: UserBadgesCount, mayorships: UserMayorshipsSummary,
                      checkins: UserCheckinsSummary, friends: FriendsOthersCompactView,
                      followers: Option[UserFollowersCount], following: Option[UserFollowingCount],
                      requests: Option[UserRequestsCount],
                      tips: UserTipsCount, todos: UserTodosCount, scores: UserScores) extends UserKernel
case class UserDetailResponse(user: UserDetail)

case class UserCompact(id: String, firstName: String, lastName: Option[String], photo: String,
                          gender: String, homeCity: String, relationship: Option[String]) extends UserKernel

case class UserCore(id: String, firstName: String, lastName: Option[String], photo: String,
                          gender: String, homeCity: String, relationship: Option[String],
                          `type`: Option[String]) extends UserKernel

case class UserRequestResponse(requests: List[UserCompact])

// TODO: not my favorite solution for polymorphic return data.  Will think on this.
trait Primitive {}
case class IntPrimitive(v: Int) extends Primitive
case class DoublePrimitive(v: Double) extends Primitive
case class StringPrimitive(v: String) extends Primitive
case class BooleanPrimitive(v: Boolean) extends Primitive
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
  def id: Option[String]
  def name: String
  def pluralName: String
  def icon: String
}
case class VenueCategoryCore(id: Option[String], name: String, pluralName: String, icon: String) extends VenueCategoryKernel
case class VenueCategoryCompact(id: Option[String], name: String, pluralName: String, icon: String, parents: List[String], primary: Option[Boolean]) extends VenueCategoryKernel
case class VenueCategoryWithChildren(id: Option[String], name: String, pluralName: String, icon: String, categories: List[VenueCategoryWithChildren]) extends VenueCategoryKernel

case class VenueCategoriesResponse(categories: List[VenueCategoryWithChildren])

case class UserPhotoUpdateResponse(user: UserDetail)

case class UserMayorshipsList(count: Int, items: List[VenueCompact])
case class UserMayorshipsResponse(mayorships: UserMayorshipsList)

case class UserTipsList(count: Int, items: List[TipForList])
case class UserTipsResponse(tips: UserTipsList)

case class UserCheckins(count: Int, items: List[CheckinForFriend])
case class UserCheckinsResponse(checkins: UserCheckins)

case class MentionEntity(indices: List[Int], `type`: String, user: Option[List[UserCompact]])

case class CheckinLocation(name: String, lat: Double, lng: Double)
case class CheckinForFriend(id: String,
                            createdAt: Long,
                            `type`: String,
                            `private`: Option[Boolean],
                            shout: Option[String],
                            isMayor: Option[Boolean],
                            timeZone: String,
                            entities: Option[List[MentionEntity]],
                            venue: Option[VenueCompact],
                            location: Option[CheckinLocation],
                            event: Option[CompactEvent],
                            photos: Option[PhotoList],
                            comments: Option[CommentList],
                            source: Option[OAuthSource])

case class Comment(id: String, createdAt: Long, user: UserCompact, text: String, entities: Option[List[MentionEntity]])
case class CommentList(count: Int, items: List[Comment])

case class CompactEvent(id: String, name: Option[String])

case class AddCheckinResponse(checkin: CheckinForFriend)

// User Updates:
case class UserSetPingsResponse(user: UserDetail)
case class UserUnfriendResponse(user: UserDetail)
case class UserFriendRequestResponse(user: UserDetail)
case class UserApproveFriendResponse(user: UserDetail)
case class UserDenyFriendshipResponse(user: UserDetail)

case class UserFriendsList(count: Int, summary: Option[String], items: List[UserCompact])
case class UserFriendsResponse(friends: UserFriendsList)

case class AllSettings(receivePings: Boolean,
                       receiveCommentPings: Boolean,
                       twitter: Option[String],
                       sendToTwitter: Boolean,
                       sendMayorshipsToTwitter: Boolean,
                       sendBadgesToTwitter: Boolean,
                       facebook: Option[Long],
                       sendToFacebook: Boolean,
                       sendMayorshipsToFacebook: Boolean,
                       sendBadgesToFacebook: Boolean,
                       foreignConsent: String)
case class AllSettingsResponse(settings: AllSettings)

case class LeaderboardItem(user: UserCompact, scores: UserScores, rank: Int)
case class Leaderboard(count: Int, items: List[LeaderboardItem])
case class LeaderboardResponse(leaderboard: Leaderboard)

case class BadgeImage(prefix: String, sizes: List[Int], name: String)
case class BadgeGroup(`type`: String, name: String, image: BadgeImage, items: List[String], groups: List[BadgeGroup])
case class BadgeSets(groups: List[BadgeGroup])
case class BadgeUnlocks(checkins: List[CheckinForFriend])
case class Badge(id: String, badgeId: String, name: String, description: Option[String], hint: Option[String],
                 image: BadgeImage , unlocks:List[BadgeUnlocks])
case class Badges(map: Map[String, Badge])
case class UserBadgesResponse(sets: BadgeSets, badges: Badges, defaultSetType: String)

// Tips
case class TodoStat(count: Int)
case class DoneStat(count: Int)

trait TipKernel {
  def id: String
  def createdAt: Long
  def itemId: String
  def text: String
  def url: Option[String]
  def status: Option[String]
  def photo: Option[PhotoCore]
  def photourl: Option[String]
}

trait TipStats {
  def todo: TodoStat
  def done: DoneStat
}

case class TipCore(id: String, createdAt: Long, itemId: String, text: String, url: Option[String], status: Option[String],
               photo: Option[PhotoCore], photourl: Option[String]) extends TipKernel
case class TipForList(id: String, createdAt: Long, itemId: String, text: String, url: Option[String], status: Option[String],
               photo: Option[PhotoCore], photourl: Option[String], todo: TodoStat, done: DoneStat,
               venue: Option[VenueCompact], user: Option[UserCompact]) extends TipKernel with TipStats
case class TipSearchResponse(tips: List[TipForList])

case class TipDetail(id: String, createdAt: Long, itemId: String, text: String, url: Option[String], status: Option[String],
               photo: Option[PhotoCore], photourl: Option[String], todo: TodoStat, done: DoneStat,
               venue: Option[VenueCompact], user: Option[UserCompact]) extends TipKernel with TipStats
case class TipDetailResponse(tip: TipDetail)


case class TodoListName(name: String)
case class TodoForList(id: String, createdAt: Long, list: Option[TodoListName], tip: Option[TipForList])

case class UserTodosList(count: Int, items: List[TodoForList])
case class UserTodosResponse(todos: UserTodosList)

// Photos

case class PhotoList(count: Int, items: List[PhotoForList])
case class PhotoDimension(url: String, width: Int, height: Int)
case class PhotoDimensionList(count: Int, items: List[PhotoDimension])
case class OAuthSource(name: String, url: String)
trait PhotoKernel {
  def id: String
  def createdAt: Long
  def url: String
  def sizes: PhotoDimensionList
  def source: Option[OAuthSource]
}
case class PhotoCore(id: String, createdAt: Long, url: String, sizes: PhotoDimensionList, source: Option[OAuthSource])
case class PhotoForList(id: String, createdAt: Long, url: String, sizes: PhotoDimensionList, source: Option[OAuthSource],
                        user: Option[UserCompact], visibility: String) extends PhotoKernel

case class PhotoDetail(id: String, createdAt: Long, url: String, sizes: PhotoDimensionList, source: Option[OAuthSource],
                        user: Option[UserCompact], venue: Option[VenueCompact], tip: Option[TipForList]) extends PhotoKernel


case class AddPhotoResponse(photo: PhotoForList)
case class PhotoDetailResponse(photo: PhotoDetail)


// Specials
case class SpecialRedemptionInteraction(prompt: String, waitingPrompt: Option[String], timeoutPrompt: Option[String],
                                        timeoutExplanation: Option[String], input: String, entryUrl: Option[String],
                                        purchaseUrl: Option[String], exitUrl: Option[String])

case class Special(id: String, `type`: String, message: String, description: Option[String],
                   finePrint: Option[String], unlocked: Option[Boolean],
                   icon: String, title: String, state: Option[String], progress: Option[Int], progressDescription:
                   Option[String], detail: Option[String], target: Option[Int], friendsHere: Option[List[UserCompact]],
                   provider: String, iconUrl: Option[String], redemption: String,
                   interaction: Option[SpecialRedemptionInteraction], venue: Option[VenueCore])

case class SpecialDetailResponse(special: Special)

case class SpecialList(count: Int, items: List[Special])
case class SpecialsSearchResponse(specials: SpecialList)


// Settings
case class SettingsDetailResponse(value: Primitive)
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
case class VenueAddResponse()
case class VenueExploreResponse()
case class VenueSearchResponse()
case class VenueTrendingResponse()
case class RecentCheckinsResponse()
case class AddTipResponse()
case class NotificationsResponse()


case class VenueHereNowResponse()
case class VenueTipsResponse()
case class VenuePhotosResponse()
case class VenueLinksResponse()

case class UserVenueHistoryResponse()
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

