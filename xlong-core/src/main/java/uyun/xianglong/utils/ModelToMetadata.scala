package uyun.xianglong.utils

import java.io.{File, FileInputStream}

import com.google.common.io.Files
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.json4s.DefaultFormats
import org.json4s.Extraction._
import org.json4s.jackson.JsonMethods._
import uyun.xianglong.metadata.MetricNames.{DeviceGroup, Metric, MetricGroup}
import uyun.xianglong.metadata.ResourceFields.ResourceAttribute
import uyun.xianglong.metadata.TicketInnerFields.TicketField

import scala.collection.mutable
import scala.xml.XML

object ModelToMetadata {

  private implicit val fmt: DefaultFormats = DefaultFormats

  def convertResourceFields(): Seq[ResourceAttribute] = {
    val in = xml.Source.fromInputStream(ClassLoader.getSystemResourceAsStream("metadata/pacific_zh_CN_v3.3.4.xml"))
    val nodes = XML.load(in)
    val attrs = nodes \ "Attributes" \ "Attribute"
    attrs map { attr =>
      val code = attr.attribute("code").map(_.head.text.trim).orNull
      val name = attr.attribute("name").map(_.head.text.trim).orNull
      val attrType = attr.attribute("type").map(_.head.text.trim).orNull
      val text = attr.text.trim
      val param = parse(if (text == "") "{}" else text)
      ResourceAttribute(code, name, attrType, param)
    }
  }

  def convertTicketInnerFields(): Seq[TicketField] = {
    val node = parse(ClassLoader.getSystemResourceAsStream("metadata/ticket.json"))
    extract[Seq[TicketField]](node \ "data")
  }

  def convertMetricNames(): Seq[DeviceGroup] = {
    val excel = new HSSFWorkbook(new FileInputStream("D:\\文档\\Store\\指标列表-V1.10.xls"))
    import scala.collection.JavaConversions._
    val iter = excel.sheetIterator()
    iter.next()
    iter.next()
    var rowNum = 0
    val groupMap = mutable.Map[String, mutable.Buffer[MetricGroup]]()
    iter.foreach { sheet =>
      val iter = sheet.rowIterator()
      iter.next()
      val deviceGroupCode = sheet.getSheetName
      import scala.util.control.Breaks._
      val metricMap = mutable.Map[(String, String), mutable.Buffer[Metric]]()
      breakable {
        var lastGroupCode = ""
        var lastGroupName = ""
        iter.foreach { row =>
          var groupName = Option(row.getCell(0)).map(_.getStringCellValue).getOrElse(lastGroupName)
          var groupCode = Option(row.getCell(1)).map(_.getStringCellValue).getOrElse(lastGroupCode)
          if (groupName == "") groupName = lastGroupName
          if (groupCode == "") groupCode = lastGroupCode
          val metricCode = Option(row.getCell(2)).map(_.getStringCellValue).orNull
          val unit = Option(row.getCell(3)).map(_.getStringCellValue).orNull
          val metricName = Option(row.getCell(4)).map(_.getStringCellValue).orNull
          val description = Option(row.getCell(5)).map(_.getStringCellValue).orNull
          if (metricCode == null) {
            break()
          }
          metricMap.getOrElseUpdate((groupName, groupCode), mutable.Buffer()) +=
            Metric(metricCode, metricName, unit, description)
          lastGroupCode = groupCode
          lastGroupName = groupName
        }
      }
      val group = metricMap.map(kv => {
        val (name, code) = kv._1
        val metrics = kv._2
        MetricGroup(code, name, metrics)
      })
      groupMap.getOrElseUpdate(deviceGroupCode, mutable.Buffer()) ++= group
    }
    groupMap.map(kv => DeviceGroup(kv._1, kv._2)).toSeq
  }

  def main(args: Array[String]): Unit = {
    val resOutput = "D:\\workspace\\store\\xlong\\xlong-core\\src\\main\\resources\\metadata\\resource.fields.json"
    val metricOutput = "D:\\workspace\\store\\xlong\\xlong-core\\src\\main\\resources\\metadata\\metric.names.json"
    val ticketOutput = "D:\\workspace\\store\\xlong\\xlong-core\\src\\main\\resources\\metadata\\ticket.inner.fields.json"

    Files.write(compact(decompose(convertResourceFields())).getBytes(), new File(resOutput))
    Files.write(compact(decompose(convertMetricNames())).getBytes(), new File(metricOutput))
    Files.write(compact(decompose(convertTicketInnerFields())).getBytes(), new File(ticketOutput))
  }
}
