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
import uyun.xianglong.examples.sclaz.benchmark.FlinkNetworkModelBenchmark.reportConfig

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
    new File(dir).mkdirs()
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
    val checkpoint0s = FlinkNetworkModelBenchmark.readJson("checkpoint=-1")
    val checkpoint10s = FlinkNetworkModelBenchmark.readJson("checkpoint=10s")
    val checkPoint30s = FlinkNetworkModelBenchmark.readJson("checkpoint=30s")
    val checkpoint60s = FlinkNetworkModelBenchmark.readJson("checkpoint=60s")
    val redisPipeline = FlinkNetworkModelBenchmark.readJson("redisMode=pipeline")
    val redisExecute = FlinkNetworkModelBenchmark.readJson("redisMode=execute")
    val redisMock = FlinkNetworkModelBenchmark.readJson("redisMode=mock")
    val p1 = FlinkNetworkModelBenchmark.readJson("parallelism=1")
    val p4 = FlinkNetworkModelBenchmark.readJson("parallelism=4")
    val p8 = FlinkNetworkModelBenchmark.readJson("parallelism=8")
    val p16 = FlinkNetworkModelBenchmark.readJson("parallelism=16")

    val all = Output("all", Seq(
      checkpoint0s,
      checkpoint10s,
      checkPoint30s,
      checkpoint60s,
      redisPipeline,
      redisExecute,
      redisMock,
      p1, p4, p8, p16
    ), reportConfig.sampleInterval / 1000, reportConfig.sampleTimes)
    val checkpointCompare = Output("checkpoint对比", Seq(
      checkpoint0s, checkpoint10s, checkPoint30s, checkpoint60s
    ), reportConfig.sampleInterval / 1000, reportConfig.sampleTimes)
    val redisCompare = Output("redis对比", Seq(
      redisPipeline,
      redisExecute,
      redisMock
    ), reportConfig.sampleInterval / 1000, reportConfig.sampleTimes)
    val parallelismCompare = Output("并行度对比", Seq(p1, p4, p8, p16),
      reportConfig.sampleInterval / 1000, reportConfig.sampleTimes)

    GraphOutput.writeTo(reportConfig.dir, Seq(all, checkpointCompare, redisCompare, parallelismCompare))
    Desktop.getDesktop.open(new File(reportConfig.dir + "/report.html"))
  }

}
