package org.scalafoursquare

import net.liftweb.common.Box
import scalaj.http.{HttpOptions, Http}

//

case class Call (token: String, version: String = "20110823", url: String = "https://api.foursquare.com/v2/") {

}

case class VenueCategories(call: Call) {

  def makeCall() = {
    val http = Http.get(call.url + "venues/categories").options(HttpOptions.connTimeout(1000), HttpOptions.readTimeout(1000))
      .param("oauth_token", call.token)
    println(http.getUrl.toString)
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
