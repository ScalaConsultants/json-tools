#jsonTools [![Build Status](https://travis-ci.org/ScalaConsultants/jsonTools.png?branch=master)](https://travis-ci.org/ScalaConsultants/jsonTools)
###A toolbox for working with JSON formatted data.  
The main target of jsonTools is to enable users to compare json against a given schema, which is useful when doing API driven development or using some external schema as JSON definition. JsonTools compare both data structure and types. The presence of data is also validated.
###Usage examples
####Comparing JSON structure with given schema
```
val json = ("string" -> "string example") ~ ("number" -> 42) ~ ("array" -> List(1,2,3))
val schema = ("string" -> "string example") ~ ("number" -> 42) ~ ("array" -> List(1,2,3))
val result = JsonComparator.compare(json, schema)
```
####Comparing structure with external schema (like apiary) using a GET call 
```
val json = ("string" -> "string example") ~ ("number" -> 42) ~ ("array" -> List(1,2,3))
val result = JsonComparator.compareWithApiary(json, "http://example.com/user/1")
```
####Handling the comparison result  
Comparison result is a subclass of JsonComparatorResult, either JsonCompareOK or JsonCompareMismatch
JsonCompareOK indicates success in comparison. The JsonCompareMismatch contains additional information about the failure - the expected schema, the actual json and info about the change 
```
result match {
  case JsonCompareMismatch(removedJson, addedJson, json, jsonSchema) => // perform some action on match failure
  case _ => // everything is fine
}
```
####For more examples please take a look at test files
=========
