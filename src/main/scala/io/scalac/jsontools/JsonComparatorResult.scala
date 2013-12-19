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
    
    val empty = JObject(List())
    val addedSign = "+"
    val deletedSign = "-"
    
    def formatAsDiff(json: JValue, added: JValue, removed: JValue): StringBuilder = json match {
      case changedField @ JField(key, value) if (added \\ key) != empty && (removed \\ key) != empty => 
        val add = (added \\ key)
        val rem = (removed \\ key)
        formattedStr ++= s"$addedSign${add.pretty} \n$deletedSign${rem.pretty}\n"
      case addedField @ JField(key, value) if (added \\ key) != empty => 
        formattedStr ++= s"$addedSign${addedField.pretty} \n$deletedSign\n"
      case removedField @ JField(key, JObject(value)) if (removed \\ key) != empty => 
        val jobject = removedField \\ key
        val add = (added \\ key)
        val rem = (removed \\ key)
        
        formattedStr ++= "{\n"
        jobject.children.foreach(formatAsDiff(_, add, rem))
        formattedStr ++= "}\n"
      case removedField @ JField(key, value) if (removed \\ key) != empty => 
        formattedStr ++= s"$addedSign\n$deletedSign${removedField.pretty}\n"
      case ok => 
        formattedStr ++= s" ${ok.pretty}\n"
    }
    
    (api merge expected).children.foreach(formatAsDiff(_, added, removed))//the initial api object is wrapped in object
    formattedStr.toString
  }
}