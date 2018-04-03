package uyun.xianglong.metadata

import org.json4s.DefaultFormats
import org.json4s.Extraction._
import org.json4s.jackson.JsonMethods._

object MetricNames {

  case class DeviceGroup(code: String, metricGroup: Seq[MetricGroup])

  case class MetricGroup(code: String, name: String, metrics: Seq[Metric])

  case class Metric(code: String, name: String, unit: String, description: String)

  def getDeviceGroups: Seq[DeviceGroup] = {
    implicit val fmt: DefaultFormats = DefaultFormats
    extract[Seq[DeviceGroup]](parse(ClassLoader.getSystemClassLoader.getResourceAsStream("metadata/metric.names.json")))
  }

  def main(args: Array[String]) {
    println(getDeviceGroups)
  }
}
