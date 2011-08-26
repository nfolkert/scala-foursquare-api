package org.scalafoursquare

import net.liftweb.json.{JsonAST, Printer, Extraction, JsonParser}
import org.junit.{Ignore}
import org.scalafoursquare.call.{UserlessApp, AuthApp}
import org.scalafoursquare.response.{APICustomSerializers}
import org.specs.SpecsMatchers
import util.Random

class RandomTest extends SpecsMatchers {
  implicit val formats = APICustomSerializers.formats

  val P = TestUtil.propParams

  @Ignore
  def randomVenues() {
    val seed = {
      val time = System.currentTimeMillis
      println("RANDOM SEED: " + time)
      time
      // 1314248366804L 1314290178357L 1314290196887L 1314290227343L
    }
    val r = new Random(seed)

    val caller = TestUtil.httpCaller
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

    val caller = TestUtil.httpCaller
    val userApp = new AuthApp(caller, P.USER_TOKEN)
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