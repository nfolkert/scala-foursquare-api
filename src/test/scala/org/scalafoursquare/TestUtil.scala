package org.scalafoursquare

import net.liftweb.util.Props

object TestUtil {

  def propParams = {
    val USER_TOKEN = Props.get("access.token.user").open_!
    val CONSUMER_KEY = Props.get("consumer.key").open_!
    val CONSUMER_SECRET = Props.get("consumer.secret").open_!
    val FS_URL = Props.get("foursquare.url").open_!
    val TEST_URL = Props.get("foursquare.api.url").open_!
    val API_VERSION = Props.get("api.version").open_!
    val CALLBACK_URL = Props.get("consumer.callbackUrl").open_!

    AppTestParameters(TEST_URL, FS_URL, API_VERSION, CONSUMER_KEY, CONSUMER_SECRET, CALLBACK_URL, USER_TOKEN)
  }
}

case class AppTestParameters(TEST_URL: String,
                             FS_URL: String,
                             API_VERSION: String,
                             CONSUMER_KEY: String,
                             CONSUMER_SECRET: String,
                             CALLBACK_URL: String,
                             USER_TOKEN: String)
