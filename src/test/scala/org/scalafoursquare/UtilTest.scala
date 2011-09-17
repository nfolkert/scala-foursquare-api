package org.scalafoursquare

import org.junit.{Test}
import org.specs.SpecsMatchers
import net.liftweb.json.{Printer, JsonAST, JsonParser}
import net.liftweb.json.JsonAST.JValue

class UtilTest extends SpecsMatchers {

  @Test
  def testJsonDiff() {
    val j1 = JsonParser.parse("""{"same":5, "old":3, "ch":2, "nest":{"same":1, "old":4, "ch":5}}""")
    val j2 = JsonParser.parse("""{"same":5, "new":3, "ch":4, "nest":{"same":1, "new":6, "ch":7}}""")

    def p(v: Option[JValue]) = v.map(jv=>Printer.compact(JsonAST.render(jv))).getOrElse("")
    def pp(v: Option[JValue]) = println(p(v))

    p(TestUtil.JsonDiff.minus(j1, j2)) must_== """{"old":3,"ch":2,"nest":{"old":4,"ch":5}}"""
    p(TestUtil.JsonDiff.minus(j2, j1)) must_== """{"new":3,"ch":4,"nest":{"new":6,"ch":7}}"""
    p(TestUtil.JsonDiff.intersect(j1, j2)) must_== """{"same":5,"nest":{"same":1}}"""
    p(TestUtil.JsonDiff.intersect(j2, j1)) must_== """{"same":5,"nest":{"same":1}}"""

    val j3 = JsonParser.parse("""{"same":1}""")
    p(TestUtil.JsonDiff.minus(j3, j3)) must_== ""
    p(TestUtil.JsonDiff.intersect(j3, j3)) must_== """{"same":1}"""
    val j4 = JsonParser.parse("""{"same":1, "new":2}""")
    p(TestUtil.JsonDiff.minus(j3,j4)) must_== ""

    val j5 = JsonParser.parse("""{"same":[1,2], "old":[3,4], "ch":[5,6], "nest":[[7,8], [9,10], [11,12]]}""")
    val j6 = JsonParser.parse("""{"same":[1,2], "new":[13,14], "ch":[15,6], "nest":[[7,8], [16,17], [11,18]]}""")
    p(TestUtil.JsonDiff.minus(j5, j6)) must_== """{"old":[3,4],"ch":[5],"nest":[[9,10],[12]]}"""
    p(TestUtil.JsonDiff.minus(j6, j5)) must_== """{"new":[13,14],"ch":[15],"nest":[[16,17],[18]]}"""
    p(TestUtil.JsonDiff.intersect(j5, j6)) must_== """{"same":[1,2],"ch":[6],"nest":[[7,8],[11]]}"""
    p(TestUtil.JsonDiff.intersect(j6, j5)) must_== """{"same":[1,2],"ch":[6],"nest":[[7,8],[11]]}"""

    val j7 = JsonParser.parse("""{"nest":{"same":[1,2], "old":[3,4], "ch":[5,6]}}""")
    val j8 = JsonParser.parse("""{"nest":{"same":[1,2], "new":[7,8], "ch":[9,6]}}""")
    p(TestUtil.JsonDiff.minus(j7, j8)) must_== """{"nest":{"old":[3,4],"ch":[5]}}"""
    p(TestUtil.JsonDiff.minus(j8, j7)) must_== """{"nest":{"new":[7,8],"ch":[9]}}"""
    p(TestUtil.JsonDiff.intersect(j7, j8)) must_== """{"nest":{"same":[1,2],"ch":[6]}}"""
    p(TestUtil.JsonDiff.intersect(j8, j7)) must_== """{"nest":{"same":[1,2],"ch":[6]}}"""

    val j9 = JsonParser.parse("""{"nest":[{"same":[1,2]}, {"old":[3,4]}, {"ch":[5,6]}],"nest2":[{"same":[1,2]}]}""")
    val j10 = JsonParser.parse("""{"nest":[{"same":[1,2]}, {"new":[7,8]}, {"ch":[9,6]}],"nest2":[{"same":[1,2]}]}""")
    p(TestUtil.JsonDiff.minus(j9, j10)) must_== """{"nest":[{"old":[3,4]},{"ch":[5]}]}"""
    p(TestUtil.JsonDiff.minus(j10, j9)) must_== """{"nest":[{"new":[7,8]},{"ch":[9]}]}"""
    p(TestUtil.JsonDiff.intersect(j9, j10)) must_== """{"nest":[{"same":[1,2]},{"ch":[6]}],"nest2":[{"same":[1,2]}]}"""
    p(TestUtil.JsonDiff.intersect(j10, j9)) must_== """{"nest":[{"same":[1,2]},{"ch":[6]}],"nest2":[{"same":[1,2]}]}"""

  }
}
