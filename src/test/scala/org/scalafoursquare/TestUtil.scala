package org.scalafoursquare

import org.scalafoursquare.call.{HttpCaller}
import net.liftweb.util.Props
import net.liftweb.json.JsonAST.{JDouble, JInt, JBool, JString, JValue, JArray, JField, JObject, JNull}

object TestUtil {

  def httpCaller: HttpCaller = {
    val P = TestUtil.propParams
    HttpCaller(P.CONSUMER_KEY, P.CONSUMER_SECRET, P.TEST_URL, P.API_VERSION, P.CONNECT_TIMEOUT, P.READ_TIMEOUT)
  }

  def propParams = {
    val USER_TOKEN = Props.get("access.token.user").open_!
    val CONSUMER_KEY = Props.get("consumer.key").open_!
    val CONSUMER_SECRET = Props.get("consumer.secret").open_!
    val FS_URL = Props.get("foursquare.url").open_!
    val TEST_URL = Props.get("foursquare.api.url").open_!
    val API_VERSION = Props.get("api.version").open_!
    val CALLBACK_URL = Props.get("consumer.callbackUrl").open_!
    val CONNECT_TIMEOUT = Props.get("connect.timeout").open_!.toInt
    val READ_TIMEOUT = Props.get("read.timeout").open_!.toInt

    AppTestParameters(TEST_URL, FS_URL, API_VERSION, CONSUMER_KEY, CONSUMER_SECRET, CALLBACK_URL, USER_TOKEN,
                      CONNECT_TIMEOUT, READ_TIMEOUT)
  }

  object JsonDiff {

    def compare(left: JValue, right: JValue): (Option[JValue], Option[JValue], Option[JValue]) = {
      (intersect(left, right), minus(left, right), minus(right, left))
    }

    def intersect(left: JValue, right: JValue): Option[JValue] = {
      (left, right) match {
        case (l: JObject, r: JObject) => intersectObj(l, r)
        case (l: JArray, r: JArray) => intersectArr(l, r)
        case (l: JField, r: JField) => intersect(l.value, r.value).map(v=>JField(l.name, v))
        case (JBool(l), JBool(r)) if (l == r) => Some(JBool(l))
        case (JInt(l), JInt(r)) if (l == r) => Some(JInt(l))
        case (JString(l), JString(r)) if (l == r) => Some(JString(l))
        case (JDouble(l), JDouble(r)) if (l == r) => Some(JDouble(l))
        case (JNull, JNull) => Some(JNull)
        case (_, _) => None
      }
    }
    
    def intersectObj(left: JObject, right: JObject): Option[JObject] = {
      val rmap: Map[String, JField] = right.obj.map(f=>(f.name, f)).toMap
      val fields: List[JField] = left.obj.flatMap(f=>{
        rmap.get(f.name).map(rf=>{
          intersect(f.value, rf.value).map(v=>JField(f.name, v))
        }).getOrElse(None)
      })
      if (!fields.isEmpty)
        Some(JObject(fields))
      else
        None
    }

    def intersectArr(left: JArray, right: JArray): Option[JArray] = {
      val zip: List[(JValue, JValue)] = left.arr.zip(right.arr)
      val ret = zip.flatMap(p=>{intersect(p._1, p._2)})
      if (ret.isEmpty) None else Some(JArray(ret))
    }

    def minus(left: JValue, right: JValue): Option[JValue] = {
      (left, right) match {
        case (l: JObject, r: JObject) => minusObj(l, r)
        case (l: JArray, r: JArray) => minusArr(l, r)
        case (l: JField, r: JField) => minus(l.value, r.value).map(v=>JField(l.name, v))
        case (JBool(l), JBool(r)) if (l == r) => None
        case (JInt(l), JInt(r)) if (l == r) => None
        case (JString(l), JString(r)) if (l == r) => None
        case (JDouble(l), JDouble(r)) if (l == r) => None
        case (JNull, JNull) => None
        case (l, _) => Some(l)
      }
    }

    def minusObj(left: JObject, right: JObject): Option[JObject] = {
      val rmap: Map[String, JField] = right.obj.map(f=>(f.name, f)).toMap
      val fields: List[JField] = left.obj.flatMap(f=>{
        rmap.get(f.name).map(rf=>{
          minus(f.value, rf.value).map(v=>JField(f.name, v))
        }).getOrElse(Some(f))
      })
      if (!fields.isEmpty)
        Some(JObject(fields))
      else
        None
    }

    def minusArr(left: JArray, right: JArray): Option[JArray] = {
      val zip: List[(JValue, Option[JValue])] = (for {i <- 0 to left.arr.length-1} yield {
        val l = left.arr(i)
        val ro = if (i < right.arr.length) Some(right.arr(i)) else None
        (l, ro)
      }).toList
      val ret = zip.flatMap(p=>{
        p._2.map(r=>minus(p._1, r)).getOrElse(Some(p._1))
      })
      if (ret.isEmpty) None else Some(JArray(ret))
    }
  }

}

case class AppTestParameters(TEST_URL: String,
                             FS_URL: String,
                             API_VERSION: String,
                             CONSUMER_KEY: String,
                             CONSUMER_SECRET: String,
                             CALLBACK_URL: String,
                             USER_TOKEN: String,
                             CONNECT_TIMEOUT: Int,
                             READ_TIMEOUT: Int)
