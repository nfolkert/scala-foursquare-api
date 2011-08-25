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
                        specials: List[String], hereNow: Option[VenueHereNowCompact], events: List[String]) extends VenueKernel

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

case class UserCheckin(id: String, createdAt: Long, `type`: String, timeZone: String, venue: Option[VenueCompact])
case class UserCheckins(count: Int, items: List[UserCheckin])
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

case class UserCore(id: String, firstName: String, lastName: Option[String], photo: String,
                          gender: String, homeCity: String, relationship: Option[String],
                          `type`: String) extends UserKernel

trait UserKernel {
  def id: String
  def firstName: String
  def lastName: Option[String]
  def photo: String
  def gender: String
  def homeCity: String
  def relationship: Option[String]
  def `type`: String
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

case class Meta(code: Int, errorType: Option[String], errorDetail: Option[String])
case class Notification(unreadCount: Int)
case class Notifications(`type`: String, item: Notification)
case class Response[T](meta: Meta, notifications: Option[Notifications], response: Option[T])

case class MultiResponse[A,B,C,D,E](meta: Meta, notifications: Option[Notifications], responses: (Option[Response[A]], Option[Response[B]], Option[Response[C]], Option[Response[D]], Option[Response[E]]))
case class MultiResponseList[A](meta: Meta, notifications: Option[Notifications], responses: Option[List[Response[A]]])

// TODO:
case class UpdateDetailResponse()
case class CheckinDetailResponse()
case class TipDetailResponse()
case class PhotoDetailResponse()
case class SpecialDetailResponse()
case class SettingsDetailResponse()
