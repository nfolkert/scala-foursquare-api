package org.scalafoursquare.response

import net.liftweb.json.{Extraction, DefaultFormats, TypeInfo, Formats, Serializer}
import net.liftweb.json.JsonAST.{JBool, JString, JDouble, JInt, JArray, JField, JObject, JValue, JNothing, JNull}

object APICustomSerializers {
  object Formats extends DefaultFormats
  def formats = Formats + UserSearchUnmatchedSerializer + PrimitiveSerializer + BadgesSerializer +
    VenueDetailSerializer + UpdateTargetSerializer

  def serializePrimitive(p: Primitive): JValue = {
    p match {
      case IntPrimitive(v) => JInt(v)
      case DoublePrimitive(v) => JDouble(v)
      case StringPrimitive(v) => JString(v)
      case BooleanPrimitive(v) => JBool(v)
      case NothingPrimitive => JNothing
      case _ => JNothing
    }
  }
  def deserializePrimitive(v: JValue): Primitive = {
    v match {
      case JInt(x) => IntPrimitive(x.intValue)
      case JDouble(x) => DoublePrimitive(x)
      case JString(x) => StringPrimitive(x)
      case JBool(x) => BooleanPrimitive(x)
      case _ => NothingPrimitive
    }
  }

  val PrimitiveSerializer = new Serializer[Primitive] {
    def serialize(implicit format: Formats): PartialFunction[Any, JValue] = {
      case x: Primitive => {serializePrimitive(x)}
    }
    val theClass = classOf[Primitive]
    def deserialize(implicit format: Formats): PartialFunction[(TypeInfo, JValue), Primitive] = {
      case (TypeInfo(cls, _), v) if cls == theClass => deserializePrimitive(v)
    }
  }

  def serializeUpdateTarget(t: UpdateTarget)(implicit format: Formats): JValue = {
    def tf(ty: String) = JField("type", JString(ty))
    def ob(ov: JValue) = List(JField("object", ov))
    t match {
      case UserUpdateTarget(v) => JObject(tf("user") :: ob(Extraction.decompose(v)))
      case CheckinUpdateTarget(/*v*/) => JObject(tf("checkin") :: ob(JNull /*Extraction.decompose(v)*/)) // TODO
      case VenueUpdateTarget(v) => JObject(tf("venue") :: ob(Extraction.decompose(v)))
      case ListUpdateTarget(/*v*/) => JObject(tf("list") :: ob(JNull /*Extraction.decompose(v)*/)) // TODO
      case BadgeUpdateTarget(v) => JObject(tf("badge") :: ob(Extraction.decompose(v)))
      case SpecialUpdateTarget(v) => JObject(tf("special") :: ob(Extraction.decompose(v)))
      case UrlUpdateTarget(v) => JObject(tf("url") :: ob(Extraction.decompose(v)))
      case _ => JNothing
    }
  }

  def deserializeUpdateTarget(obj: JObject)(implicit format: Formats): UpdateTarget = {
    val t = obj.obj.find(_.name == "type").map(_.value)
    val v = obj.obj.find(_.name == "object").map(_.value)
    (t,v) match {
      case (Some(JString("user")), Some(obj: JObject))  => UserUpdateTarget(obj.extract[UserCompact])
      case (Some(JString("checkin")), Some(obj: JObject))  => CheckinUpdateTarget(/*obj.extract[CheckinForFeed]*/)
      case (Some(JString("venue")), Some(obj: JObject))  => VenueUpdateTarget(obj.extract[VenueCompact])
      case (Some(JString("list")), Some(obj: JObject))  => ListUpdateTarget(/*obj.extract[UserCompact]*/)
      case (Some(JString("tip")), Some(obj: JObject))  => TipUpdateTarget(obj.extract[TipForList])
      case (Some(JString("badge")), Some(obj: JObject))  => BadgeUpdateTarget(obj.extract[Badge])
      case (Some(JString("special")), Some(obj: JObject))  => SpecialUpdateTarget(obj.extract[Special])
      case (Some(JString("url")), Some(obj: JObject))  => UrlUpdateTarget(obj.extract[Url])
      case _ => NothingUpdateTarget
    }
  }

  val UpdateTargetSerializer = new Serializer[UpdateTarget] {
    def serialize(implicit format: Formats): PartialFunction[Any, JValue] = {
      case x: UpdateTarget => {serializeUpdateTarget(x)}
    }
    val theClass = classOf[UpdateTarget]
    def deserialize(implicit format: Formats): PartialFunction[(TypeInfo, JValue), UpdateTarget] = {
      case (TypeInfo(cls, _), obj: JObject) if cls == theClass => deserializeUpdateTarget(obj)
    }
  }


  val UserSearchUnmatchedSerializer = new Serializer[UserSearchUnmatched] {
    def serialize(implicit format: Formats): PartialFunction[Any, JValue] = {
      case x: UserSearchUnmatched => {
        JObject(x.map.map(p=>{
          val key = p._1
          val vals = p._2
          JField(key, JArray(vals.map(v=>serializePrimitive(v))))
        }).toList)
      }
    }
    val theClass = classOf[UserSearchUnmatched]
    def deserialize(implicit format: Formats): PartialFunction[(TypeInfo, JValue), UserSearchUnmatched] = {
      case (TypeInfo(cls, _), obj: JObject) if cls == theClass => {
        val map = obj.obj.map(f=>{
          val list = f.value match {case JArray(vs)=>vs.map(v=>deserializePrimitive(v)); case _ => Nil}
          (f.name, list)
        }).toMap
        new UserSearchUnmatched(map)
      }
    }
  }

  val BadgesSerializer = new Serializer[Badges] {
    def serialize(implicit format: Formats): PartialFunction[Any, JValue] = {
      case x: Badges => {
        JObject(x.map.map(p=>{
          JField(p._1, Extraction.decompose(p._2))
        }).toList)
      }
    }
    val theClass = classOf[Badges]
    def deserialize(implicit format: Formats): PartialFunction[(TypeInfo, JValue), Badges] = {
      case (TypeInfo(cls, _), obj: JObject) if cls == theClass => {
        val map = obj.obj.map(f=>{
          (f.name, f.value.extract[Badge])
        }).toMap
        new Badges(map)
      }
    }
  }

  val VenueDetailSerializer = new Serializer[VenueDetail] {
    def serialize(implicit format: Formats): PartialFunction[Any, JValue] = {
      case x: VenueDetail => {
        JObject(Extraction.decompose(x.core).asInstanceOf[JObject].obj ++
          Extraction.decompose(x.extended).asInstanceOf[JObject].obj)
      }
    }
    val theClass = classOf[VenueDetail]
    def deserialize(implicit format: Formats): PartialFunction[(TypeInfo, JValue), VenueDetail] = {
      case (TypeInfo(cls, _), obj: JObject) if cls == theClass => {
        VenueDetail(core = obj.extract[VenueCore], extended = obj.extract[VenueDetailExtended])
      }
    }
  }
}

