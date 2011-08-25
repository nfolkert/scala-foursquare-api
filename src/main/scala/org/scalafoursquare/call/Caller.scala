package org.scalafoursquare.call

import net.liftweb.json.{DefaultFormats, JsonParser}
import net.liftweb.json.JsonAST.{JArray, JObject}
import net.liftweb.util.Helpers._
import org.scalafoursquare.response._
import scalaj.http.{HttpException, HttpOptions, Http}

class RawRequest(val app: App, val endpoint: String, val params: List[(String, String)]) {
  def getRaw: String = app.caller.makeCall(this, app.token)
}

class Request[T](app: App, endpoint: String, params: List[(String, String)] = Nil)(implicit mf: Manifest[T]) extends RawRequest(app, endpoint, params) {
  def get = app.convertSingle(getRaw)
}

class RawMultiRequest(app: App, reqA: Option[RawRequest], reqB: Option[RawRequest], reqC: Option[RawRequest],
                      reqD: Option[RawRequest], reqE: Option[RawRequest]) {
  def getRaw: String = {
    val subreqs = List(reqA, reqB, reqC, reqD, reqE).flatten
    val param = subreqs.map(r=>r.endpoint + (if (r.params.isEmpty) "" else "?" + r.params.map(p=>(p._1 + "=" + urlEncode(p._2))).join("&"))).join(",")
    new RawRequest(app, "multi", List(("requests", param))).getRaw
  }
}

class MultiRequest[A,B,C,D,E](app: App, reqA: Option[Request[A]], reqB: Option[Request[B]], reqC: Option[Request[C]],
                                   reqD: Option[Request[D]], reqE: Option[Request[E]])(implicit mfa: Manifest[A], mfb: Manifest[B], mfc: Manifest[C], mfd: Manifest[D], mfe: Manifest[E])
  extends RawMultiRequest(app, reqA, reqB, reqC, reqD, reqE) {
  def get = app.convertMulti(getRaw)
}

class RawMultiRequestList(val app: App, val subreqs: List[RawRequest]) {
  def getRaw: String = {
    val param = subreqs.map(r=>r.endpoint + (if (r.params.isEmpty) "" else "?" + r.params.map(p=>(p._1 + "=" + urlEncode(p._2))).join("&"))).join(",")
    new RawRequest(app, "multi", List(("requests", param))).getRaw
  }
}

class MultiRequestList[A](app: App, subreqs: List[Request[A]])(implicit mf: Manifest[A]) extends RawMultiRequestList(app, subreqs) {
  def get = app.convertMultiList(getRaw)
}

abstract class Caller {
  def makeCall(req: RawRequest, token: Option[String]): String
}

case class HttpCaller(clientId: String, clientSecret: String,
                      urlRoot: String = "https://api.foursquare.com/v2/",
                      version: String = "20110823") extends Caller {
  def makeCall(req: RawRequest, token: Option[String]): String = {
    val fullParams: List[(String, String)] = ("v", version) ::
      (token.map(t => List(("oauth_token", t))).getOrElse(List(("client_id", clientId), ("client_secret", clientSecret)))) ++
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

abstract class App(val caller: Caller) {
  object Formats extends DefaultFormats
  implicit val formats = Formats

  def token: Option[String]

  def convertSingle[T](raw: String)(implicit mf: Manifest[T]): Response[T] = {
    val json = JsonParser.parse(raw)

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

  def convertMulti[A,B,C,D,E](raw: String)(implicit mfa: Manifest[A], mfb: Manifest[B], mfc: Manifest[C], mfd: Manifest[D], mfe: Manifest[E]) = {
    val json = JsonParser.parse(raw)

    val fields = json.asInstanceOf[JObject].obj
    val meta = fields.find(_.name == "meta").map(_.extract[Meta]).get
    val notifications = fields.find(_.name == "notifications").map(_.extract[Notifications])
    val responses = {
      if (meta.code != 200)
        (None, None, None, None, None)
      else {
        val responses = fields.find(_.name == "response").get.value.asInstanceOf[JObject].obj.find(_.name == "responses").asInstanceOf[JArray]
        def response(idx: Int) = if (idx >= responses.arr.length) None else Some(responses.arr(idx))

        (response(0).map(_.extract[A]), response(1).map(_.extract[B]), response(2).map(_.extract[C]),
         response(3).map(_.extract[D]), response(4).map(_.extract[E]))
      }
    }
    MultiResponse[A,B,C,D,E](meta, notifications, responses)
  }

  def convertMultiList[A](raw: String)(implicit mf: Manifest[A]) = {
    val json = JsonParser.parse(raw)

    val fields = json.asInstanceOf[JObject].obj
    val meta = fields.find(_.name == "meta").map(_.extract[Meta]).get
    val notifications = fields.find(_.name == "notifications").map(_.extract[Notifications])
    val responses = {
      if (meta.code != 200)
        None
      else {
        val responses = fields.find(_.name == "response").get.value.asInstanceOf[JObject].obj.find(_.name == "responses").asInstanceOf[JArray]
        val resolved: List[A] = responses.arr.map(_.extract[A])
        Some(resolved)
      }
    }
    MultiResponseList[A](meta, notifications, responses)
  }
}

class UserlessApp(caller: Caller) extends App(caller) {
  def token: Option[String] = None

  // Userless Endpoints
  def venueCategories = new Request[VenueCategoriesResponse](this, "venues/categories")
  def venueDetail(id: String) = new Request[VenueDetailResponse](this, "venues/" + id)
  def tipDetail(id: String) = new Request[TipDetailResponse](this, "tips/" + id)
  def specialDetail(id: String, venue: String) = new Request[SpecialDetailResponse](this, "specials/" + id, ("venueId", venue) :: Nil)

  // Not sure if these can be userless; will move to AuthApp if not

  def multi[A](req: Request[A])(implicit mfa: Manifest[A]) = new MultiRequest(this, Some(req), None, None, None, None)
  def multi[A,B](reqA: Request[A], reqB: Request[B])(implicit mfa: Manifest[A], mfb: Manifest[B]) =
    new MultiRequest(this, Some(reqA), Some(reqB), None, None, None)
  def multi[A,B,C](reqA: Request[A], reqB: Request[B], reqC: Request[C])(implicit mfa: Manifest[A], mfb: Manifest[B], mfc: Manifest[C]) =
    new MultiRequest(this, Some(reqA), Some(reqB), Some(reqC), None, None)
  def multi[A,B,C,D](reqA: Request[A], reqB: Request[B], reqC: Request[C], reqD: Request[D])(implicit mfa: Manifest[A], mfb: Manifest[B], mfc: Manifest[C], mfd: Manifest[D]) =
    new MultiRequest(this, Some(reqA), Some(reqB), Some(reqC), Some(reqD), None)
  def multi[A,B,C,D,E](reqA: Request[A], reqB: Request[B], reqC: Request[C], reqD: Request[D], reqE: Request[E])(implicit mfa: Manifest[A], mfb: Manifest[B], mfc: Manifest[C], mfd: Manifest[D], mfe: Manifest[E]) =
    new MultiRequest(this, Some(reqA), Some(reqB), Some(reqC), Some(reqD), Some(reqE))
  def multi[A](reqs: List[Request[A]])(implicit mfa: Manifest[A]) = new MultiRequestList(this, reqs)
}

class AuthApp(caller: Caller, authToken: String) extends UserlessApp(caller) {
  override def token = Some(authToken)

  // Authenticated Endpoints
  def self = userDetail("self")
  def userDetail(id: String) = new Request[UserDetailResponse](this, "users/" + id)
  def updateDetail(id: String) = new Request[UserDetailResponse](this, "updates/" + id)
  def photoDetail(id: String) = new Request[PhotoDetailResponse](this, "photos/" + id)
  def settingsDetail(id: String) = new Request[SettingsDetailResponse](this, "settings/" + id)
  def checkinDetail(id: String, signature: Option[String] = None) =
    new Request[CheckinDetailResponse](this, "checkins/" + id, signature.map(s=>("signature", s)).toList)
}
