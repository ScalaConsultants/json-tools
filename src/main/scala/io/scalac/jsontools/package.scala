package io.scalac

import net.liftweb.json._

package object jsontools {
  object JsonImpliticts {
    implicit class JValuePimp(jvalue: JValue) {
      //render called with JNothing throws error
      // https://github.com/lift/framework/blob/master/core/json/src/main/scala/net/liftweb/json/JsonAST.scala#L420
      def pretty: String = jvalue match {
        case JNothing => ""
        case _ => net.liftweb.json.pretty(render(jvalue))
      }
    }
  
    implicit class DiffPimp(diff: Diff) {
      def sameStructure: Boolean = diff match {
        case Diff(_, JNothing, JNothing) => true
        case _ => false
      }
    }
  }
}