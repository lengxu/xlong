package uyun.xianglong.examples.sclaz

import java.net.URL

import com.alibaba.fastjson.JSON
import com.google.common.io.ByteStreams

object Utils {
  def cancelAllJobs(host: String, port: Int): Unit = {
    val input = new URL(s"http://$host:$port/joboverview").openStream()
    val bytes = ByteStreams.toByteArray(input)
    val json = JSON.parseObject(new String(bytes))
    val array = json.getJSONArray("running")
    0 until array.size() foreach { i =>
      val jid = array.getJSONObject(i).getString("jid")
      println(s"close $jid")
      new URL(s"http://$host:$port/jobs/$jid/yarn-cancel").openStream().close()
    }
    input.close()
  }

  def main(args: Array[String]) {
    cancelAllJobs("10.1.62.236", 8081)
  }


}
