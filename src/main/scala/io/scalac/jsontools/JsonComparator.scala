package io.scalac.jsontools

import net.liftweb.json._
import dispatch._, Defaults._
import io.scalac.http.ReqMethod

/**
 * 
 */
object JsonComparator {

  /**
   * Compares the specified JSON formatted API return data with a JSON schema downloaded from the web
   * @param apiJson The real-life JSON being tested against the schema
   * @param resource An url string pointing to JSON schema
   * @param method Specifies what http verb should be used to obtain the resource. The default value is GET
   * @return JsonCompareMismatch showing the comparison result or JsonCompareOK if there is no difference
   */
  def compareWithApiary(apiJson: JValue, resource: String, method: ReqMethod.Value = ReqMethod.GET) = {
    import io.scalac.http.ReqMethod._

    val server = method match {
      case GET => url(resource)
      case PUT => url(resource).PUT
      case POST => url(resource).POST
      case DELETE => url(resource).DELETE
      case _ => throw new IllegalArgumentException("No handler for " + method + " http method in JsonComparator")
    }
    val http = Http(server OK as.String)
    val apiaryJson = parse(http())

    compare(apiJson, apiaryJson)
  }

  /**
   * Compares API return JSON with the specified JSON schema
   * @param apiJson The real-life JSON being tested against the schema
   * @param schemaJson JValue holding the desired JSON schema
   * @return JsonCompareMismatch showing the comparison result or JsonCompareOK if there is no difference
   */
  def compare(apiJson: JValue, schemaJson: JValue) = {
    ApiaryDiff.diff(apiJson, schemaJson) match {
      case Diff(_, JNothing, JNothing) => JsonCompareOK
      case Diff(_, added, deleted) => JsonCompareMismatch(added, deleted, apiJson, schemaJson)
    }
  }
}