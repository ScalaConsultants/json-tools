#jsonTools [![Build Status](https://travis-ci.org/ScalaConsultants/jsonTools.png?branch=master)](https://travis-ci.org/ScalaConsultants/jsonTools)
###A set of tools helping in working with JSON formatted data.
###Usage
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
=========
