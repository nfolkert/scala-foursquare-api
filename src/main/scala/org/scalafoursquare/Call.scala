package org.scalafoursquare

import scalaj.http.{HttpOptions, Http}
import net.liftweb.json.{DefaultFormats, JsonParser}
import net.liftweb.json.JsonAST.{JObject, JValue}
import net.liftweb.common.{Loggable, Full, Empty, Box}

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

    http.asString
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
      val response = fields.find(_.name == "response").get.value.extract[T]
      Response[T](meta, notifications, response)
    }

    def get: Response[T]
  }

  case class VenueCategoryFSCall(caller: FSCaller) extends FSCall[VenueCategories](caller) {
    def get: Response[VenueCategories] = {
      getInternal(FSRequest("venues/categories"))
    }
  }

  case class VenueDetailFSCall(caller: FSCaller, id: String) extends FSCall[VenueDetail](caller) {
    def get: Response[VenueDetail] = {
      getInternal(FSRequest("venues/" + id))
    }
  }

  case class UserDetailFSCall(caller: FSCaller, id: String, token: String) extends FSCall[UserDetail](caller) {
    def get: Response[UserDetail] = {
      getInternal(FSRequest("users/" + id, Full(token)))
    }
  }
}

case class VenueDetail()



// User Details
// TODO: find which of these can be merged; which items are optional
case class UserContact(phone: String, email: String, twitter: String, facebook: String)
case class UserBadges(count: Int)
case class UserMayorships(count: Int /*, items: List[String]*/)
case class UserCheckinVenueContact(twitter: String)
case class UserCheckinVenueLocation(address: String, city: String, state: String, postalCode: String, lat: Double, lng: Double)
case class UserCheckinVenueCategory(id: String, name: String, pluralName: String, icon: String, parents: List[String], primary: Option[Boolean])
case class UserCheckinVenueStats(checkinsCount: Int, usersCount: Int, tipCount: Int)
case class UserCheckinVenue(id: String, name: String, itemId: String, contact: UserCheckinVenueContact, location: UserCheckinVenueLocation,
                            categories: List[UserCheckinVenueCategory], verified: Boolean, stats: UserCheckinVenueStats, url: Option[String])
case class UserCheckin(id: String, createdAt: Long, `type`: String, timeZone: String, venue: UserCheckinVenue)
case class UserCheckins(count: Int, items: List[UserCheckin])
case class UserFriendGroup(`type`: String, name: String, count: Int /*, items: List[String] */)
case class UserFriends(count: Int, groups: List[UserFriendGroup])
case class UserFollowing(count: Int)
case class UserRequests(count: Int)
case class UserTips(count: Int)
case class UserTodos(count: Int)
case class UserScores(recent: Int, max: Int, checkinsCount: Int)
case class UserDetailData(id: String, firstName: String, lastName: String, photo: String,
                          gender: String, homeCity: String, relationship: String, `type`: String,
                          pings: Boolean, contact: UserContact, badges: UserBadges,
                          mayorships: UserMayorships, checkins: UserCheckins, friends: UserFriends,
                          following: UserFollowing, requests: UserRequests, tips: UserTips,
                          todos: UserTodos, scores: UserScores)
case class UserDetail(user: UserDetailData)

// Venue Categories
case class VenueCategory(id: String, name: String, pluralName: String, icon: String, categories: List[VenueCategory])
case class VenueCategories(categories: List[VenueCategory])

case class Meta(code: Int, errorType: Option[String], errorDetail: Option[String])
case class Notification(unreadCount: Int)
case class Notifications(`type`: String, item: Notification)
case class Response[T](meta: Meta, notifications: Option[Notifications], response: T)
