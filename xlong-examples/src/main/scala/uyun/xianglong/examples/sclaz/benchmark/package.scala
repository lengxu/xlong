package uyun.xianglong.examples.sclaz

import java.util.concurrent.TimeUnit

import org.json4s.DefaultFormats
import uyun.xianglong.examples.sclaz.benchmark.RedisUtils.RedisConfig

package object benchmark {

  case class KafkaConfig(host: String, port: Int, zkAddress: String, group: String, topic: String) extends Serializable

  case class GenerateConfig(deviceCount: Int, rateLimit: Long) extends Serializable

  case class Model(device: String, timestamp: Long, input: Double, output: Double) extends Serializable

  case class ModelDetail(device: String, port: String, comment: String) extends Serializable

  case class ReportConfig(dir: String, host: String, port: Int, sampleInterval: Long, sampleTimes: Int, firstSampleTime: Long)


  implicit val fmt: DefaultFormats = DefaultFormats

  implicit val redisConfig: RedisConfig = RedisConfig("10.1.51.236")

  implicit val generateConfig: GenerateConfig = GenerateConfig(
    deviceCount = 100000,
    rateLimit = 330000
  )

  implicit val reportConfig: ReportConfig = ReportConfig(
    dir = "D:/Temp/flink_bench",
    host = "server03",
    port = 8081,
    sampleInterval = TimeUnit.SECONDS.toMillis(5),
    sampleTimes = 120,
    firstSampleTime = TimeUnit.MINUTES.toMillis(5)
  )

  implicit val kafkaConfig: KafkaConfig = KafkaConfig(
    host = "10.1.53.65",
    port = 9192,
    zkAddress = "10.1.61.106:2181",
    group = "test",
    topic = "Network"
  )
}
