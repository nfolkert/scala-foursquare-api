package org.scalafoursquare

import org.junit.Test
import net.liftweb.common.Empty
import net.liftweb.util.Props
import org.specs.SpecsMatchers

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
          "venue":{"id":"fakeId","name":"foursquare HQ","itemId":"fakeId",
            "contact":{"twitter":"fakeTwitter"},
            "location":{"address":"East Village","city":"New York","state":"NY","postalCode":"10003","lat":40,"lng":-73},
            "categories":[{"id":"fakeId","name":"Fake Category","pluralName":"Fake Categories","icon":"noIcon","parents":["Fake Parent","Fake Grandpa"],"primary":true}],
            "verified":true,"stats":{"checkinsCount":500,"usersCount":200,"tipCount":100},"url":"fakeUrl"}}]},
         "friends":{"count":60,"groups":[{"type":"others","name":"other friends","count":149,"items":[]}]},
         "following":{"count":70},
         "requests":{"count":80},
         "tips":{"count":90},
         "todos":{"count":100},
         "scores":{"recent":110,"max":120,"checkinsCount":30}}}
      """)
      case "venues/someVenueId" => m("""{}""")
      case _ => """{"meta":{"code":404, "errorType":"other", "errorDetail":"Endpoint not found"},"response":{}}"""
    }
  }
}

class ConnectTest extends SpecsMatchers {

  val USER_TOKEN = Props.get("access.token.user").open_!
  val CONSUMER_KEY = Props.get("consumer.key").open_!
  val CONSUMER_SECRET = Props.get("consumer.secret").open_!
  val TEST_URL = Props.get("foursquare.test.url").open_!
  val API_VERSION = Props.get("api.version").open_!

  @Test
  def venueDetail() {
    val mockCaller = TestCaller
    val mockApp = FSApp(mockCaller)

    val mockVenue = mockApp.venueDetail("someVenueId").get

    // This one actually makes a web call!

    val caller = HttpFSCaller(CONSUMER_KEY, CONSUMER_SECRET, TEST_URL, API_VERSION)
    val app = FSApp(caller)

    val venue = app.venueDetail("1234").get

    println(venueCategories.toString)
  }

  @Test
  def userDetail() {
    val mockCaller = TestCaller
    val mockApp = FSApp(mockCaller)
    val mockUserApp = mockApp.user("Just Testing!")

    val mockSelf = mockUserApp.self.get
    val mockById = mockUserApp.userDetail("someUserId").get

    println(mockSelf.toString)
    println(mockById.toString)

    val caller = HttpFSCaller(CONSUMER_KEY, CONSUMER_SECRET, TEST_URL, API_VERSION)
    val userApp = FSApp(caller).user(USER_TOKEN)

    val self = userApp.self.get

    println(self.toString)
  }

  @Test
  def venueCategories() {
    val mockCaller = TestCaller
    val mockApp = FSApp(mockCaller)

    val mockVenueCategories = mockApp.venueCategories.get
    mockVenueCategories.meta must_== Meta(200, None, None)
    mockVenueCategories.notifications must_== None
    mockVenueCategories.response.categories.length must_== 1
    mockVenueCategories.response.categories(0).name must_== "Fake Category"
    mockVenueCategories.response.categories(0).pluralName must_== "Fake Categories"
    mockVenueCategories.response.categories(0).id must_== "fakeId"
    mockVenueCategories.response.categories(0).icon must_== "noIcon"
    mockVenueCategories.response.categories(0).categories.length must_== 0

    // This one actually makes a web call!
    
    val caller = HttpFSCaller(CONSUMER_KEY, CONSUMER_SECRET, TEST_URL, API_VERSION)
    val app = FSApp(caller)

    val venueCategories = app.venueCategories.get

    println(venueCategories.toString)
  }

  /*
user: {
id: "47321"
firstName: "Nate"
lastName: "Folkert"
photo: "https://playfoursquare.s3.amazonaws.com/userpix_thumbs/0FQGVWKEVEK20I0V.jpg"
gender: "male"
homeCity: "New York, NY"
relationship: "self"
type: "user"
pings: false
contact: {
phone: "4155951126"
email: "nfolkert@gmail.com"
twitter: "nfolkert"
facebook: "203195"
}
badges: {
count: 66
}
mayorships: {
count: 37
items: [ ]
}
checkins: {
count: 5513
items: [
{
id: "4e5503bab61c93816c0fac33"
createdAt: 1314194362
type: "checkin"
timeZone: "America/New_York"
venue: {
id: "4ab7e57cf964a5205f7b20e3"
name: "foursquare HQ"
itemId: "v4ab7e57cf964a5205f7b20e3"
contact: {
twitter: "foursquare"
}
location: {
address: "East Village"
city: "New York"
state: "NY"
postalCode: "10003"
lat: 40.7281409283912
lng: -73.99112820625305
}
categories: [
{
id: "4bf58dd8d48988d125941735"
name: "Tech Startup"
pluralName: "Tech Startups"
icon: "https://foursquare.com/img/categories/building/default.png"
parents: [
"Home / Work / Others"
"Offices"
]
primary: true
}
]
verified: true
stats: {
checkinsCount: 14865
usersCount: 3921
tipCount: 95
}
url: "http://foursquare.com"
}
}
]
}
friends: {
count: 149
groups: [
{
type: "others"
name: "other friends"
count: 149
items: [ ]
}
]
}
following: {
count: 14
}
requests: {
count: 0
}
tips: {
count: 80
}
todos: {
count: 27
}
scores: {
recent: 216
max: 585
checkinsCount: 71
}
}











venue: {
id: "3fd66200f964a52060e71ee3"
name: "Bar 29"
itemId: "v3fd66200f964a52060e71ee3"
contact: {
phone: "2127790306"
formattedPhone: "(212) 779-0306"
}
location: {
address: "405 Third Avenue"
crossStreet: "btw 28th and 29th St"
city: "New York"
state: "NY"
postalCode: "10016"
country: "USA"
lat: 40.742407
lng: -73.980599
}
categories: [
{
id: "4bf58dd8d48988d116941735"
name: "Bar"
pluralName: "Bars"
icon: "https://foursquare.com/img/categories/nightlife/bar.png"
parents: [
"Nightlife Spots"
]
primary: true
}
]
verified: true
stats: {
checkinsCount: 1321
usersCount: 852
tipCount: 11
}
createdAt: 1071014400
hereNow: {
count: 0
groups: [
{
type: "friends"
name: "friends here"
count: 0
items: [ ]
}
{
type: "others"
name: "other people here"
count: 0
items: [ ]
}
]
}
mayor: {
count: 18
user: {
id: "250702"
firstName: "Grace"
lastName: "C."
photo: "https://playfoursquare.s3.amazonaws.com/userpix_thumbs/K43EMXF1KRJ2GIOU.jpg"
gender: "female"
homeCity: "New York, NY"
}
}
tips: {
count: 12
groups: [
{
type: "others"
name: "Tips from others"
count: 12
items: [
{
id: "49b6e29f70c603bba20e8eb4"
createdAt: 1236722335
itemId: "t49b6e29f70c603bba20e8eb4"
text: "Pool table, nice tenders, and seating"
todo: {
count: 2
}
done: {
count: 7
}
user: {
id: "373"
firstName: "Krisanne"
lastName: "C."
photo: "https://playfoursquare.s3.amazonaws.com/userpix_thumbs/373_1236293029.jpg"
gender: "female"
homeCity: "New York, NY"
}
}
{
id: "4cde0bb8f8a4a1435f96d4bc"
createdAt: 1289620408
itemId: "t4cde0bb8f8a4a1435f96d4bc"
text: "A crazy fun bar. Make sure Lauren is bartending. She's amazing and the hottest thing to happen to Bar 29."
todo: {
count: 0
}
done: {
count: 3
}
user: {
id: "1101229"
firstName: "Matty"
lastName: "M."
photo: "https://playfoursquare.s3.amazonaws.com/userpix_thumbs/N01LIN3DOCPQ0TFS.jpg"
gender: "male"
homeCity: "New York, NY"
}
}
{
id: "4ba061e870c603bbaaac94b4"
createdAt: 1268802024
itemId: "t4ba061e870c603bbaaac94b4"
text: "watch Syracuse basketball games here.  Let's Go Orange!"
todo: {
count: 1
}
done: {
count: 3
}
user: {
id: "169953"
firstName: "ben"
lastName: "m."
photo: "https://playfoursquare.s3.amazonaws.com/userpix_thumbs/LIECY5QV2ZWTTS4H.jpg"
gender: "male"
homeCity: "New York, NY"
}
}
{
id: "4ce95eb9f3bda143f38abfe4"
createdAt: 1290362553
itemId: "t4ce95eb9f3bda143f38abfe4"
text: "Check into bar29 on 4square today, Sunday the 21st and get a FREE bud!"
todo: {
count: 0
}
done: {
count: 2
}
user: {
id: "1535980"
firstName: "isaac"
lastName: "b."
photo: "https://playfoursquare.s3.amazonaws.com/userpix_thumbs/B00EUJ3BYZQVN1QX.jpg"
gender: "male"
homeCity: "new york, ny"
}
}
{
id: "4ce83e08baa6a1cdb1c82c6c"
createdAt: 1290288648
itemId: "t4ce83e08baa6a1cdb1c82c6c"
text: "Ask for Lauren the bartender.  She rocks and is fast at serving drinks!"
todo: {
count: 0
}
done: {
count: 2
}
user: {
id: "945001"
firstName: "Christina"
lastName: "K."
photo: "https://playfoursquare.s3.amazonaws.com/userpix_thumbs/RAKFNJQ4VVA4M34W.jpg"
gender: "female"
homeCity: "New York"
}
}
{
id: "4e4dea19c65bb313ba712e39"
createdAt: 1313729049
itemId: "t4e4dea19c65bb313ba712e39"
text: "Old version of big buck"
todo: {
count: 0
}
done: {
count: 1
}
user: {
id: "361732"
firstName: "Greg"
photo: "https://playfoursquare.s3.amazonaws.com/userpix_thumbs/MYOWE4MZNNPECJQE.jpg"
gender: "male"
homeCity: "Amherst, NY"
}
}
{
id: "4d7c1d46da568cfa46ba5fff"
createdAt: 1299979590
itemId: "t4d7c1d46da568cfa46ba5fff"
text: "This place sucks.  They lie to you.  Avoid at all costs"
todo: {
count: 0
}
done: {
count: 1
}
user: {
id: "2322073"
firstName: "Mike"
lastName: "M."
photo: "https://playfoursquare.s3.amazonaws.com/userpix_thumbs/531ZOWI0YRS5GMYS.jpg"
gender: "male"
homeCity: "Glen Cove, NY"
}
}
{
id: "4d7ace923fbf6dcbdacf6323"
createdAt: 1299893906
itemId: "t4d7ace923fbf6dcbdacf6323"
text: "One toilet. Which sucks, until you realize you are in murray hill and this bar blows. Fuck you, greedy owners."
todo: {
count: 0
}
done: {
count: 1
}
user: {
id: "1236800"
firstName: "Dan"
lastName: "K."
photo: "https://playfoursquare.s3.amazonaws.com/userpix_thumbs/YEU1BEUEOKRJHTOS.jpg"
gender: "male"
homeCity: "Hoboken, NJ"
}
}
{
id: "4d5ef5845c39b1f7f2e4f149"
createdAt: 1298068868
itemId: "t4d5ef5845c39b1f7f2e4f149"
text: "Great pool table!"
todo: {
count: 0
}
done: {
count: 1
}
user: {
id: "1535980"
firstName: "isaac"
lastName: "b."
photo: "https://playfoursquare.s3.amazonaws.com/userpix_thumbs/B00EUJ3BYZQVN1QX.jpg"
gender: "male"
homeCity: "new york, ny"
}
}
{
id: "4cb093dccbab236a9f88a173"
createdAt: 1286640604
itemId: "t4cb093dccbab236a9f88a173"
text: "Think I have to downgrade this place to 'beware'. Was served a warm beer by a snarky little girl behind the bar who told me 'I'm not the one in charge of keeping things cold' Her bf is a lucky guy."
todo: {
count: 0
}
done: {
count: 1
}
user: {
id: "61537"
firstName: "Patrick"
lastName: "H."
photo: "https://playfoursquare.s3.amazonaws.com/userpix_thumbs/GGJ4OSRM3BNGPGVE.jpg"
gender: "male"
homeCity: "New York, NY"
}
}
{
id: "4c980afdf419a093a9b86c88"
createdAt: 1285032701
itemId: "t4c980afdf419a093a9b86c88"
text: "Stop by on Thursdays to see Alexis. She's smoking Hott!"
todo: {
count: 0
}
done: {
count: 1
}
user: {
id: "890600"
firstName: "kelly"
lastName: "w."
photo: "https://playfoursquare.s3.amazonaws.com/userpix_thumbs/KWJ1CGD5TIHMQTR4.jpg"
gender: "female"
homeCity: "NY"
}
}
{
id: "4c0bf97c3c49d13a808707cd"
createdAt: 1275853180
itemId: "t4c0bf97c3c49d13a808707cd"
text: "This place used to be Maker's.  They cleaned it up a bit and the drink prices are a bit more back down to earth but the crowd really isn't very good and the atmosphere is a bit lame."
todo: {
count: 2
}
done: {
count: 1
}
user: {
id: "61537"
firstName: "Patrick"
lastName: "H."
photo: "https://playfoursquare.s3.amazonaws.com/userpix_thumbs/GGJ4OSRM3BNGPGVE.jpg"
gender: "male"
homeCity: "New York, NY"
}
}
]
}
]
}
tags: [
"pool table"
]
specials: [ ]
specialsNearby: [ ]
shortUrl: "http://4sq.com/3O6q07"
timeZone: "America/New_York"
beenHere: {
count: 0
}
photos: {
count: 2
groups: [
{
type: "checkin"
name: "friends' checkin photos"
count: 1
items: [
{
id: "4d508caf30caa34088abfeb8"
createdAt: 1297124527
url: "https://playfoursquare.s3.amazonaws.com/pix/FWHLVSY2V0VXCQXE55MCUD1Q04PAA5EWG10S34AUJHZNWRJW.jpg"
sizes: {
count: 4
items: [
{
url: "https://playfoursquare.s3.amazonaws.com/pix/FWHLVSY2V0VXCQXE55MCUD1Q04PAA5EWG10S34AUJHZNWRJW.jpg"
width: 540
height: 720
}
{
url: "https://playfoursquare.s3.amazonaws.com/derived_pix/FWHLVSY2V0VXCQXE55MCUD1Q04PAA5EWG10S34AUJHZNWRJW_300x300.jpg"
width: 300
height: 300
}
{
url: "https://playfoursquare.s3.amazonaws.com/derived_pix/FWHLVSY2V0VXCQXE55MCUD1Q04PAA5EWG10S34AUJHZNWRJW_100x100.jpg"
width: 100
height: 100
}
{
url: "https://playfoursquare.s3.amazonaws.com/derived_pix/FWHLVSY2V0VXCQXE55MCUD1Q04PAA5EWG10S34AUJHZNWRJW_36x36.jpg"
width: 36
height: 36
}
]
}
source: {
name: "foursquare for iPhone"
url: "https://foursquare.com/download/#/iphone"
}
user: {
id: "32"
firstName: "Dens"
photo: "https://playfoursquare.s3.amazonaws.com/userpix_thumbs/32_1239135232.jpg"
gender: "male"
homeCity: "New York, NY"
relationship: "friend"
}
visibility: "friends"
checkin: {
id: "4d508c9fbd6ff04d7410e90c"
createdAt: 1297124511
type: "checkin"
shout: "Checking out this @SyracuseU tweet up. Hey it's social media week!"
timeZone: "America/New_York"
}
}
]
}
{
type: "venue"
name: "venue photos"
count: 1
items: [
{
id: "4e472232d164155c0deff177"
createdAt: 1313284658
url: "https://playfoursquare.s3.amazonaws.com/pix/ENZSQLJLWYO1PKJ5ZBF3O1OG4GFC0VFGX0ZHGIJRXRZCBGZF.jpg"
sizes: {
count: 4
items: [
{
url: "https://playfoursquare.s3.amazonaws.com/pix/ENZSQLJLWYO1PKJ5ZBF3O1OG4GFC0VFGX0ZHGIJRXRZCBGZF.jpg"
width: 537
height: 720
}
{
url: "https://playfoursquare.s3.amazonaws.com/derived_pix/ENZSQLJLWYO1PKJ5ZBF3O1OG4GFC0VFGX0ZHGIJRXRZCBGZF_300x300.jpg"
width: 300
height: 300
}
{
url: "https://playfoursquare.s3.amazonaws.com/derived_pix/ENZSQLJLWYO1PKJ5ZBF3O1OG4GFC0VFGX0ZHGIJRXRZCBGZF_100x100.jpg"
width: 100
height: 100
}
{
url: "https://playfoursquare.s3.amazonaws.com/derived_pix/ENZSQLJLWYO1PKJ5ZBF3O1OG4GFC0VFGX0ZHGIJRXRZCBGZF_36x36.jpg"
width: 36
height: 36
}
]
}
source: {
name: "foursquare for iPhone"
url: "https://foursquare.com/download/#/iphone"
}
user: {
id: "12359290"
firstName: "Taylor AK"
lastName: "S."
photo: "https://playfoursquare.s3.amazonaws.com/userpix_thumbs/V3WZ4DGXGO5YQ2UL.jpg"
gender: "male"
homeCity: "Brooklyn "
}
visibility: "public"
}
]
}
]
}
todos: {
count: 0
items: [ ]
}
}
   */



  
}