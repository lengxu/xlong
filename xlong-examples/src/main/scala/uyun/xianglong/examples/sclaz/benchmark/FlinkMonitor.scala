package uyun.xianglong.examples.sclaz.benchmark

import java.net.URL
import java.util.concurrent.TimeUnit

import com.alibaba.fastjson.JSON
import com.google.common.io.ByteStreams

object FlinkMonitor {

  def monitor(host: String, port: Int, interval: Long, times: Int, firstWaiting: Long): Seq[Double] = {
    Thread.sleep(firstWaiting)
    0 until times map { _ =>
      val tps = getTps(host, port)
      Thread.sleep(interval)
      tps
    }
  }

  def getTps(host: String, port: Int): Double = {
    val prefix = s"http://$host:$port"
    val json = JSON.parseObject(read(s"$prefix/joboverview"))
    val array = json.getJSONArray("running")
    val jid = array.getJSONObject(0).getString("jid")
    val vertices = JSON.parseObject(read(s"$prefix/jobs/$jid/vertices"))
    val id = vertices.getJSONArray("vertices").getJSONObject(1).getString("id")
    val metricNames = 0 until 8 map (i => s"$i.numRecordsOutPerSecond") mkString ","
    val metrics = JSON.parseArray(read(s"$prefix/jobs/$jid/vertices/$id/metrics?get=$metricNames"))
    val values = 0 until metrics.size() map (i => metrics.getJSONObject(i).getDoubleValue("value"))
    values.sum.toLong
  }

  private def read(url: String): String = {
    val input = new URL(url).openStream()
    val bytes = ByteStreams.toByteArray(input)
    input.close()
    new String(bytes)
  }
}
