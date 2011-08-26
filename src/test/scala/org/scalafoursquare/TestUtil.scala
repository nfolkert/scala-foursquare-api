package org.scalafoursquare

import org.scalafoursquare.call.{Caller, HttpCaller}
import net.liftweb.util.Props

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
