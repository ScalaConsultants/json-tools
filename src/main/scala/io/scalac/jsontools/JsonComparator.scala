package io.scalac.jsontools

import net.liftweb.json._
import dispatch._, Defaults._
import io.scalac.http.ReqMethod

object JsonComparator {

  def compareWithApiary(apiJson: JValue, resource: String, method: ReqMethod.Value = ReqMethod.GET) = {
    import io.scalac.http.ReqMethod._

    val server = method match {
      case GET => url(resource)
      case PUT => url(resource).PUT
      case POST => url(resource).POST
      case _ => throw new IllegalArgumentException("No handler for " + method + " http method in JsonComparator")
    }
    val http = Http(server OK as.String)
    val apiaryJson = parse(http())

    compare(apiJson, apiaryJson)
  }

  def compare(apiJson: JValue, schemaJson: JValue) = {
    ApiaryDiff.diff(apiJson, schemaJson) match {
      case Diff(_, JNothing, JNothing) => JsonCompareOK
      case Diff(_, added, deleted) => JsonCompareMismatch(added, deleted, apiJson, schemaJson)
    }
  }
}