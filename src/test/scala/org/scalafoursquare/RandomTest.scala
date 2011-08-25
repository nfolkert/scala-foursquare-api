package org.scalafoursquare

import org.scalafoursquare.call.{HttpCaller, UserlessApp, AuthApp, Request, RawRequest}
import net.liftweb.common.Empty
import net.liftweb.util.Props
import org.specs.SpecsMatchers
import util.Random
import org.junit.{Ignore, Test}
import net.liftweb.json.{JsonAST, Printer, DefaultFormats, Extraction, JsonParser}

class RandomTest extends SpecsMatchers {
  object Formats extends DefaultFormats
  implicit val formats = Formats

  val USER_TOKEN = Props.get("access.token.user").open_!
  val CONSUMER_KEY = Props.get("consumer.key").open_!
  val CONSUMER_SECRET = Props.get("consumer.secret").open_!
  val TEST_URL = Props.get("foursquare.test.url").open_!
  val API_VERSION = Props.get("api.version").open_!

  @Ignore
  def randomVenues() {
    val seed = {
      val time = System.currentTimeMillis
      println("RANDOM SEED: " + time)
      time
      // 1314248366804L 1314290178357L 1314290196887L 1314290227343L
    }
    val r = new Random(seed)

    val caller = HttpCaller(CONSUMER_KEY, CONSUMER_SECRET, TEST_URL, API_VERSION)
    val app = new UserlessApp(caller)
    for (i <- 1 to 10) {
      val venueId = r.nextInt(100000)
      val venueReq = app.venueDetail(venueId.toString)
      val venue = venueReq.get
      println(venue.toString)

      if (venue.meta.code == 200) {
        val rawVenue = venueReq.getRaw
        val parsedRaw = JsonParser.parse(rawVenue)
        val unparsedRes = Extraction.decompose(venue)

        val parsed = Printer.compact(JsonAST.render(parsedRaw))
        val unparsed = Printer.compact(JsonAST.render(unparsedRes))
        println(parsed)
        println(unparsed)

        parsed must_== unparsed
      }
    }
  }

  @Ignore
  def randomUsers() {
    val seed = {
      val time = System.currentTimeMillis
      println("RANDOM SEED: " + time)
      time
    }
    val r = new Random(seed)

    val caller = new HttpCaller(CONSUMER_KEY, CONSUMER_SECRET, TEST_URL, API_VERSION)
    val userApp = new AuthApp(caller, USER_TOKEN)
    for (i <- 1 to 10) {
      val userId = r.nextInt(100000)
      val userReq = userApp.userDetail(userId.toString)
      val user = userReq.get
      println(user.toString)

      if (user.meta.code == 200) {
        val rawUser = userReq.getRaw
        val parsedRaw = JsonParser.parse(rawUser)
        val unparsedRes = Extraction.decompose(user)

        val parsed = Printer.compact(JsonAST.render(parsedRaw))
        val unparsed = Printer.compact(JsonAST.render(unparsedRes))
        println(parsed)
        println(unparsed)

        parsed must_== unparsed
      }
    }
  }
}