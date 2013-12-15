package io.scalac.jsontools

import net.liftweb.json._

object ApiaryDiff {

  def diff(api: JValue, schema: JValue): Diff = (api, schema) match {
    case (a, s) if a == s => Diff(JNothing, JNothing, JNothing)
    case (JObject(a), JObject(s)) => diffFields(a, s)
    case (JArray(a), JArray(s)) => diffVals(a, s)
    case (JField(apiName, apiVal), JField(schemaName, schemaVal)) if (apiName == schemaName) => diff(apiVal, schemaVal) map (JField(apiName, _))
    case (a @ JField(apiName, apiVal), s @ JField(schemaName, schemaVal)) if (apiName != schemaName) => Diff(JNothing, s, a)
    case (JInt(a), JInt(s)) if (a != s) => Diff(JInt(s), JNothing, JNothing)
    case (JDouble(a), JDouble(s)) if (a != s) => Diff(JDouble(s), JNothing, JNothing)
    case (JString(a), JString(s)) if (a != s) => Diff(JString(s), JNothing, JNothing)
    case (JBool(a), JBool(s)) if (a != s) => Diff(JBool(s), JNothing, JNothing)
    case (a, s) => Diff(JNothing, s, a)
  }

  private def diffFields(api: List[JField], schema: List[JField]) = {
    def diffRec(xleft: List[JField], yleft: List[JField]): Diff = xleft match {
      case Nil => Diff(JNothing, if (yleft.isEmpty) JNothing else JObject(yleft), JNothing)
      case x :: xs => yleft find (_.name == x.name) match {
        case Some(y) =>
          val Diff(c1, a1, d1) = diff(x, y)
          val Diff(c2, a2, d2) = diffRec(xs, yleft filterNot (_ == y))
          Diff(c1 ++ c2, a1 ++ a2, d1 ++ d2) map {
            case f: JField => JObject(f :: Nil)
            case x => x
          }
        case None =>
          val Diff(c, a, d) = diffRec(xs, yleft)
          Diff(c, a, JObject(x :: Nil) merge d)
      }
    }

    diffRec(api, schema)
  }

  private def diffVals(api: List[JValue], schema: List[JValue]) = {
    def diffRec(xleft: List[JValue], yleft: List[JValue]): Diff = (xleft, yleft) match {
      case (a, Nil) => //no element in schema
        Diff(JNothing, JNothing, if (a.isEmpty) JNothing else JArray(a))
      case (Nil, s) => //no element in apiary response
        Diff(JNothing, if (s.isEmpty) JNothing else JArray(s), JNothing)
      case (a @ x :: xs, s @ y :: ys) if a.length == s.length => //there is an schema example for every element in response
        val Diff(c1, a1, d1) = diff(x, y)
        val Diff(c2, a2, d2) = diffRec(xs, ys)
        Diff(c1 ++ c2, a1 ++ a2, d1 ++ d2)
      case (a, s :: Nil) => //instead of list schema defines only one element
        a.foldLeft(Diff(JNothing, JNothing, JNothing)) {
          case (Diff(changed, added, removed), arrayElem) => {
            val diff = ApiaryDiff.diff(arrayElem, s)
            Diff(changed ++ diff.changed, added ++ diff.added, removed ++ diff.deleted)
          }
        }
    }

    diffRec(api, schema)
  }
}