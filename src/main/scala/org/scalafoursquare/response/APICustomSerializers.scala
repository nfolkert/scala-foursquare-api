package org.scalafoursquare.response

import net.liftweb.json.{Extraction, DefaultFormats, TypeInfo, Formats, Serializer}
import net.liftweb.json.JsonAST.{JBool, JString, JDouble, JInt, JArray, JField, JObject, JValue, JNothing}

object APICustomSerializers {
  object Formats extends DefaultFormats
  def formats = Formats + UserSearchUnmatchedSerializer + PrimitiveSerializer + BadgesSerializer

  def serializePrimitive(p: Primitive): JValue = {
    p match {
      case IntPrimitive(v) => JInt(v)
      case DoublePrimitive(v) => JDouble(v)
      case StringPrimitive(v) => JString(v)
      case BooleanPrimitive(v) => JBool(v)
      case NothingPrimitive => JNothing
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
}

