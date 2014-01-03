package io.scalac.http

/**
 * HTTP Request methods
 */
object ReqMethod extends Enumeration {
  type ReqMethod = Value
  val GET = Value("GET")
  val POST = Value("POST")
  val PUT = Value("PUT")
  val DELETE = Value("DELETE")
  val HEAD = Value("HEAD")
  val CONNECT = Value("CONNECT")
  val OPTIONS = Value("OPTIONS")
  val PATCH = Value("PATCH")
  val TRACE = Value("TRACE")
  //we can avoid exceptions using unknown or not ;)
  val Unknown = Value("unknown")

  implicit class StringAsReq(str: String) {
    def toReqMethod: ReqMethod.Value = {
      try {
        withName(str)
      } catch {
        case e: Throwable => ReqMethod.Unknown
      }
    }
  }
}