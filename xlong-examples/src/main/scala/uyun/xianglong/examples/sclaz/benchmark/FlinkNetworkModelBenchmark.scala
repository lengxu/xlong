package uyun.xianglong.examples.sclaz.benchmark

import java.util.Properties

import org.apache.commons.io.Charsets
import org.apache.flink.api.common.functions.RichMapFunction
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment, createTypeInformation}
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010
import org.json4s.Extraction._
import org.json4s.jackson.JsonMethods._
import redis.clients.jedis.Jedis
import uyun.xianglong.examples.sclaz.{JarUtils, Utils}
import uyun.xianglong.examples.sclaz.benchmark.{KafkaConfig, Model, ModelDetail, RedisUtils}

object FlinkNetworkModelBenchmark {

  private val kafka = implicitly[KafkaConfig]

  case class Metric(
                     sendTime: TimeRange = NullRange,
                     mapTime: TimeRange = NullRange,
                     reduceTime: TimeRange = NullRange,
                     joinTime: TimeRange = NullRange
                   ) extends Serializable

  case class SumModel(device: String, input: Double, output: Double) extends Serializable

  case class TimeRange(start: Long = Long.MaxValue, end: Long = Long.MinValue) extends Serializable {
    def merge(other: TimeRange): TimeRange = {
      TimeRange(other.start.min(start), other.end.max(end))
    }
  }

  private val NullRange = TimeRange(Long.MaxValue, Long.MinValue)

  private def current() = TimeRange(System.currentTimeMillis(), System.currentTimeMillis())


  def main(args: Array[String]) {
    benchmark()
  }


  def benchmark(): Unit = {
    Utils.cancelAllJobs("server03", 8081)
    val env = StreamExecutionEnvironment.createRemoteEnvironment("server03", 6123, JarUtils.getJars: _*)
    env.setParallelism(8)
    val properties = new Properties()
    properties.setProperty("bootstrap.servers", s"${kafka.host}:${kafka.port}")
    properties.setProperty("zookeeper.connect", kafka.zkAddress)
    properties.setProperty("group.id", kafka.group)
    val consumer = new FlinkKafkaConsumer010[String](kafka.topic, new SimpleStringSchema(Charsets.UTF_8), properties)
    consumer.setStartFromEarliest()
    val source = env.addSource(consumer)
    doTransform(source)
    env.execute()
  }

  def doTransform(source: DataStream[String]): Unit = {
    source
      .rebalance
      .map(json => mapModel(json))
      .keyBy(_._1.device)
      .timeWindow(Time.seconds(1))
      .reduce((m1, m2) => doSum(m1, m2))
      //      .map(kv => joinFromRedis(kv))
      .map(new RichRedisJoin)
      //      .map(tuple => mapToReport(tuple))
      .map(kv => kv._1.input / 100)
      .timeWindowAll(Time.seconds(1))
      .sum(0)
      .print()
  }

  private def mapToReport(tuple: (SumModel, ModelDetail, Metric)) = {

    val metric = tuple._3

    def calDelay(timeRange: TimeRange): TimeRange = {
      TimeRange(timeRange.start - metric.sendTime.start, timeRange.end - metric.sendTime.end)
    }

    val str = s"count:${tuple._1.output / 100 toLong},map delay:${calDelay(metric.mapTime)},reduce delay:${calDelay(metric.reduceTime)},join delay:${calDelay(metric.joinTime)}"
    println(str)
    str

  }

  private def joinFromRedis(kv: (SumModel, Metric)): (SumModel, ModelDetail, Metric) = {

    val (model, metric) = kv
    //        val modelDetail = extract[ModelDetail](parse(RedisUtils.execute(jedis => jedis.hget("devices", model.device))))
    val modelDetail = ModelDetail("test", "test", "test")
    (model, modelDetail, metric.copy(joinTime = current()))

  }

  private def doSum(m1: (SumModel, Metric), m2: (SumModel, Metric)) = {

    val (sm1, metric1) = m1
    val (sm2, metric2) = m2
    val sumModel = SumModel(sm1.device, sm1.input + sm2.input, sm1.output + sm2.output)

    def getReduceTime(range: TimeRange) = if (range == NullRange) current() else range

    val reduceTime = getReduceTime(metric1.reduceTime).merge(getReduceTime(metric2.reduceTime))
    val metric = Metric(
      sendTime = metric1.sendTime.merge(metric2.sendTime),
      mapTime = metric1.mapTime.merge(metric2.mapTime),
      reduceTime = reduceTime
    )
    (sumModel, metric)

  }

  private def mapModel(json: String) = {
    val model = extract[Model](parse(json))
    val sendTime = TimeRange(model.timestamp, model.timestamp)
    val mapTime = current()
    val sumModel = SumModel(model.device, model.input, model.output)
    (sumModel, Metric(sendTime = sendTime, mapTime = mapTime))
  }


  class RichRedisJoin extends RichMapFunction[(SumModel, Metric), (SumModel, ModelDetail, Metric)] with Serializable {
    private var jedis: Jedis = _

    override def open(parameters: Configuration): Unit = {
      jedis = RedisUtils.pool.getResource
    }

    override def map(value: (SumModel, Metric)): (SumModel, ModelDetail, Metric) = {
      val (model, metric) = value
      val modelDetail = extract[ModelDetail](parse(jedis.hget("devices", model.device)))
      //      val modelDetail = ModelDetail("test", "test", "test")
      (model, modelDetail, metric.copy(joinTime = current()))
    }

    override def close(): Unit = {
      jedis.close()
    }
  }

}
