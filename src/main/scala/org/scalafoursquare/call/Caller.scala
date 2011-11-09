package org.scalafoursquare.call

import net.liftweb.json.{Formats, JsonParser, Printer}
import net.liftweb.json.JsonAST
import net.liftweb.json.JsonDSL._
import net.liftweb.util.Helpers._
import org.scalafoursquare.response._
import scalaj.http.{HttpException, HttpOptions, Http, MultiPart}
import com.thoughtworks.paranamer.{BytecodeReadingParanamer, CachingParanamer, Paranamer}
import java.lang.reflect.{Type, ParameterizedType}
import net.liftweb.json.JsonAST.{JString, JValue, JArray, JObject, JField}

abstract class PostData {
  def asMultipart: List[MultiPart]
}

class PhotoData(fileName: String, data: Array[Byte]) extends PostData {
  def asMultipart: List[MultiPart] = {
    MultiPart("photo", fileName, "image/jpeg", data) :: Nil
  }
}
object PhotoData {
  def fromUrl(url: String) = {
    val bytes = Http(url).options(HttpOptions.connTimeout(500), HttpOptions.readTimeout(5000)).asBytes
    new PhotoData(url.substring(url.lastIndexOf("/")), bytes)
  }

  def fromFile(path: String) = {
    // Better way to read bytes from a file?  Just grabbed this from Http.asBytes.
    def fileToBytes(file: java.io.File): Array[Byte] = {
      val in = new java.io.FileInputStream(file)
      val bos = new java.io.ByteArrayOutputStream
      val ba = new Array[Byte](4096)

      def readOnce {
        val len = in.read(ba)
        if (len > 0) bos.write(ba, 0, len)
        if (len >= 0) readOnce
      }
      readOnce
      bos.toByteArray
    }
    val file = new java.io.File(path)
    val bytes = fileToBytes(file)
    new PhotoData(file.getName, bytes)
  }
}

case class CallFailed(msg: String, cause: Throwable) extends Exception(msg, cause)

case class ExtractionFailed(msg: String, cause: Throwable, json: JObject) extends Exception(msg, cause) {
  def pretty = Printer.pretty(JsonAST.render(json))
  def compact = Printer.compact(JsonAST.render(json))
}

case class ParseFailed(msg: String, cause: Throwable, raw: String) extends Exception(msg, cause) {
  def getJson: JObject = JsonParser.parse(raw).asInstanceOf[JObject]
  def pretty = Printer.pretty(JsonAST.render(getJson))
  def compact = Printer.pretty(JsonAST.render(getJson))
}

class RawRequest(val app: App, val endpoint: String, val params: List[(String, String)] = Nil, val method: String = "GET", val postData: Option[PostData]=None) {
  lazy val (getRaw, callDuration) = app.caller.makeCall(this, app.token, method, postData)
  def getJson: JObject = JsonParser.parse(getRaw).asInstanceOf[JObject]
}

class Request[T](app: App, endpoint: String, params: List[(String, String)] = Nil)(implicit mf: Manifest[T]) extends RawRequest(app, endpoint, params, "GET", None) {
  lazy val (get, extractDuration) = app.convertSingle[T](getRaw)
  def force = get.response.get // For testing
}

class PostRequest[T](app: App, endpoint: String, params: List[(String, String)] = Nil)(implicit mf: Manifest[T]) extends RawRequest(app, endpoint, params, "POST", None) {
  lazy val (get, extractionDuration): (Response[T], Long) = app.convertSingle[T](getRaw)
  def force = get.response.get // For testing
}

class PostDataRequest[T](app: App, endpoint: String, params: List[(String, String)]=Nil, postData: PostData)(implicit mf: Manifest[T]) extends RawRequest(app, endpoint, params, "POST", Some(postData)) {
  lazy val (get, extractionDuration): (Response[T], Long) = app.convertSingle[T](getRaw)
  def force = get.response.get // For testing
}

class RawMultiRequest(app: App, reqA: Option[RawRequest], reqB: Option[RawRequest], reqC: Option[RawRequest],
                      reqD: Option[RawRequest], reqE: Option[RawRequest], val method: String="GET") {
  lazy val (getRaw, callDuration): (String, Long) = {
    val subreqs = List(reqA, reqB, reqC, reqD, reqE).flatten
    val param = subreqs.map(r=>r.endpoint + (if (r.params.isEmpty) "" else "?" + r.params.map(p=>(p._1 + "=" + urlEncode(p._2))).join("&"))).join(",")
    val rawReq = new RawRequest(app, "/multi", List(("requests", param)), method, None)
    (rawReq.getRaw, rawReq.callDuration)
  }
  def getJson: JObject = JsonParser.parse(getRaw).asInstanceOf[JObject]
}

class MultiRequest[A,B,C,D,E](app: App, reqA: Option[Request[A]], reqB: Option[Request[B]], reqC: Option[Request[C]],
                                   reqD: Option[Request[D]], reqE: Option[Request[E]])(implicit mfa: Manifest[A], mfb: Manifest[B], mfc: Manifest[C], mfd: Manifest[D], mfe: Manifest[E])
  extends RawMultiRequest(app, reqA, reqB, reqC, reqD, reqE) {
  lazy val (get, extractDuration): (MultiResponse[A,B,C,D,E], Long) = app.convertMulti[A,B,C,D,E](getRaw)
  def f1 = get.responses._1.get
  def f2 = get.responses._2.get
  def f3 = get.responses._3.get
  def f4 = get.responses._4.get
  def f5 = get.responses._5.get
}

class RawMultiRequestList(val app: App, val subreqs: List[RawRequest], val method: String="GET") {
  lazy val (getRaw, callDuration): (String, Long) = {
    val param = subreqs.map(r=>r.endpoint + (if (r.params.isEmpty) "" else "?" + r.params.map(p=>(p._1 + "=" + urlEncode(p._2))).join("&"))).join(",")
    val rawReq = new RawRequest(app, "/multi", List(("requests", param)), method, None)
    (rawReq.getRaw, rawReq.callDuration)
  }
  def getJson: JObject = JsonParser.parse(getRaw).asInstanceOf[JObject]
}

class MultiRequestList[A](app: App, subreqs: List[Request[A]])(implicit mf: Manifest[A]) extends RawMultiRequestList(app, subreqs) {
  lazy val (get, extractionDuration): (MultiResponseList[A], Long) = app.convertMultiList[A](getRaw)
  def force = get.responses.get
}

abstract class Caller {
  def makeCall(req: RawRequest, token: Option[String]=None, method: String="GET", postData: Option[PostData]=None): (String, Long)
}

case class HttpCaller(clientId: String, clientSecret: String,
                      urlRoot: String = "https://api.foursquare.com/v2",
                      version: String = "20110823",
                      connectTimeout: Int=1000, readTimeout: Int=2000
                     ) extends Caller {
  import App.logger

  def makeCall(req: RawRequest, token: Option[String]=None, method: String="GET", postData: Option[PostData]=None): (String, Long) = {
    val fullParams: List[(String, String)] = ("v", version) ::
      (token.map(t => List(("oauth_token", t))).getOrElse(List(("client_id", clientId), ("client_secret", clientSecret)))) ++
      req.params.toList

    val url = urlRoot + req.endpoint
    val http = (method match {
      case "GET" => Http.get(url)
      case "POST" if postData.isEmpty => Http.post(url)
      case "POST" => Http.multipart(url, postData.get.asMultipart:_*)
      case _ => throw new Exception("Don't understand " + method)
    }).options(HttpOptions.connTimeout(connectTimeout), HttpOptions.readTimeout(readTimeout)).params(fullParams)

    val startTime = System.currentTimeMillis
    val result = try {
      http.asString
    } catch {
      case e: HttpException => {e.body}
    }

    val duration = System.currentTimeMillis - startTime
    logger.call(url + " " + method, duration)
    logger.debug(result)

    (result, duration)
  }
}

abstract class App(val caller: Caller) {
  implicit val formats = APICustomSerializers.formats
  import App.logger

  def token: Option[String]

  def p[T](key: String, value: T) = List((key, value.toString))
  def op[T](key: String, value: Option[T]) = value.map(v=>(key, v.toString)).toList

  private def parse(raw: String): JObject = {
    try {
      JsonParser.parse(raw).asInstanceOf[JObject]
    } catch {
      case e => {
        val failure = new ParseFailed("Failed to parse results from the server as Json", e, raw)
        throw failure
      }
    }
  }

  private def extract[T](json: JObject)(f: => T): T = {
    try {
      f
    } catch {
      case e => {
        val failure = new ExtractionFailed("Failed to extract results from Json as a scala object", e, json)
        throw e
      }
    }
  }


  def convertSingle[T](raw: String)(implicit mf: Manifest[T]): (Response[T], Long) = {
    val startTime = System.currentTimeMillis
    val json = parse(raw)
    val res = extract(json) {
      val fields = json.obj
      val meta = fields.find(_.name == "meta").map(_.extract[Meta]).get
      val notifications = fields.find(_.name == "notifications").map(_.value.asInstanceOf[JArray].arr.map(_.extract[NotificationItem]))
      val response = {
        if (meta.code != 200)
          None
        else {
          val full = fields.find(_.name == "response").get.value.asInstanceOf[JObject]
          App.extractSingleResponse[T](full)
        }
      }
      Response[T](meta, notifications, response)
    }
    val duration = System.currentTimeMillis - startTime
    logger.extract("Extraction", duration)

    (res, duration)
  }

  def convertMulti[A,B,C,D,E](raw: String)(implicit mfa: Manifest[A], mfb: Manifest[B], mfc: Manifest[C], mfd: Manifest[D], mfe: Manifest[E]) = {
    val startTime = System.currentTimeMillis
    val json = parse(raw)
    val res = extract(json) {
      val fields = json.obj
      val meta = fields.find(_.name == "meta").map(_.extract[Meta]).get
      val notifications = fields.find(_.name == "notifications").map(_.value.asInstanceOf[JArray].arr.map(_.extract[NotificationItem]))
      val responses: (Option[Response[A]], Option[Response[B]], Option[Response[C]], Option[Response[D]], Option[Response[E]]) = {
        if (meta.code != 200)
          (None, None, None, None, None)
        else {
          val parentResponse = fields.find(_.name == "response").get.value.asInstanceOf[JObject]
          App.extractMultiResponse[A,B,C,D,E](parentResponse)
        }
      }
      MultiResponse[A,B,C,D,E](meta, notifications, responses)
    }
    val duration = System.currentTimeMillis - startTime
    logger.extract("Extraction", duration)
    (res, duration)
  }

  def convertMultiList[A](raw: String)(implicit mf: Manifest[A]) = {
    val startTime = System.currentTimeMillis
    val json = parse(raw)
    val res = extract(json) {
      val fields = json.obj
      val meta = fields.find(_.name == "meta").map(_.extract[Meta]).get
      val notifications = fields.find(_.name == "notifications").map(_.value.asInstanceOf[JArray].arr.map(_.extract[NotificationItem]))
      val responses: Option[List[Response[A]]] = {
        if (meta.code != 200)
          None
        else {
          val parentResponse = fields.find(_.name == "response").get.value.asInstanceOf[JObject]
          App.extractMultiListResponse[A](parentResponse)
        }
      }
      MultiResponseList[A](meta, notifications, responses)
    }
    val duration = System.currentTimeMillis - startTime
    logger.extract("Extraction", duration)

    (res, duration)
  }
}

object App {
  def extractSingleResponse[A](obj: JObject)(implicit mf: Manifest[A], formats: Formats): Option[A] = {
    Some(obj.extract[A])
  }

  def extractMultiResponse[A,B,C,D,E](obj: JObject)(implicit mfa: Manifest[A], mfb: Manifest[B], mfc: Manifest[C], mfd: Manifest[D], mfe: Manifest[E], formats: Formats):
    (Option[Response[A]], Option[Response[B]], Option[Response[C]], Option[Response[D]], Option[Response[E]]) = {
    val responses = obj.obj.find(_.name == "responses").get.value.asInstanceOf[JArray]
    def response(idx: Int) = if (idx >= responses.arr.length) None else Some(responses.arr(idx))

    def convert[T](idx: Int)(implicit mf: Manifest[T]): Option[Response[T]] = {
      response(idx).map(res=>{
        val sfields = res.asInstanceOf[JObject].obj
        val smeta = sfields.find(_.name == "meta").map(_.extract[Meta]).get
        val snotifications = sfields.find(_.name == "notifications").map(_.value.asInstanceOf[JArray].arr.map(_.extract[NotificationItem]))
        val sresponse = {
          if (smeta.code != 200)
            None
          else
            Some(sfields.find(_.name == "response").get.value.extract[T])
        }
        Response[T](smeta, snotifications, sresponse)
      })
    }

    (convert[A](0), convert[B](1), convert[C](2), convert[D](3), convert[E](4))
  }
  
  def extractMultiListResponse[A](obj: JObject)(implicit mf: Manifest[A], formats: Formats): Option[List[Response[A]]] = {
    val responses = obj.obj.find(_.name == "responses").get.value.asInstanceOf[JArray]
    val resolved: List[Response[A]] = responses.arr.map(res => {
      val sfields = res.asInstanceOf[JObject].obj
      val smeta = sfields.find(_.name == "meta").map(_.extract[Meta]).get
      val snotifications = sfields.find(_.name == "notifications").map(_.value.asInstanceOf[JArray].arr.map(_.extract[NotificationItem]))
      val sresponse = {
        if (smeta.code != 200)
          None
        else
          Some(sfields.find(_.name == "response").get.value.extract[A])
      }
      Response[A](smeta, snotifications, sresponse)
    })
    Some(resolved)
  }

  def deriveInterface(app: App): List[EndpointInterface] = {
    val paranamer = new CachingParanamer(new BytecodeReadingParanamer)

    def generateReturnTree(t: Type, visited: List[Type]): JValue = {
      t match {
        case pt: ParameterizedType if classOf[Option[_]].isAssignableFrom(pt.getRawType.asInstanceOf[Class[_]]) => {
          ("optional" -> generateReturnTree(pt.getActualTypeArguments()(0), visited))
        }
        case pt: ParameterizedType if classOf[List[_]].isAssignableFrom(pt.getRawType.asInstanceOf[Class[_]]) => {
          JArray(List(generateReturnTree(pt.getActualTypeArguments()(0), visited)))
        }
        case x if x == classOf[String] => JString("string");
        case x if x == classOf[Boolean] => JString("boolean"); case x if x == classOf[java.lang.Boolean] => JString("boolean");
        case x if x == classOf[Int] => JString("integer"); case x if x == classOf[java.lang.Integer] => JString("integer");
        case x if x == classOf[Long] => JString("integer"); case x if x == classOf[java.lang.Long] => JString("integer");
        case x if x == classOf[Double] => JString("decimal"); case x if x == classOf[java.lang.Double] => JString("decimal");
        case c: Class[_] => {
          if (visited.contains(c))
            JString("RECURSIVE")
          else c match {
            case c if c == classOf[Primitive] => JString("primitive (string, boolean, integer, or decimal)")
            case c if c == classOf[UpdateTarget] => ("type" -> "update type") ~ ("object" -> "update object (polymorphic)")
            case c if c == classOf[UserSearchUnmatched] => ("match type" -> "e.g., twitter") ~ ("unmatched values" -> List(generateReturnTree(classOf[Primitive], visited)))
            case c if c == classOf[VenueDetail] => generateReturnTree(classOf[VenueCore], visited).asInstanceOf[JObject] ~ generateReturnTree(classOf[VenueDetailExtended], visited).asInstanceOf[JObject]
            case c if c == classOf[SpecialConfigurationDetail] => generateReturnTree(classOf[SpecialConfigurationDetail1], visited).asInstanceOf[JObject] ~ generateReturnTree(classOf[SpecialConfigurationDetail2], visited).asInstanceOf[JObject]
            case c if c == classOf[NotificationItem] => ("notificationType (varies)" -> "notification object (polymorphic)")
            case c if c == classOf[Badges] => ("badgeId (multiple)" -> generateReturnTree(classOf[Badge], visited))
            case c => {
              val cons = c.getConstructors()(0)
              val pTypes = cons.getGenericParameterTypes.toList
              val pNames = paranamer.lookupParameterNames(cons).toList
              val params: List[(String, Type)] = pNames.zip(pTypes)

              JObject(params.map(p=>{JField(p._1, generateReturnTree(p._2, c :: visited))}))
            }
          }
        }
        case e => {println("WTF?: " + e); JObject(Nil)}
      }
    }


    val allMethods = app.getClass.getDeclaredMethods.toList
    val requestMethods = allMethods.filter(_.getReturnType.isAssignableFrom(classOf[RawRequest]))
    allMethods.flatMap(m=>{
      val returnType = m.getReturnType
      if (classOf[RawRequest].isAssignableFrom(returnType)) {
        val pNames: List[String] = paranamer.lookupParameterNames(m).toList
        val pTypes: List[Type] = m.getGenericParameterTypes.toList

        val params: List[(String, Type)] = pNames.zip(pTypes)

        def fullArgs: List[AnyRef] = params.map(p=>{getDefault(p._2, p._1, true).asInstanceOf[AnyRef]})

        def emptyArgs: List[AnyRef] = params.map(p=>{getDefault(p._2, p._1, false).asInstanceOf[AnyRef]})

        def getDefault(t: java.lang.reflect.Type, pname: String, full: Boolean): Any = {
          t match {
            case pt: ParameterizedType if classOf[List[_]].isAssignableFrom(pt.getRawType.asInstanceOf[Class[_]]) => {
              if (full) {
                val ta = pt.getActualTypeArguments()(0)
                val d = getDefault(ta, pname, full)
                List(d, d)
              }
              else
                Nil
            }
            case pt: ParameterizedType if classOf[Option[_]].isAssignableFrom(pt.getRawType.asInstanceOf[Class[_]]) => {
              if (full) {
                val ta = pt.getActualTypeArguments()(0)
                val d = getDefault(ta, pname, full)
                Some(d)
              }
              else
                None
            }
            case pt: ParameterizedType if classOf[Product].isAssignableFrom(pt.getRawType.asInstanceOf[Class[_]]) => {
              val ta = pt.getActualTypeArguments()(0)
              val d = getDefault(ta, pname, full)
              pt.getRawType match {
                case c if c == classOf[Tuple2[_,_]] => (d, d)
                case c if c == classOf[Tuple3[_,_,_]] => (d, d, d)
                case _ => null
              }
            }
            case x if x == classOf[String] => pname;
            case x if x == classOf[Boolean] => false; case x if x == classOf[java.lang.Boolean] => false;
            case x if x == classOf[Int] => 0; case x if x == classOf[java.lang.Integer] => 0;
            case x if x == classOf[Long] => 0L; case x if x == classOf[java.lang.Long] => 0L;
            case x if x == classOf[Double] => 0.0; case x if x == classOf[java.lang.Double] => 0.0;
            case _ => null
          }
        }

        val fullInstance = m.invoke(app, fullArgs:_*).asInstanceOf[RawRequest]
        val emptyInstance = m.invoke(app, emptyArgs:_*).asInstanceOf[RawRequest]
        val reqKeys = emptyInstance.params.toList.map(_._1).toSet

        val reqParams = fullInstance.params.toList.filter(p => reqKeys.contains(p._1))
        val optParams = fullInstance.params.toList.filter(p => !reqKeys.contains(p._1))

        val endpoint = fullInstance.endpoint

        val isGet = classOf[Request[_]].isAssignableFrom(returnType)
        val isPost = classOf[PostRequest[_]].isAssignableFrom(returnType)
        val isPostData = classOf[PostDataRequest[_]].isAssignableFrom(returnType)

        val callType = if (isGet) "GET" else if (isPost) "POST" else "POST (with data)"

        val rt = m.getGenericReturnType.asInstanceOf[ParameterizedType].getActualTypeArguments()(0)
        val tree = generateReturnTree(rt, Nil)
        
        Some(EndpointInterface(endpoint, callType, reqParams, optParams, tree))
      }
      else
        None
    })
  }

  trait CallLogger {
    def call(msg: => String, timeMillis: Long): Unit
    def extract(msg: => String, timeMillis: Long): Unit
    def debugCallResult(msg: => String): Unit

    def trace(msg: => String): Unit
    def debug(msg: => String): Unit
    def info(msg: => String): Unit
    def warn(msg: => String): Unit
    def error(msg: => String): Unit
  }

  class DefaultCallLogger extends CallLogger {
    def call(msg: => String, timeMillis: Long) {}
    def extract(msg: => String, timeMillis: Long) {}
    def debugCallResult(msg: => String) {}
    def trace(msg: => String) {}
    def debug(msg: => String) {}
    def info(msg: => String) {}
    def warn(msg: => String) {}
    def error(msg: => String) {}
  }

  object NoopCallLogger extends DefaultCallLogger

  var logger: CallLogger = NoopCallLogger
}

case class EndpointInterface(endpoint: String, requestType: String,
                             requiredQueryParams: List[(String, String)],
                             optionalQueryParams: List[(String, String)],
                             returnTree: JValue) {
  def pretty {
    println("Endpoint: " + endpoint + " " + requestType)

    if (!requiredQueryParams.isEmpty) {
      println("Required:")
      requiredQueryParams.map(p=>println("\t" + p._1 + ", e.g.:\"" + p._2 + "\""))
    }

    if (!optionalQueryParams.isEmpty) {
      println("Optional:")
      optionalQueryParams.map(p=>println("\t" + p._1 + ", e.g.: \"" + p._2 + "\""))
    }

    println("Response Structure: ")
    println(Printer.pretty(JsonAST.render(returnTree)))
    println("\n")
  }
}

class UserlessApp(caller: Caller) extends App(caller) {
  def token: Option[String] = None

  // Userless Endpoints
  def venueCategories = new Request[VenueCategoriesResponse](this, "/venues/categories")
  def venueDetail(id: String) = new Request[VenueDetailResponse](this, "/venues/" + id)
  def tipDetail(id: String) = new Request[TipDetailResponse](this, "/tips/" + id)
  def specialDetail(id: String, venue: String) = new Request[SpecialDetailResponse](this, "/specials/" + id, p("venueId", venue))

  // Can pass in USER_ID/tips, USER_ID/todos, or USER_ID/dones
  // def listDetail(id: String) = new Request[ListDetailResponse](this, "/lists/" + id)

  def venueHereNow(id: String, limit: Option[Int]=None, offset: Option[Int]=None, afterTimestamp: Option[Long]=None) =
    new Request[VenueHereNowResponse](this, "/venues/" + id + "/herenow",
      op("limit", limit) ++
      op("offset", offset) ++
      op("afterTimestamp", afterTimestamp)
    )

  // sort =  recent, popular
  def venueTips(id: String, sort: Option[String]=None, limit: Option[Int]=None, offset: Option[Int]=None) =
    new Request[VenueTipsResponse](this, "/venues/" + id + "/tips",
      op("sort", sort) ++
      op("limit", limit) ++
      op("offset", offset)
    )

  // group: checkin, venue, multi
  def venuePhotos(id: String, group: String, limit: Option[Int]=None, offset: Option[Int]=None) =
    new Request[VenuePhotosResponse](this, "/venues/" + id + "/photos",
      p("group", group) ++
      op("limit", limit) ++
      op("offset", offset)
    )

  def venueLinks(id: String) = new Request[VenueLinksResponse](this, "/venues/" + id  + "/links")

  // section = food, drinks, coffee, shops, arts, outdoors
  // intent = specials, ?
  // novelty = new, old
  def exploreVenues(lat: Double, long: Double, llAcc: Option[Double]=None, alt: Option[Double]=None,
                    altAcc: Option[Double]=None, radius: Option[Int]=None, section: Option[String]=None,
                    query: Option[String]=None, limit: Option[Int]=None, intent: Option[String]=None,
                    novelty: Option[String]=None) =
    new Request[VenueExploreResponse](this, "/venues/explore",
      p("ll", lat + "," + long) ++
      op("llAcc", llAcc) ++
      op("alt", alt) ++
      op("altAcc", altAcc) ++
      op("radius", radius) ++
      op("section", section) ++
      op("query", query) ++
      op("limit", limit) ++
      op("intent", intent) ++
      op("novelty", novelty)
    )

  // intent = checkin, match, specials
  def venueSearch(lat: Double, long: Double, llAcc: Option[Double]=None, alt: Option[Double]=None, altAcc: Option[Double]=None,
                  query: Option[String]=None, limit: Option[Int]=None, intent: Option[String]=None,
                  categoryId: Option[String]=None, url: Option[String]=None, providerId: Option[String]=None,
                  linkedId: Option[String]=None) =
    new Request[VenueSearchResponse](this, "/venues/search",
      p("ll", lat + "," + long) ++
      op("llAcc", llAcc) ++
      op("alt", alt) ++
      op("altAcc", altAcc) ++
      op("query", query) ++
      op("limit", limit) ++
      op("intent", intent) ++
      op("categoryId", categoryId) ++
      op("url", url) ++
      op("providerId", providerId) ++
      op("linkedId", linkedId)
      // No radius?
    )

  def venueTrending(lat: Double, long: Double, limit: Option[Int]=None, radius: Option[Int]=None) =
    new Request[VenueTrendingResponse](this, "/venues/trending",
      p("ll", lat + "," + long) ++
      op("limit", limit) ++
      op("radius", radius)
    )

  // filter = friends, ?
  def tipsSearch(lat: Double, long: Double, limit: Option[Int]=None, offset: Option[Int]=None, filter: Option[String]=None,
                 query: Option[String]=None) =
    new Request[TipSearchResponse](this, "/tips/search",
      p("ll", lat + "," + long) ++
      op("limit", limit) ++
      op("offset", offset) ++
      op("filter", filter) ++
      op("query", query)
    )

  def specialsSearch(lat: Double, long: Double, llAcc: Option[Double]=None, alt: Option[Double]=None,
                     altAcc: Option[Double]=None, limit: Option[Int]=None) =
    new Request[SpecialsSearchResponse](this, "/specials/search",
      p("ll", lat + "," + long) ++
      op("llAcc", llAcc) ++
      op("alt", alt) ++
      op("altAcc", altAcc) ++
      op("limit", limit)
    )

  // Not sure if these can be userless; will move to AuthApp if not

  val dummy: Option[Request[Any]] = None // Type system does not like None by itself

  def multi[A](req: Request[A])(implicit mfa: Manifest[A]) = new MultiRequest(this, Some(req), None, None, None, None)
  def multi[A,B](reqA: Request[A], reqB: Request[B])(implicit mfa: Manifest[A], mfb: Manifest[B]) =
    new MultiRequest(this, Some(reqA), Some(reqB), dummy, dummy, dummy)
  def multi[A,B,C](reqA: Request[A], reqB: Request[B], reqC: Request[C])(implicit mfa: Manifest[A], mfb: Manifest[B], mfc: Manifest[C]) =
    new MultiRequest(this, Some(reqA), Some(reqB), Some(reqC), dummy, dummy)
  def multi[A,B,C,D](reqA: Request[A], reqB: Request[B], reqC: Request[C], reqD: Request[D])(implicit mfa: Manifest[A], mfb: Manifest[B], mfc: Manifest[C], mfd: Manifest[D]) =
    new MultiRequest(this, Some(reqA), Some(reqB), Some(reqC), Some(reqD), dummy)
  def multi[A,B,C,D,E](reqA: Request[A], reqB: Request[B], reqC: Request[C], reqD: Request[D], reqE: Request[E])(implicit mfa: Manifest[A], mfb: Manifest[B], mfc: Manifest[C], mfd: Manifest[D], mfe: Manifest[E]) =
    new MultiRequest(this, Some(reqA), Some(reqB), Some(reqC), Some(reqD), Some(reqE))
  def multi[A](reqs: List[Request[A]])(implicit mfa: Manifest[A]) = new MultiRequestList(this, reqs)
}

class AuthApp(caller: Caller, authToken: String) extends UserlessApp(caller) {
  override def token = Some(authToken)

  // Authenticated Endpoints
  def self = userDetail("self")
  def userDetail(id: String) = new Request[UserDetailResponse](this, "/users/" + id)
  def updateDetail(id: String) = new Request[UserDetailResponse](this, "/updates/" + id)
  def photoDetail(id: String) = new Request[PhotoDetailResponse](this, "/photos/" + id)
  def settingsDetail(id: String) = new Request[SettingsDetailResponse](this, "/settings/" + id)
  def checkinDetail(id: String, signature: Option[String] = None) =
    new Request[CheckinDetailResponse](this, "/checkins/" + id, op("signature", signature))

  def leaderboard(neighbors: Option[Int] = None) = new Request[LeaderboardResponse](this, "/users/leaderboard", op("neighbors", neighbors))

  def userSearch(phone: Option[List[String]]=None, email: Option[List[String]]=None, twitter: Option[List[String]]=None,
                 twitterSource: Option[String]=None, fbid: Option[List[String]]=None, name: Option[String]=None) =
    new Request[UserSearchResponse](this, "/users/search",
      op("phone", phone.map(_.join(","))) ++
      op("email", email.map(_.join(","))) ++
      op("twitter", twitter.map(_.join(","))) ++
      op("twitterSource", twitterSource) ++
      op("fbid", fbid.map(_.join(","))) ++
      op("name", name)
    )

  def userRequests = new Request[UserRequestResponse](this, "/users/requests")

  def addVenue(name: String, lat: Double, long: Double, address: Option[String]=None, crossStreet: Option[String]=None,
               city: Option[String]=None, state: Option[String]=None, zip: Option[String]=None,
               phone: Option[String]=None, twitter: Option[String]=None, primaryCategoryId: Option[String]=None) =
    new PostRequest[VenueAddResponse](this, "/venues/add",
      p("name", name) ++
      op("address", address) ++
      op("crossStreet", crossStreet) ++
      op("city", city) ++
      op("state", state) ++
      op("zip", zip) ++
      op("phone", phone) ++
      op("twitter", twitter) ++
      p("ll", lat + "," + long) ++
      op("primaryCategoryId", primaryCategoryId)
    )

  // broaddcast = private, public, facebook, twitter, followers
  def addCheckin(venueId: Option[String]=None, venue: Option[String]=None, shout: Option[String]=None,
                 broadcast: Option[List[String]]=None, ll: Option[(Double, Double)]=None, llAcc: Option[Double]=None,
                 alt: Option[Double]=None, altAcc: Option[Double]=None) =
    new PostRequest[AddCheckinResponse](this, "/checkins/add",
      op("venueId", venueId) ++
      op("venue", venue) ++
      op("shout", shout) ++
      op("broadcast", broadcast.map(_.join(","))) ++
      op("ll", ll.map(p=>p._1 + "," + p._2)) ++
      op("llAcc", llAcc) ++
      op("alt", alt) ++
      op("altAcc", altAcc)
    )

  def recentCheckins(ll: Option[(Double, Double)]=None, limit: Option[Int]=None, afterTimestamp: Option[Long]=None) =
    new Request[RecentCheckinsResponse](this, "/checkins/recent",
      op("ll", ll.map(p=>p._1 + "," + p._2)) ++
      op("limit", limit) ++
      op("afterTimestamp", afterTimestamp)
    )

  // broadcast = twitter, facebook
  def addTip(venueId: String, text: String, url: Option[String]=None, broadcast: Option[List[String]]=None) =
    new Request[AddTipResponse](this, "/tips/add",
      p("venueId", venueId) ++
      p("text", text) ++
      op("url", url) ++
      op("broadcast", broadcast.map(_.join(",")))
    )

  def notifications(limit: Option[Int]=None, offset: Option[Int]=None) =
    new Request[NotificationsResponse](this, "/updates/notifications",
      op("limit", limit) ++
      op("offset", offset)
    )

  // broadcast: twitter, facebook (todo: convenience method for addCheckinPhoto vs. addTipPhoto, etc.)
  def addPhoto(data: PhotoData, checkinId: Option[String]=None, tipId: Option[String]=None, venueId: Option[String]=None,
               broadcast: Option[List[String]]=None, `public`: Option[Boolean]=None, ll: Option[(Double, Double)]=None,
               llAcc: Option[Double]=None, alt: Option[Double]=None, altAcc: Option[Double]=None) =
    new PostDataRequest[AddPhotoResponse](this, "/photos/add",
      op("checkinId", checkinId) ++
      op("tipId", tipId) ++
      op("venueId", venueId) ++
      op("broadcast", broadcast.map(_.join(","))) ++
      op("public", `public`.map(b=> if (b) 1 else 0)) ++
      op("ll", ll.map(p=>p._1 + "," + p._2)) ++
      op("llAcc", llAcc) ++
      op("alt", alt) ++
      op("altAcc", altAcc), data
    )

  /*
  def addList(name: String, description: Option[String]=None, collaborative: Option[Boolean]=None, photoId: Option[String]=None) =
    new Request[AddListResponse](this, "/lists/add",
      p("name", name) ++
      op("description", description) ++
      op("collaborative", collaborative) ++
      op("photoId", photoId))

  def addListItem(id: String, venueId: Option[String]=None, text: Option[String]=None, url: Option[String]=None,
                  tipId: Option[String]=None, listId: Option[String]=None, itemId: Option[String]=None) =
    new Request[AddListItemResponse](this, "/lists/" + id + "/additem",
      op("venueId", venueId) ++
      op("text", text) ++
      op("url", url) ++
      op("tipId", tipId) ++
      op("listId", listId) ++
      op("itemId", itemId))
   */

  def allSettings = new Request[AllSettingsResponse](this, "/settings/all")

  def selfBadges = userBadges("self")
  def userBadges(id: String) = new Request[UserBadgesResponse](this, "/users/" + id + "/badges")

  def selfCheckins(limit: Option[Int]=None, offset: Option[Int]=None, afterTimestamp: Option[Long]=None,
                   beforeTimestamp: Option[Long]=None) = {
    val id = "self" // Only self is supported
    new Request[UserCheckinsResponse](this, "/users/" + id + "/checkins",
      op("limit", limit) ++
      op("offset", offset) ++
      op("afterTimestamp", afterTimestamp) ++
      op("beforeTimestamp", beforeTimestamp)
    )
  }
  // TODO: userCheckins, if supported

  def selfFriends(limit: Option[Int]=None, offset: Option[Int]=None) = userFriends("self", limit, offset)
  def userFriends(id: String, limit: Option[Int]=None, offset: Option[Int]=None) =
    new Request[UserFriendsResponse](this, "/users/" + id + "/friends",
      op("limit", limit) ++
      op("offset", offset)
    )

  def selfMayorships = userMayorships("self")
  def userMayorships(id: String) = new Request[UserMayorshipsResponse](this, "/users/" + id + "/mayorships")

  /*
  def selfLists(lat: Double, lng: Double) = userLists("self", lat, lng)
  def userLists(id: String, lat: Double, lng: Double) = new Request[UserListsResponse](this, "/users/" + id + "/lists",
    p("ll", lat + "," + lng))

  // Because their response structure is different, decided to split these out into separate calls, even though they hit the same endpoint
  def selfListGroup(group: String, lat: Double, lng: Double) = userListGroup("self", group, lat, lng)
  def userListGroup(id: String, group: String, lat: Double, lng: Double) = new Request[UserListGroupResponse](this, "/users/" + id + "/lists",
    p("group", group) ++
    p("ll", lat + "," + lng))
  */

  // sort = recent, nearby, popular
  def selfTips(sort: Option[String]=None, ll: Option[(Double, Double)]=None, limit: Option[Int]=None, offset: Option[Int]=None) =
    userTips("self", sort, ll, limit, offset)
  def userTips(id: String, sort: Option[String]=None, ll: Option[(Double, Double)]=None, limit: Option[Int]=None,
               offset: Option[Int]=None) =
    new Request[UserTipsResponse](this, "/users/" + id + "/tips",
      op("sort", sort) ++
      op("ll", ll.map(p=>p._1 + "," + p._2)) ++
      op("limit", limit) ++
      op("offset", offset)
    )

  // sort = nearby, recent
  // NOTE: either sort=recent or sort=nearby+ll is required.  Enforce this logically?
  def selfTodos(sort: Option[String]=None, ll: Option[(Double, Double)]=None) =
    userTodos("self", sort, ll)
  def userTodos(id: String, sort: Option[String]=None, ll: Option[(Double, Double)]=None) =
    new Request[UserTodosResponse](this, "/users/" + id + "/todos",
      op("sort", sort) ++
      op("ll", ll.map(p=>p._1 + "," + p._2))
    )

  def selfVenueHistory(afterTimestamp: Option[Long]=None, beforeTimestamp: Option[Long]=None, categoryId: Option[String]=None) = {
    val id = "self" // Only self is supported
    new Request[UserVenueHistoryResponse](this, "/users/" + id + "/venuehistory",
      op("afterTimestamp", afterTimestamp) ++
      op("beforeTimestamp", beforeTimestamp) ++
      op("categoryId", categoryId)
    )
  }
  // TODO: userVenueHistory, should it be supported

  def friendRequest(id: String) = new PostRequest[UserFriendRequestResponse](this, "/users/" + id + "/request")
  def unfriend(id: String) = new PostRequest[UserUnfriendResponse](this, "/users/" + id + "/unfriend")
  def approveFriendship(id: String) = new PostRequest[UserApproveFriendResponse](this, "/users/" + id + "/approve")
  def denyFriendship(id: String) = new PostRequest[UserDenyFriendshipResponse](this, "/users/" + id + "/deny")

  def setPings(id: String, value: Boolean) =
    new PostRequest[UserSetPingsResponse](this, "/users/" + id + "/setpings", p("value", value))

  // Settings options:
  // sendToTwitter, sendMayorshipsToTwitter, sendBadgesToTwitter, sendToFacebook, sendMayorshipsToFacebook, sendBadgesToFacebook, receivePings, receiveCommentPings
  def changeSetting(id: String, value: Boolean) = new PostRequest[ChangeSettingsResponse](this, "/settings/" + id + "/set",
    p("value", (if (value) 1 else 0)))

  // TODO: add other update parameters?
  def updateSelf(data: PhotoData) = new PostDataRequest[UserPhotoUpdateResponse](this, "/users/self/update", postData=data)

  def markVenueTodo(id: String, text: Option[String]=None) = new PostRequest[VenueMarkTodoResponse](this, "/venues/" + id + "/marktodo", op("text", text))

  // problem = mislocated, closed, duplicate
  def flagVenue(id: String, problem: String, venueId: Option[String]=None) =
    new PostRequest[VenueFlagResponse](this, "/venues/" + id + "/flag", p("problem", problem) ++ op("venueId", venueId))

  def editVenue(id: String, name: Option[String]=None, address: Option[String]=None, crossStreet: Option[String]=None,
                city: Option[String]=None, state: Option[String]=None, zip: Option[String]=None,
                phone: Option[String]=None, ll: Option[(Double, Double)]=None, categoryId: Option[List[String]]=None) =
    new PostRequest[VenueEditResponse](this, "/venues/" + id + "/edit",
      op("name", name) ++
      op("address", address) ++
      op("crossStreet", crossStreet) ++
      op("city", city) ++
      op("state", state) ++
      op("zip", zip) ++
      op("phone", phone) ++
      op("ll", ll.map(p=>p._1 + "," + p._2)) ++
      op("categoryId", categoryId.map(_.join(",")))
    )

  def proposeEditVenue(id: String, name: Option[String]=None, address: Option[String]=None, crossStreet: Option[String]=None,
                city: Option[String]=None, state: Option[String]=None, zip: Option[String]=None,
                phone: Option[String]=None, ll: Option[(Double, Double)]=None, primaryCategoryId: Option[String]=None) =
    new PostRequest[VenueProposeEditResponse](this, "/venues/" + id + "/proposeedit",
      op("name", name) ++
      op("address", address) ++
      op("crossStreet", crossStreet) ++
      op("city", city) ++
      op("state", state) ++
      op("zip", zip) ++
      op("phone", phone) ++
      op("ll", ll.map(p=>p._1 + "," + p._2)) ++
      op("primaryCategoryId", primaryCategoryId)
    )

  /*
  def venueListed(id: String) =
    new Request[VenueListedResponse](this, "/venues/" + id + "/listed")
  // group = created, edited, followed, friends, other
  def venueListedGroup(id: String, group: String) =
    new Request[VenueListedGroupResponse](this, "/venues/" + id + "/listed", p("group", group))

  def tipListed(id: String) =
    new Request[TipListedResponse](this, "/tips/" + id + "/listed")
  def tipListedGroup(id: String, group: String) =
    new Request[TipListedGroupResponse](this, "/tips/" + id + "/listed", p("group", group))
  */

  def addCheckinComment(id: String, text: String) = new PostRequest[CheckinAddCommentResponse](this, "/checkins/" + id + "/addcomment", p("text", text))

  def deleteCheckinComment(id: String, commentId: String) =
    new PostRequest[CheckinDeleteCommentResponse](this, "/checkins/" + id + "/deletecomment", p("commentId", commentId))

  def markTipTodo(id: String) = new PostRequest[TipMarkTodoResponse](this, "/tips/" + id + "/marktodo")
  def markTipDone(id: String) = new PostRequest[TipMarkDoneResponse](this, "/tips/" + id + "/markdone")
  def unmarkTip(id: String) = new PostRequest[TipUnmarkResponse](this, "/tips/" + id + "/unmark")

  def markNotificationsRead(highWatermark: Long) =
    new PostRequest[MarkNotificationsReadResponse](this, "/updates/marknotificationsread", p("highWatermark", highWatermark))

  // problem = not_redeemable, not_valuable, other
  def flagSpecial(id: String, venueId: String, problem: String, text: Option[String]=None) =
    new PostRequest[FlagSpecialResponse](this, "/specials/" + id + "/flag",
      p("venueId", venueId) ++
      p("problem", problem) ++
      op("text", text)
    )

  // MERCHANT API

  def venueGroupDetails(id: String) = new Request[VenueGroupDetailResponse](this, "/venuegroups/" + id)
  def campaignDetails(id: String) = new Request[CampaignDetailResponse](this, "/campaigns/" + id)

  def addCampaign(specialId: String, groupId: Option[List[String]]=None, venueId: Option[List[String]]=None,
                  startAt: Option[Long]=None, endAt: Option[Long]=None) =
    new PostRequest[AddCampaignResponse](this, "/campaigns/add",
      p("specialId", specialId) ++
      op("groupId", groupId.map(_.join(","))) ++
      op("venueId", venueId.map(_.join(","))) ++
      op("startAt", startAt) ++
      op("endAt", endAt)
    )

  // status = pending, active, expired, all
  def listCampaigns(specialId: Option[String]=None, groupId: Option[String]=None, status: Option[String]=None) =
    new Request[ListCampaignResponse](this, "/campaigns/list",
      op("specialId", specialId) ++
      op("groupId", groupId) ++
      op("status", status)
    )

  // type = mayor, frequency, count, regular, swarm, friends, flash
  def addSpecial(`type`: String, text: String, unlockedText: String, finePrint: Option[String]=None,
                 count1: Option[Int]=None, count2: Option[Int]=None, count3: Option[Int]=None) =
    new PostRequest[AddSpecialResponse](this, "/specials/add",
      p("text", text) ++
      p("unlockedText", unlockedText) ++
      op("finePrint", finePrint) ++
      op("count1", count1) ++
      op("count2", count2) ++
      op("count3", count3) ++
      p("type", `type`)
    )

  // status = pending, active, expired, all
  def listSpecials(venueId: Option[List[String]]=None, status: Option[String]=None) =
    new Request[ListSpecialResponse](this, "/specials/list",
      op("venueId", venueId.map(_.join(","))) ++
      op("status", status)
    )

  def addVenueGroup(name: String) =
    new PostRequest[AddVenueGroupResponse](this, "/venuegroups/add", p("name", name))

  def listVenueGroups() = new Request[ListVenueGroupResponse](this, "/venuegroups/list")


  def deleteVenueGroup(id: String) =
    new PostRequest[DeleteVenueGroupResponse](this, "/venuegroups/" + id + "/delete")

  def managedVenues() = new Request[ManagedVenuesResponse](this, "/venues/managed")

  def venuesTimeSeries(venueId: List[String], startAt: Long, endAt: Option[Long]=None) =
    new Request[VenuesTimeSeriesResponse](this, "/venues/timeseries",
      p("venueId", venueId.join(",")) ++
      p("startAt", startAt) ++
      op("endAt", endAt)
    )

  def venueStats(id: String, startAt: Option[Long]=None, endAt: Option[Long]=None) =
    new Request[VenueStatsResponse](this, "/venues/" + id + "/stats",
      op("startAt", startAt) ++
      op("endAt", endAt)
    )

  def specialConfigurationDetail(id: String) =
    new Request[SpecialConfigurationDetailResponse](this, "/specials/" + id + "/configuration")

  def campaignTimeSeries(id: String, startAt: Option[Long]=None, endAt: Option[Long]=None) =
    new Request[CampaignTimeSeriesResponse](this, "/campaigns/" + id + "/timeseries",
      op("startAt", startAt) ++
      op("endAt", endAt)
    )

  def startCampaign(id: String) =
    new PostRequest[StartCampaignResponse](this, "/campaigns/" + id + "/start")

  def endCampaign(id: String) =
    new PostRequest[EndCampaignResponse](this, "/campaigns/" + id + "/end")

  def deleteCampaign(id: String) =
    new PostRequest[DeleteCampaignResponse](this, "/campaigns/" + id + "/delete")

  def retireSpecial(id: String) =
    new PostRequest[RetireSpecialResponse](this, "/specials/" + id + "/retire")

  def addVenueToGroup(id: String, venueId: List[String]) =
    new PostRequest[AddVenueToGroupResponse](this, "/venuegroups/" + id + "/addvenue",
      p("venueId", venueId.join(","))
    )

  def removeVenueFromGroup(id: String, venueId: List[String]) =
    new PostRequest[RemoveVenueFromGroupResponse](this, "/venuegroups/" + id + "/removevenue",
      p("venueId", venueId.join(","))
    )
}
