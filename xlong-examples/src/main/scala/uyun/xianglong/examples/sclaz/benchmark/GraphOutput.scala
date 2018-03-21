package uyun.xianglong.examples.sclaz.benchmark

import java.awt.Desktop
import java.io.File
import java.util.UUID
import java.util.regex.Pattern.quote

import com.google.common.io.{Files, Resources}
import org.apache.commons.io.Charsets
import org.json4s.Extraction._
import org.json4s._
import org.json4s.jackson.JsonMethods._

import scala.language.implicitConversions

object GraphOutput {

  case class Point(value: Double)

  case class PointGroup(title: String, points: Seq[Point])

  case class Output(title: String, groups: Seq[PointGroup], sampleSec: Long, sampleNum: Int)

  private case class XAxis(data: Seq[String], `type`: String = "category", boundaryGap: Boolean = false)

  private val markLine: JValue = parse(read("benchmark/markLine.json"))

  private val markPoint: JValue = parse(read("benchmark/markPoint.json"))

  private def read(uri: String): String = Resources.toString(ClassLoader.getSystemResource(uri), Charsets.UTF_8)


  private case class Series(name: String
                            , data: Seq[Double]
                            , `type`: String = "line"
                            , markLine: JValue = markLine
                            , markPoint: JValue = markPoint)


  def writeTo(dir: String, outputs: Seq[Output]): Unit = {
    val builder = new StringBuilder()
    outputs.foreach { output =>
      val xAxis = XAxis(1 to output.sampleNum map { i => output.sampleSec * i + "s" })
      val series = output.groups.map { group =>
        val name = group.title
        val data = group.points.map(_.value)
        Series(name, data)
      }
      val xAxisJson = compact(decompose(xAxis))
      val seriesJson = compact(decompose(series))
      val legendJson = compact(decompose(output.groups.map(_.title)))
      val html = template
        .replaceAll(quote("$title"), output.title)
        .replaceAll(quote("$divId"), UUID.randomUUID().toString)
        .replaceAll(quote("$legend"), legendJson)
        .replaceAll(quote("$series"), seriesJson)
        .replaceAll(quote("$xAxis"), xAxisJson)
      builder.append(html).append("\n")
    }
    val tables = read("benchmark/template.html").replaceAll(quote("$templates"), builder.toString())
    Files.write(tables.getBytes(), new File(dir + "/report.html"))
    Files.write(js.getBytes(), new File(dir + "/echarts.min.js"))
  }

  implicit def converter(i: Long): Point = Point(i)

  implicit def converter(i: Int): Point = Point(i)

  implicit def converter(i: Double): Point = Point(i)

  private val template = read("benchmark/template.html.part")

  private def js = read("benchmark/echarts.min.js")

  def main(args: Array[String]): Unit = {
    val group = FlinkNetworkModelBenchmark.readJson("checkpoint=-1")
    val group2 = FlinkNetworkModelBenchmark.readJson("checkpoint=10s")
    val group3 = FlinkNetworkModelBenchmark.readJson("checkpoint=30s")
    val group4 = FlinkNetworkModelBenchmark.readJson("checkpoint=60s")
    //    val pointGroup = PointGroup("test", Seq(10000.1, 20000.2, 30000.3))
    //    val pointGroup2 = PointGroup("test2", Seq(20000.1, 10000.1, 30000.3))
    val output = Output("test", Seq(group, group2, group3, group4), 5, 120)
    writeTo("D:/Temp", Seq(output, output))
    Desktop.getDesktop.open(new File("D:/Temp/report.html"))
  }

}
