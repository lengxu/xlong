package uyun.xianglong.metadata

import org.json4s.DefaultFormats
import org.json4s.Extraction._
import org.json4s.jackson.JsonMethods._

object TicketInnerFields {

  case class TicketField(name: String, code: String, `type`: String, typeDesc: String)

  def getTicketInnerFields: Seq[TicketField] = {
    implicit val fmt: DefaultFormats = DefaultFormats
    extract[Seq[TicketField]](parse(ClassLoader.getSystemClassLoader.getResourceAsStream("metadata/ticket.inner.fields.json")))
  }

  def main(args: Array[String]) {
    println(getTicketInnerFields)
  }
}
