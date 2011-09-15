package org.scalafoursquare

import org.scalafoursquare.call.{UserlessApp, Request, ParseFailed, ExtractionFailed}
import org.scalafoursquare.response._
import org.specs.SpecsMatchers
import org.junit.{Test, Ignore}
import net.liftweb.json.{Printer, Extraction}
import net.liftweb.json.JsonAST
import net.liftweb.json.JsonAST.{JValue, JObject, JNothing}
import net.liftweb.util.Props

object EndpointTest {
  implicit val formats = APICustomSerializers.formats

  def pretty(v: JValue) = {
    if (v == JNothing) "" else Printer.pretty(JsonAST.render(v))
  }

  def compare(unextracted: JValue, extracted: JValue): Boolean = {
    val (intersection, missed, extra) = TestUtil.JsonDiff.compare(unextracted, extracted)
    if (missed.isDefined || extra.isDefined) {
      println("Original:\n" + pretty(unextracted))
      println("Extracted:\n" + pretty(extracted))
      intersection.map(j=>println("Same:\n" + pretty(j)))
      missed.map(j=>println("Missed:\n" + pretty(j)))
      extra.map(j=>println("Extra:\n" + pretty(j)))
      false
    } else true
  }

  def test[T](req: Request[T])(implicit mf: Manifest[T]): T = {
    try {
      val raw = req.getRaw
      println(raw)

      val callDuration = req.callDuration
      println("Call took " + callDuration + " ms")
      
      val json = req.getJson

      val ret = req.get
      val extractDuration = req.extractDuration
      println("Extraction took " + extractDuration + " ms")

      val meta = ret.meta
      val notifications = ret.notifications
      val response = ret.response

      val metaJson = json.obj.find(_.name == "meta").map(_.value).getOrElse(JNothing)
      val notificationsJson = json.obj.find(_.name == "notifications").map(_.value).getOrElse(JNothing)
      val responseJson = json.obj.find(_.name == "response").map(_.value).getOrElse(JNothing)

      val metaDecomposed = Extraction.decompose(meta)
      val notificationDecomp = notifications.map(n=>Extraction.decompose(n)).getOrElse(JNothing)
      val responseDecomp = response.map(r=>Extraction.decompose(r)).getOrElse(JObject(Nil))

      val c1 = compare(metaJson, metaDecomposed)
      val c2 = compare(notificationsJson, notificationDecomp)
      val c3 = compare(responseJson, responseDecomp)

      if (!c1 || !c2 || !c3)
        throw new Exception("Comparison failure")

      if (meta.code != 200) {
        println("Meta.code was not 200: \n" + pretty(json))
        throw new Exception("Meta.code was not 200")
      }
      response.get
    } catch {
      case e: ParseFailed => {println("Parse failure: " + e.raw); throw e}
      case e: ExtractionFailed => {println("Extraction failure: " + e.pretty); throw e}
      case e => {println("Other Failure: " + e); throw e}
    }
  }
}

class UserlessEndpointTest extends SpecsMatchers {

  val P = TestUtil.propParams
  val E = EndpointTest

  val caller = TestUtil.httpCaller
  val app = new UserlessApp(caller)

  def IGNORE = "IGNORE"
  def VENUE_ID = Props.get("test.venue1.id").openOr(IGNORE)
  def ANOTHER_VENUE_ID = Props.get("test.venue2.id").openOr(IGNORE)
  def TIP_ID = Props.get("test.tip.id").openOr(IGNORE)
  def SPECIAL_ID = Props.get("test.special.id").openOr(IGNORE)

  @Test
  def venueCategories() {
    E.test(app.venueCategories)
  }

  @Test
  def venueDetail() {
    if (VENUE_ID != IGNORE)
      E.test(app.venueDetail(VENUE_ID))
  }

  @Test
  def tipDetail() {
    if (TIP_ID != IGNORE)
      E.test(app.tipDetail(TIP_ID))
  }

  @Test
  def specialDetail() {
    if (SPECIAL_ID != IGNORE && VENUE_ID != IGNORE)
      E.test(app.specialDetail(SPECIAL_ID, VENUE_ID))
  }

  @Test
  def venueHereNow() {
    if (VENUE_ID != IGNORE)
      E.test(app.venueHereNow(VENUE_ID))
  }

  @Test
  def venueTips() {
    if (VENUE_ID != IGNORE)
      E.test(app.venueTips(VENUE_ID))
  }

  @Test
  def venuePhotos() {
    if (VENUE_ID != IGNORE)
      E.test(app.venuePhotos(VENUE_ID, "venue"))
  }

  @Test
  def venueLinks() {
    if (VENUE_ID != IGNORE)
      E.test(app.venueLinks(VENUE_ID))
  }

  @Test
  def exploreVenues() {
    E.test(app.exploreVenues(40.6748, -73.9721))
  }

  @Test
  def venueSearch() {
    E.test(app.venueSearch(40.6748, -73.9721))
  }

  @Test
  def venueTrending() {
    E.test(app.venueTrending(40.6748, -73.9721))
  }

  @Test
  def tipsSearch() {
    E.test(app.tipsSearch(40.6748, -73.9721))
  }

  @Test
  def specialsSearch() {
    E.test(app.specialsSearch(40.6748, -73.9721))
  }

  // TODO: Do more detailed comparison (a la E.test) for multi responses

  @Test
  def multi() {
    if (VENUE_ID != IGNORE && ANOTHER_VENUE_ID != IGNORE) {
      val mult = app.multi(app.venueCategories, app.venueDetail(VENUE_ID), app.tipsSearch(40.6748, -73.9721), app.venueTips(ANOTHER_VENUE_ID)).get
      println(mult)
    }
  }

  @Test
  def multiList() {
    if (VENUE_ID != IGNORE && ANOTHER_VENUE_ID != IGNORE) {
      val mult = app.multi(List(app.venueDetail(VENUE_ID), app.venueDetail(ANOTHER_VENUE_ID))).get
      println(mult)
    }
  }
}
