package io.scalac.jsontools

import io.scalac.jsontools.JsonImpliticts._
import net.liftweb.json._
import net.liftweb.json.JsonDSL._

/**
 * Parent for classes representing comparison results 
 */
abstract class JsonComparatorResult {
  /**
   * Return a short report about the changes between JSON as String
   */
  def raport: String
  /**
   * Return True if all fields required by the schema are in the API result (the result might give more data, than specified in schema).
   * False otherwise.
   */
  def hasMinimalFieldSet: Boolean
}
case object JsonCompareOK extends JsonComparatorResult {
  def raport = "No difference"
  def hasMinimalFieldSet = true
}

case class JsonCompareMismatch(removed: JValue, added: JValue, api: JValue, expected: JValue) extends JsonComparatorResult {
  def raport = "Comparison result: \nAdded: " + added.pretty + " \nRemoved: " + removed.pretty
  def hasMinimalFieldSet = removed == JNothing
  override def toString = raport + "\nApi: " + api.pretty + " \nExpected: " + expected.pretty
}