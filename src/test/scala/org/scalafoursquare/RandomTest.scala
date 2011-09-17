package org.scalafoursquare

import net.liftweb.json.{JsonAST, Printer, Extraction, JsonParser}
import net.liftweb.util.Props
import org.scalafoursquare.call.{UserlessApp, AuthApp}
import org.scalafoursquare.response.{APICustomSerializers}
import org.specs.SpecsMatchers
import org.junit.{Test, Ignore}
import util.Random

class RandomTest extends SpecsMatchers {
  implicit val formats = APICustomSerializers.formats

  val P = TestUtil.propParams
  val E = EndpointTest

  def IGNORE = "IGNORE"
  def ROOT_IDS_STRING = Props.get("test.random.roots.ids").openOr(IGNORE)
  def NUM_VISITS = Props.get("test.random.numVisits").map(_.toInt).openOr(10)

  @Test
  def randomExplore() {
    if (ROOT_IDS_STRING != IGNORE) {
      val seed = {val t = System.currentTimeMillis; println("SEED: " + t); t}
      val r = new Random(seed)
      val roots = ROOT_IDS_STRING.split(',').toList.map(_.trim)

      val caller = TestUtil.httpCaller
      val app = new AuthApp(caller, P.USER_TOKEN)

      def popRandom[T](list: List[T]): (Option[T], List[T]) = {
        if (list.isEmpty) (None, Nil)
        else if (list.tail.isEmpty) (list.headOption, Nil)
        else {
          val idx = r.nextInt(list.length)
          (Some(list(idx)), list.slice(0, idx) ++ list.slice(idx+1, list.length))
        }
      }

      def takeRandom[T](list: List[T], maxNum: Int): List[T] = {
        if (list.isEmpty) Nil
        else if (list.tail.isEmpty) list
        else {
          (for (i <- 1 to maxNum) yield {
            list(r.nextInt(list.length))
          }).toList.distinct
        }
      }

      def doStep(inRoots: List[String], depth: Int, visited: Set[String]) {
        if (depth < NUM_VISITS && !inRoots.isEmpty) {
          val (next, lessRoots) = popRandom(inRoots)
          next.map(nextId => {
            println("Step: " + depth + " User: " + nextId)
            // println("Roots: " + lessRoots)

            val friends = E.test(app.userFriends(nextId, Some(500)))
            val friendIds: List[String] = friends.friends.items.map(_.id)

            E.test(app.userDetail(nextId))

            // Other stuff to look up: mayorship venue, etc.

            doStep(takeRandom((lessRoots ++ friendIds).distinct, 100), depth+1, visited + nextId)
          })
        }
      }

      doStep(roots, 0, Set[String]())
    }
  }

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