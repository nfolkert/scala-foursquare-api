package org.scalafoursquare

import org.scalafoursquare.response._
import org.specs.SpecsMatchers
import org.junit.Test
import net.liftweb.json.{JsonAST, Printer, Extraction, JsonParser}

class ExtractionTest extends SpecsMatchers {
  implicit val formats = APICustomSerializers.formats

  def testExtraction[T](jsonStr: String, checkMatch: Boolean = true)(implicit mf: Manifest[T]): T = {
    val json = JsonParser.parse(jsonStr)
    val extracted = json.extract[T]
    val extJson = Extraction.decompose(extracted)

    val unparsed = Printer.compact(JsonAST.render(extJson))
    val original = Printer.compact(JsonAST.render(json))

    println(mf.erasure.getName)
    println(unparsed)
    println(original)
    println()

    if (checkMatch)
      unparsed == original must_== true
    extracted
  }

  @Test
  def leaderboardExtraction() {
    val jsonStr = """
    {"leaderboard":{
      "count": 2,
      "items":[
        {"user":{"id":"1",
                 "firstName":"Blue",
                 "lastName":"Ribbon",
                 "photo":"SOMEPICTURE1.jpg",
                 "gender":"male",
                 "homeCity":"Brooklyn, NY",
                 "relationship":"friend"},
         "scores":{"recent":150,"max":250,"checkinsCount":2000},
         "rank":1},
        {"user":{"id":"2",
                 "firstName":"Runner",
                 "lastName":"Up",
                 "photo":"SOMEPICTURE2.jpg",
                 "gender":"female",
                 "homeCity":"New York, NY",
                 "relationship":"self"},
         "scores":{"recent":100,"max":200,"checkinsCount":1000},
         "rank":1}
      ]
    }}
    """
    testExtraction[LeaderboardResponse](jsonStr)
  }

  @Test
  def userSearchExtraction() {
    val jsonStr = """
    {"results":[
       {"id":"33","firstName":"naveen","photo":"SOMEPICTURE3.jpg","gender":"male","homeCity":"New York, NY","relationship":"friend"}
     ],
     "unmatched":{"twitter":[]}}
    """
    val testAdditional = testExtraction[UserSearchResponse](jsonStr)
  }

  @Test
  def userRequestExtraction() {
    val jsonStr = """
      {"requests":[
        {"id":"3","firstName":"Friend","lastName":"Requested","photo":"SOMEPICTURE4.jpg","gender":"male","homeCity":"San Antonio, TX","relationship":"pendingMe"}
      ]}
    """
    testExtraction[UserRequestResponse](jsonStr)
  }

  @Test
  def userBadges() {
    val jsonStr = """
      {"sets":{
         "groups":[
           {"type":"all","name":"all badges","image":{"prefix":"https://foursquare.com/img/badge/","sizes":[24,32,48,64],"name":"/allbadges.png"},
            "items":["badgeAid","badgeBid"],
            "groups":[]},
           {"type":"partner","name":"partner","image":{"prefix":"https://foursquare.com/img/badge/","sizes":[24,32,48,64],"name":"/partner.png"},
            "items":["badge1id","badge2id"],
            "groups":[
              {"type":"zagat","name":"ZAGAT","image":{"prefix":"https://foursquare.com/img/badge/","sizes":[24,32,48,64],"name":"/.png"},
               "items":["badge1id"],
               "groups":[]},
              {"type":"bravotv","name":"Bravo","image":{"prefix":"https://foursquare.com/img/badge/","sizes":[24,32,48,64],"name":"/.png"},
               "items":["badge2id","badge3id"],
               "groups":[]},
            ]}]},
       "badges":{"badge1id":{
                   "id":"badge1id","badgeId":"badgeXid","name":"Barista",
                   "description":"Congrats - you've checked in at 5 different Starbucks! Be sure to pick up a double tall latte for your friend - I'm sure they'd do the same for you.",
                   "image":{"prefix":"https://playfoursquare.s3.amazonaws.com/badge/","sizes":[57,114,200,300,400],"name":"/barista.png"},
                   "unlocks":[
                     {"checkins":[{
                       "id":"c1id","createdAt":1000,"type":"checkin","timeZone":"America/Los_Angeles",
                         "venue":{"id":"v1id","name":"Starbucks","itemId":"v1id",
                           "contact":{"phone":"4155522649","formattedPhone":"(415) 552-2649","twitter":"starbucks"},
                           "location":{"address":"2727 Mariposa St","crossStreet":"at Bryant St","city":"San Francisco","state":"CA","postalCode":"94110","country":"USA","lat":37.7630136,"lng":-122.4105095},
                           "categories":[{"id":"4bf58dd8d48988d1e0931735","name":"Coffee Shop","pluralName":"Coffee Shops","icon":"https://foursquare.com/img/categories/food/coffeeshop.png","parents":["Food"],"primary":true}],
                           "verified":true,"stats":{"checkinsCount":1482,"usersCount":493,"tipCount":13}}}]}]},
                 "badge2id":{
                   "id":"badge2id","badgeId":"badgeYid","name":"Super Swarm",
                   "description":"50 person foursquare swarms are soooo 2009.  We upped it to 250 for the Super Swarm and you still nailed it.  Well played!",
                   "image":{"prefix":"https://playfoursquare.s3.amazonaws.com/badge/","sizes":[57,114,200,300,400],"name":"/super_swarm.png"},
                   "unlocks":[
                     {"checkins":[{
                       "id":"c2id","createdAt":2000,"type":"checkin","timeZone":"America/New_York",
                       "venue":{"id":"v2id","name":"Heatpocalypse NYC","itemId":"v2id",
                         "contact":{},
                         "location":{"address":"Moving Target!","city":"New York","state":"NY","country":"USA","lat":40.73688466553769,"lng":-73.99626731872559},
                         "categories":[
                           {"id":"4bf58dd8d48988d162941735","name":"Other Great Outdoors","pluralName":"Other Great Outdoors","icon":"https://foursquare.com/img/categories/parks_outdoors/default.png","parents":["Great Outdoors"],"primary":true},
                           {"id":"4d4b7105d754a06377d81259","name":"Great Outdoors","pluralName":"Great Outdoors","icon":"https://foursquare.com/img/categories/parks_outdoors/default.png","parents":[]},
                           {"id":"4bf58dd8d48988d165941735","name":"Scenic Lookout","pluralName":"Scenic Lookouts","icon":"https://foursquare.com/img/categories/parks_outdoors/default.png","parents":["Great Outdoors"]}],
                         "verified":false,"stats":{"checkinsCount":25598,"usersCount":15731,"tipCount":154}}}]}]}
                },
       "defaultSetType":"4sq"}
    """
    testExtraction[UserBadgesResponse](jsonStr)
  }
  
}
