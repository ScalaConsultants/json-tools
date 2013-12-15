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
    
    "return Mismatch on missing data" in {
      val jsonApi = json ~ ("extra" -> "added in api; not mentioned in schema")
      val result = JsonComparator.compare(jsonApi, json) 
      result match {
        case JsonCompareMismatch(JNothing, wasAdded, jsonApi, json) => success
        case _ => failure
      }
      
      result.hasMinimalFieldSet must be equalTo true
    }
  }
}