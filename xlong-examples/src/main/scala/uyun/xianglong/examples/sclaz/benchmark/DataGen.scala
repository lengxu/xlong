package uyun.xianglong.examples.sclaz.benchmark

import com.google.common.util.concurrent.RateLimiter
import org.json4s.Extraction._
import org.json4s.jackson.JsonMethods._

object DataGen {

  private val config = implicitly[GenerateConfig]

  def writeDetailToRedis(): Unit = RedisUtils.execute { jedis =>
    val pipeline = jedis.pipelined()
    0 until config.deviceCount foreach { i =>
      pipeline.hset("devices", s"device_$i", compact(decompose(ModelDetail(s"device_$i", "8080", s"device_$i comment"))))
    }
    pipeline.sync()
  }


  def writeModelToKafka(): Unit = {
    val limiter = RateLimiter.create(config.rateLimit)
    while (true) {
      0 until config.deviceCount foreach { i =>
        limiter.acquire()
        KafkaUtils.send(compact(decompose(Model(s"device_$i", System.currentTimeMillis(), 100.001, 100.001))))
      }
    }
  }

  def main(args: Array[String]) {
    writeDetailToRedis()
    writeModelToKafka()
  }

}
