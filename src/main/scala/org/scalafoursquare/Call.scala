package org.scalafoursquare

import scalaj.http.{HttpOptions, Http}
import net.liftweb.json.{DefaultFormats, JsonParser}
import net.liftweb.json.JsonAST.{JObject, JValue}
import net.liftweb.common.{Full, Empty, Box}

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

    // TODO: info logging -  println(http.getUrl.toString)
    http.asString
  }
}

case class FSApp(caller: FSCaller) {
  object Formats extends DefaultFormats
  implicit val formats = Formats

  def venueCategories = VenueCategoryFSCall(caller)

  abstract class FSCall[T](caller: FSCaller) {
    protected def getInternal(req: FSRequest)(implicit mf : scala.reflect.Manifest[T]): Response[T] = {
      val jsonStr = caller.makeCall(req)
      // TODO: debug logging - println(jsonStr)
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
}

case class VenueCategory(id: String, name: String, pluralName: String, icon: String, categories: List[VenueCategory])
case class VenueCategories(categories: List[VenueCategory])

case class Meta(code: Int, errorType: Option[String], errorDetail: Option[String])
case class Notification(unreadCount: Int)
case class Notifications(`type`: String, item: Notification)
case class Response[T](meta: Meta, notifications: Option[Notifications], response: T)
