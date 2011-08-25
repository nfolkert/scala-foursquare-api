package org.scalafoursquare

object TestCaller extends FSCaller {
  def makeCall(req: FSRequest): String = {
    def m(jsonObj: String) = """{"meta":{"code":200},"response":""" + jsonObj + "}"
    req.endpoint match {
      case "venues/categories" => {
        m("""{"categories":[{"id":"fakeId","name":"Fake Category","pluralName":"Fake Categories","icon":"noIcon","categories":[]}]}""")
      }
      case "users/self" | "users/someUserId" => m("""
      {"user":{
        "id":"fakeId", "firstName":"Fake", "lastName":"User", "photo":"noPhoto", "gender":"male",
        "homeCity":"New York, NY", "relationship":"self", "type":"user", "pings":false,
        "contact":{"phone":"8675309", "email":"fakeEmail@foursquare.com", "twitter":"fakeTwitter", "facebook":"fakeFacebook"},
        "badges":{"count":10},
        "mayorships":{"count":20,"items":[]},
        "checkins":{"count":30,"items":[{"id":"fakeId","createdAt":1000,"type":"checkin","timeZone":"America/New_York",
          "venue":{"id":"fakeId","name":"Fake Venue","itemId":"fakeId",
            "contact":{"twitter":"fakeTwitter"},
            "location":{"address":"East Village","city":"New York","state":"NY","postalCode":"10003","lat":40,"lng":-73.5},
            "categories":[{"id":"fakeId","name":"Fake Category","pluralName":"Fake Categories","icon":"noIcon","parents":["Fake Parent","Fake Grandpa"],"primary":true}],
            "verified":true,"stats":{"checkinsCount":500,"usersCount":200,"tipCount":100},"url":"fakeUrl"}}]},
         "friends":{"count":60,"groups":[{"type":"others","name":"other friends","count":149,"items":[]}]},
         "following":{"count":70},
         "requests":{"count":80},
         "tips":{"count":90},
         "todos":{"count":100},
         "scores":{"recent":110,"max":120,"checkinsCount":30}}}
      """)
      case "venues/someVenueId" => m("""
      {"venue":{
        "id":"fakeId","name":"Fake Venue","itemId":"fakeId",
        "contact":{"phone":"8675309","formattedPhone":"(701) 867-5309"},
        "location":{"address":"Fake Address","crossStreet":"At Fake Street","city":"New York",
          "state":"NY","postalCode":"10018","lat":40,"lng":-74},
        "categories":[{"id":"fakeId","name":"Fake Category","pluralName":"Fake Categories",
          "icon":"noIcon","parents":["Fake Parent"],"primary":true}],
        "verified":true,"stats":{"checkinsCount":500,"usersCount":300,"tipCount":20}, "createdAt":5000,
        "hereNow":{"count":5,"groups":[
          {"type":"friends","name":"friends here","count":0,"items":[]},
          {"type":"others","name":"other people here","count":0,"items":[]}]},
        "mayor":{"count":15},
        "tips":{"count":10,"groups":[
          {"type":"others","name":"Tips from others","count":1,"items":[
            {"id":"fakeId","createdAt":7000,"itemId":"fakeId",
              "text":"This is a fake tip","todo":{"count":20},"done":{"count":10},
              "user":{"id":"fakeId","firstName":"Fake","lastName":"User",
                "photo":"noPhoto","gender":"female",
                "homeCity":"New York, NY"}}]}]},
        "tags":["a tag"],
        "specials":[],
        "specialsNearby":[],
        "shortUrl":"fakeUrl",
        "timeZone":"America/New_York",
        "beenHere":{"count":40},
        "photos":{"count":100,"groups":[
          {"type":"checkin","name":"friends' checkin photos","count":10,"items":[
            {"id":"fakeId","createdAt":10000,
              "url":"noPhoto",
              "sizes":{"count":10,"items":[
                {"url":"fakePhoto","width":36,"height":36}]},
              "source":{"name":"foursquare for iPhone","url":"https://foursquare.com/download/#/iphone"},
              "user":{"id":"fakeId","firstName":"Fake","photo":"noPhoto","gender":"male",
              "homeCity":"New York, NY","relationship":"friend"},"visibility":"friends",
              "checkin":{"id":"fakeId","createdAt":15000,"type":"checkin",
                "shout":"This is a shout!","timeZone":"America/New_York"}}]},
          {"type":"venue","name":"venue photos","count":0,"items":[]}]},
        "todos":{"count":0,"items":[]}}}
      """)
      case "venues/missingId" => """{"meta":{"code":400,"errorType":"param_error","errorDetail":"Value missingId is invalid for venue id"},"response":{}}"""
      case _ => """{"meta":{"code":404, "errorType":"other", "errorDetail":"Endpoint not found"},"response":{}}"""
    }
  }

  def multiCall(token: String, reqs: List[FSRequest]): String = "TODO"
}