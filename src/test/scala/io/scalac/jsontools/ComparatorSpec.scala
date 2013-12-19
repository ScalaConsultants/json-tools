package io.scalac.jsontools

import org.specs2.mutable.Specification
import net.liftweb.json._
import net.liftweb.json.JsonDSL._

class ComparatorSpec extends Specification {

  val json = ("string" -> "string example") ~ ("number" -> 42) ~ ("array" -> List(1,2,3))

  "ApiaryComaprator" should {
    "return OK on identical json" in {
      JsonComparator.compare(json, json) must be equalTo JsonCompareOK
    }
    
    "return Mismatch on missing data" in {
      val jsonSchema = json ~ ("missing" -> "not existing in api")
      val result = JsonComparator.compare(json, jsonSchema) 
      result match {
        case JsonCompareMismatch(wasRemoved, JNothing, json, jsonSchema) => success
        case _ => failure
      }
      result.hasMinimalFieldSet must be equalTo false
    }
    
    "return Mismatch on added data" in {
      val jsonApi = json ~ ("extra" -> "added in api; not mentioned in schema")
      val result = JsonComparator.compare(jsonApi, json) 
      result match {
        case JsonCompareMismatch(JNothing, wasAdded, jsonApi, json) => success
        case _ => failure
      }
      
      result.hasMinimalFieldSet must be equalTo true
    }
    
    "validate types for fields" in {
      val embeddedObject = ("1" -> "one") ~ ("2" -> "two")
      val jsonWithObject = json ~ ("field" -> embeddedObject)
      val jsonWithArray = json ~ ("field" -> List("one", "two"))
      
      val result = JsonComparator.compare(jsonWithObject, jsonWithArray)
      result match {
        case JsonCompareMismatch(missingArray, addedObject, _, _) => success
        case _ => failure
      }
    }
    
    "be resistant to field order" in {
      val embeddedObject = ("1" -> "one") ~ ("2" -> "two")
      val json1 = json ~ embeddedObject
      val json2 = embeddedObject ~ json
      
      val result = JsonComparator.compare(json1, json2)
      result must be equalTo JsonCompareOK
    }
    
    "create diffable report on mismatch" in {
      val apiJson = json ~ ("addedField" , "this is a new field") ~ 
        ("changedField" , 42) ~ ("nestedObject", ("smallNumber", "1234"))
      val schemaJson = json ~ ("removedField" , "this field has been deleted") ~ 
        ("changedField" , "I should be a string") ~ ("nestedObject", 
            ("smallNumber", "1234") ~ ("bigNumber", "5678")) 
      val result = JsonComparator.compare(apiJson, schemaJson)
      println("RAW: " + result + "\n\n\n")
      println( result.diffFormatted )
      success
    }
  }
}