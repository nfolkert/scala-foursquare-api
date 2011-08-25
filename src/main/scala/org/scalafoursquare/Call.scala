package org.scalafoursquare

import net.liftweb.json.{DefaultFormats, JsonParser}
import net.liftweb.json.JsonAST.{JObject, JValue}
import net.liftweb.common.{Loggable, Full, Empty, Box}
import scalaj.http.{HttpException, HttpOptions, Http}

case class FSRequest(endpoint: String, token: Box[String] = Empty, params:List[(String, String)] = Nil) {

  def this(endpoint: String, params:(String, String)*) = this(endpoint, Empty, params.toList)
  def this(endpoint: String, token: String, params: (String, String)*) = this(endpoint, Full(token), params.toList)

}

abstract class FSCaller {
  def makeCall(req: FSRequest): String
}

case class HttpFSCaller(clientId: String, clientSecret: String,
                   urlRoot: String = "https://api.foursquare.com/v2/",
                   version: String = "20110823") extends FSCaller {
  def makeCall(req: FSRequest): String = {

    val fullParams: List[(String, String)] = ("v", version) ::
      (req.token.map(token => List(("oauth_token", token))).openOr(List(("client_id", clientId), ("client_secret", clientSecret)))) ++
      req.params.toList

    val http = Http.get(urlRoot + req.endpoint).options(HttpOptions.connTimeout(1000), HttpOptions.readTimeout(1000))
      .params(fullParams)

    println(http.getUrl.toString)

    try {
      http.asString
    } catch {
      case e: HttpException => {e.body}
    }
  }
}


case class FSApp(caller: FSCaller) {
  object Formats extends DefaultFormats
  implicit val formats = Formats

  // Userless Endpoints
  def venueCategories = VenueCategoryFSCall(caller)
  def venueDetail(id: String) = VenueDetailFSCall(caller, id)

  // Authenticated Endpoints
  def user(token: String) = FSUserApp(token)
  case class FSUserApp(token: String) {
    def self = UserDetailFSCall(caller, "self", token)
    def userDetail(id: String) = UserDetailFSCall(caller, id, token)
  }

  abstract class FSCall[T](caller: FSCaller) {
    protected def getInternal(req: FSRequest)(implicit mf : scala.reflect.Manifest[T]): Response[T] = {
      val jsonStr = caller.makeCall(req)

      println(jsonStr)

      val json = JsonParser.parse(jsonStr)

      val fields = json.asInstanceOf[JObject].obj
      val meta = fields.find(_.name == "meta").map(_.extract[Meta]).get
      val notifications = fields.find(_.name == "notifications").map(_.extract[Notifications])
      val response = {
        if (meta.code != 200)
          None
        else
          Some(fields.find(_.name == "response").get.value.extract[T])
      }
      Response[T](meta, notifications, response)
    }

    def get: Response[T]
  }

  case class VenueCategoryFSCall(caller: FSCaller) extends FSCall[VenueCategoriesResponse](caller) {
    def get: Response[VenueCategoriesResponse] = {
      getInternal(FSRequest("venues/categories"))
    }
  }

  case class VenueDetailFSCall(caller: FSCaller, id: String) extends FSCall[VenueDetailResponse](caller) {
    def get: Response[VenueDetailResponse] = {
      getInternal(FSRequest("venues/" + id))
    }
  }

  case class UserDetailFSCall(caller: FSCaller, id: String, token: String) extends FSCall[UserDetailResponse](caller) {
    def get: Response[UserDetailResponse] = {
      getInternal(FSRequest("users/" + id, Full(token)))
    }
  }
}

// Venue Details

case class VenueContact(phone: Option[String], formattedPhone: Option[String], twitter: Option[String])
case class VenueLocation(address: String, crossStreet: Option[String], city: String, state: String, postalCode: String, lat: Double, lng: Double)
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

case class UserCheckin(id: String, createdAt: Long, `type`: String, timeZone: String, venue: VenueCompact)
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
