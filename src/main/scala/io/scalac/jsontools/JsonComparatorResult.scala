package io.scalac.jsontools

import io.scalac.jsontools.JsonImpliticts._
import net.liftweb.json._
import net.liftweb.json.JsonDSL._

abstract class JsonComparatorResult {
  def raport: String
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