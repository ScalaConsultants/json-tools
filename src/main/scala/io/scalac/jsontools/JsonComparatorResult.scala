package io.scalac.jsontools

import io.scalac.jsontools.JsonImpliticts._
import net.liftweb.json._
import net.liftweb.json.JsonDSL._
import scala.collection.mutable.StringBuilder

/**
 * Parent for classes representing JSON comparison results 
 */
abstract class JsonComparatorResult {
  /**
   * Return a short report about the changes between JSON as String
   */
  def report: String
  /**
   * Return True if all fields required by the schema are in the API result (the result might give more data, than specified in schema).
   * False otherwise.
   */
  def hasMinimalFieldSet: Boolean
  /**
   * Returns a diff formatted String showing differences between JSON
   */
  def diffFormatted: String
}
case object JsonCompareOK extends JsonComparatorResult {
  def report = "No difference"
  def hasMinimalFieldSet = true
  def diffFormatted = "No difference"
}

case class JsonCompareMismatch(removed: JValue, added: JValue, api: JValue, expected: JValue) extends JsonComparatorResult {
  def report = "Comparison result: \nAdded: " + added.pretty + " \nRemoved: " + removed.pretty
  def hasMinimalFieldSet = removed == JNothing
  override def toString = report + "\nApi: " + api.pretty + " \nExpected: " + expected.pretty
  def diffFormatted = {
    val formattedStr = new StringBuilder()
    
    def separator = formattedStr ++= "\n --- \n"
    
    def formatAsDiff(json: JValue) = json match {
      case changedField @ JField(key, value) if (added \\ key) != JObject(List()) && (removed \\ key) != JObject(List()) => 
        val newField = (added \\ key)
        formattedStr ++= "> "
        formattedStr ++= newField.pretty
        formattedStr ++= "\n"
        
        val oldField = (removed \\ key)
        formattedStr ++= "< "
        formattedStr ++= oldField.toString//.pretty
        separator
      case addedField @ JField(key, value) if (added \\ key) != JObject(List()) => 
        formattedStr ++= "> "
        formattedStr ++= addedField.pretty
        formattedStr ++= "\n< "
        separator
      case removedField @ JField(key, value) if (removed \\ key) != JObject(List()) => 
        formattedStr ++= "> \n\n"
        formattedStr ++= "< "
        formattedStr ++= removedField.pretty
        separator
      case simple => 
        formattedStr ++= simple.pretty
        formattedStr ++= "\n"
    }
    
    api.children.foreach(formatAsDiff(_))//the initial api object is wrapped in object
    formattedStr.toString
  }
}