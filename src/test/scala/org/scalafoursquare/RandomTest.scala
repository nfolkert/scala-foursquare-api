package org.scalafoursquare

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
      // 1314248366804L
    }
    val r = new Random(seed)

    val caller = HttpFSCaller(CONSUMER_KEY, CONSUMER_SECRET, TEST_URL, API_VERSION)
    val app = FSApp(caller)
    for (i <- 1 to 10) {
      val venueId = r.nextInt(100000)
      val venue = app.venueDetail(venueId.toString)
      println(venue.toString)

      if (venue.meta.code == 200) {
        val rawVenue = app.getRaw(FSRequest("venues/" + venueId))
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

    val caller = HttpFSCaller(CONSUMER_KEY, CONSUMER_SECRET, TEST_URL, API_VERSION)
    val userApp = FSApp(caller).user(USER_TOKEN)
    for (i <- 1 to 10) {
      val userId = r.nextInt(100000)
      val user = userApp.userDetail(userId.toString)
      println(user.toString)

      if (user.meta.code == 200) {
        val rawUser = userApp.getRaw(FSRequest("users/" + userId))
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