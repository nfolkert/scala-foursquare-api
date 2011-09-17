package org.scalafoursquare.auth

import scalaj.http.{HttpException, HttpOptions, Http}
import net.liftweb.json.JsonAST.JObject
import org.scalafoursquare.call.{ParseFailed, ExtractionFailed, CallFailed, App}
import net.liftweb.json.{DefaultFormats, JsonParser}

case class OAuthFlow(clientId: String, clientSecret: String, clientRedirectUri: String,
                     webUrlRoot: String = "https://foursquare.com", connectTimeout: Int = 1000, readTimeout: Int = 5000) {
  import App.logger

  implicit val formats = DefaultFormats

  def authorizeUrl =
    webUrlRoot + "/oauth2/authorize" +
    "?client_id=" + clientId +
    "&response_type=code" +
    "&redirect_uri=" + clientRedirectUri

  def authenticateUrl =
    webUrlRoot + "/oauth2/authenticate" +
    "?client_id=" + clientId +
    "&response_type=code" +
    "&redirect_uri=" + clientRedirectUri

  def accessTokenUrl(code: String) =
    webUrlRoot + "/oauth2/access_token" +
    "?client_id=" + clientId +
    "&client_secret=" + clientSecret +
    "&grant_type=authorization_code" +
    "&redirect_uri=" + clientRedirectUri +
    "&code=" + code

  def ajaxAuthenticateUrl =
    webUrlRoot + "/oauth2/authenticate" +
    "?client_id=" + clientId +
    "&response_type=token" +
    "&redirect_uri=" + clientRedirectUri

  case class AccessTokenCaller(code: String) {
    lazy val (getRaw, callDuration) = makeCall
    lazy val (get, extractDuration) = resolve(getRaw)

    private def makeCall: (String, Long) = {
      val url = accessTokenUrl(code)
      val http = Http.get(url).options(HttpOptions.connTimeout(connectTimeout), HttpOptions.readTimeout(readTimeout))

      val startTime = System.currentTimeMillis
      try {
        val res = http.asString

        val duration = System.currentTimeMillis - startTime
        logger.call(url, duration)
        logger.debug(res)

        (res, duration)
      } catch {
        case e => throw CallFailed("OAuth accessToken call failed", e)
      }
    }

    private def resolve(raw: String): (String, Long) = {
      val startTime = System.currentTimeMillis
      val jobj = try {
        JsonParser.parse(raw).asInstanceOf[JObject]
      } catch {
        case e => throw ParseFailed("Parsing access token response failed", e, raw)
      }

      try {
        val token = jobj.extract[AccessTokenResponse].access_token

        val duration = System.currentTimeMillis - startTime
        logger.extract("Extraction", duration)

        (token, duration)
      } catch {
        case e => throw ExtractionFailed("Access token extraction failed", e, jobj)
      }
    }

  }
  def accessTokenCaller(code: String) = AccessTokenCaller(code)

}

case class AccessTokenResponse(access_token: String)