package org.scalafoursquare

import net.liftweb.common.Box
import scalaj.http.{HttpOptions, Http}

//

case class App(client_id: String, client_secret: String, version: String = "20110823", url: String = "https://api.foursquare.com/v2/") {
  def call(token: String) = Call(token, this)
}

case class Call (token: String, app: App) {
  def venueCategories = VenueCategoriesCall(this)
}

case class VenueCategoriesCall(call: Call) {
  def get() = {
    val http = Http.get(call.app.url + "venues/categories").options(HttpOptions.connTimeout(1000), HttpOptions.readTimeout(1000))
      .param("version", call.app.version)
      .param("oauth_token", call.token)
    http.asString
  }
}

/*case class SearchVenues(ll: (Double, Double),
                        query: Option[String] = None,
                        limit: Int = 10,
                        intent: Option[String] = Some("checkin"),
                        categoryId
                         call: Call) {
  
}*/

case class VenueCategories()