package uyun.xianglong.metadata

import org.json4s.DefaultFormats
import org.json4s.Extraction._
import org.json4s.JsonAST.JValue
import org.json4s.jackson.JsonMethods._

object ResourceFields {


  case class ResourceAttribute(code: String, name: String, attrType: String, params: JValue)

  def getResourceClasses: Seq[ResourceAttribute] = {
    implicit val fmt: DefaultFormats = DefaultFormats
    extract[Seq[ResourceAttribute]](parse(ClassLoader.getSystemClassLoader.getResourceAsStream("metadata/resource.fields.json")))
  }

  def main(args: Array[String]) {
    println(getResourceClasses)
  }
}
