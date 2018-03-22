package uyun.xianglong.examples.sclaz.benchmark

import java.awt.Desktop
import java.io.File
import java.util.Properties
import java.util.concurrent.TimeUnit

import com.google.common.io.Files
import org.apache.commons.io.Charsets
import org.apache.flink.api.common.functions.RichMapFunction
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.CheckpointingMode
import org.apache.flink.streaming.api.scala.function.AllWindowFunction
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment, createTypeInformation}
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010
import org.apache.flink.util.Collector
import org.json4s.Extraction._
import org.json4s.jackson.JsonMethods._
import org.slf4j.LoggerFactory
import redis.clients.jedis.Jedis
import uyun.xianglong.examples.sclaz.benchmark.GraphOutput._
import uyun.xianglong.examples.sclaz.{JarUtils, Utils}


object FlinkNetworkModelBenchmark {

  private val kafka = implicitly[KafkaConfig]

  private val logger = LoggerFactory.getLogger(getClass)

  object RedisMode extends Enumeration {
    val PIPELINE, EXECUTE, MOCK = Value
  }

  case class FlinkBenchmarkConfig
  (
    checkPointInterval: Long = -1,
    redisMode: RedisMode.Value = RedisMode.MOCK,
    parallelism: Int = 8
  ) extends Serializable

  case class Metric(
                     sendTime: TimeRange = NullRange,
                     mapTime: TimeRange = NullRange,
                     reduceTime: TimeRange = NullRange,
                     joinTime: TimeRange = NullRange
                   ) extends Serializable

  case class SumModel(device: String, input: Double, output: Double) extends Serializable

  case class TimeRange(start: Long, end: Long) extends Serializable {
    def merge(other: TimeRange): TimeRange = {
      TimeRange(other.start.min(start), other.end.max(end))
    }
  }

  private val NullRange = TimeRange(Long.MaxValue, Long.MinValue)

  private def current() = TimeRange(System.currentTimeMillis(), System.currentTimeMillis())

  private val reportConfig = implicitly[ReportConfig]

  def main(args: Array[String]) {
    Thread.sleep(TimeUnit.HOURS.toMillis(2))
    Utils.cancelAllJobs(reportConfig.host, reportConfig.port)
    val checkpoint0s = startBenchmark("checkpoint=-1", FlinkBenchmarkConfig(checkPointInterval = -1))
    val checkpoint10s = startBenchmark("checkpoint=10s", FlinkBenchmarkConfig(checkPointInterval = TimeUnit.SECONDS.toMillis(10)))
    val checkPoint30s = startBenchmark("checkpoint=30s", FlinkBenchmarkConfig(checkPointInterval = TimeUnit.SECONDS.toMillis(30)))
    val checkpoint60s = startBenchmark("checkpoint=60s", FlinkBenchmarkConfig(checkPointInterval = TimeUnit.SECONDS.toMillis(60)))
    val redisPipeline = startBenchmark("redisMode=pipeline", FlinkBenchmarkConfig(redisMode = RedisMode.PIPELINE))
    val redisExecute = startBenchmark("redisMode=execute", FlinkBenchmarkConfig(redisMode = RedisMode.EXECUTE))
    val redisMock = startBenchmark("redisMode=mock", FlinkBenchmarkConfig(redisMode = RedisMode.MOCK))
    val p1 = startBenchmark("parallelism=1", FlinkBenchmarkConfig(parallelism = 1))
    val p4 = startBenchmark("parallelism=4", FlinkBenchmarkConfig(parallelism = 4))
    val p8 = startBenchmark("parallelism=8", FlinkBenchmarkConfig(parallelism = 8))
    val p16 = startBenchmark("parallelism=16", FlinkBenchmarkConfig(parallelism = 16))

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

  def writeAsJson(group: PointGroup): Unit = {
    Files.write(compact(decompose(group)).getBytes(), new File(reportConfig.dir + "/" + group.title))
  }

  def readJson(title: String): PointGroup = {
    extract[PointGroup](parse(Files.asCharSource(new File(reportConfig.dir + "/" + title), Charsets.UTF_8).read()))
  }

  private def toGroup(title: String, metrics: Seq[Double]): PointGroup = {
    PointGroup(title, metrics.map(metric => metric: Point))
  }

  def startBenchmark(title: String, config: FlinkBenchmarkConfig): PointGroup = {
    logger.info(s"Start $title benchmark")
    new Thread() {
      override def run(): Unit = {
        benchmark(config)
      }
    }.start()
    val data = FlinkMonitor.monitor(reportConfig.host, 8081, reportConfig.sampleInterval, reportConfig.sampleTimes, reportConfig.firstSampleTime)
    val group = toGroup(title, data)
    writeAsJson(group)
    group
  }


  def benchmark(config: FlinkBenchmarkConfig): Unit = {
    Utils.cancelAllJobs(reportConfig.host, 8081)
    val env = StreamExecutionEnvironment.createRemoteEnvironment(reportConfig.host, 6123, JarUtils.getJars: _*)
    if (config.checkPointInterval > 0) {
      env.enableCheckpointing(config.checkPointInterval, CheckpointingMode.EXACTLY_ONCE)
    }
    env.setParallelism(config.parallelism)
    val properties = new Properties()
    properties.setProperty("bootstrap.servers", s"${kafka.host}:${kafka.port}")
    properties.setProperty("zookeeper.connect", kafka.zkAddress)
    properties.setProperty("group.id", kafka.group)
    val consumer = new FlinkKafkaConsumer010[String](kafka.topic, new SimpleStringSchema(Charsets.UTF_8), properties)
    consumer.setStartFromEarliest()
    val source = env.addSource(consumer)
    doTransform(source, config)
    env.execute()
  }

  def doTransform(source: DataStream[String], config: FlinkBenchmarkConfig): Unit = {
    val sumModel: DataStream[(SumModel, Metric)] = source
      .rebalance
      .map(json => mapModel(json))
      .keyBy(_._1.device)
      .timeWindow(Time.seconds(1))
      .reduce((m1, m2) => doSum(m1, m2))
    val joinModel = config.redisMode match {
      case RedisMode.MOCK =>
        sumModel.map(kv => joinFromMockRedis(kv))
      case RedisMode.EXECUTE =>
        sumModel.map(new RichRedisJoin)
      case RedisMode.PIPELINE =>
        sumModel
          .timeWindowAll(Time.seconds(1))
          .apply(new JedisPipelineJoin)
    }
    joinModel
      .map(kv => kv._1.input / 100)
      .timeWindowAll(Time.seconds(1))
      .sum(0)
      .print()
  }

  class JedisPipelineJoin extends AllWindowFunction[(SumModel, Metric), (SumModel, ModelDetail, Metric), TimeWindow] {
    override def apply(window: TimeWindow, input: Iterable[(SumModel, Metric)], out: Collector[(SumModel, ModelDetail, Metric)]): Unit = {
      RedisUtils.execute(jedis => {
        val pipeline = jedis.pipelined()
        val toCollect = input.map { kv =>
          val (model, metric) = kv
          val result = pipeline.hget("devices", model.device)
          (model, result, metric)
        }
        pipeline.syncAndReturnAll()
        toCollect.foreach(tuple => {
          val (model, result, metric) = tuple
          val modelDetail = extract[ModelDetail](parse(result.get()))
          out.collect(model, modelDetail, metric)
        })
      })
    }
  }

  private def mapToReport(tuple: (SumModel, ModelDetail, Metric)) = {

    val metric = tuple._3

    def calDelay(timeRange: TimeRange): TimeRange = {
      TimeRange(timeRange.start - metric.sendTime.start, timeRange.end - metric.sendTime.end)
    }

    val str = s"count:${(tuple._1.output / 100).toLong},map delay:${calDelay(metric.mapTime)},reduce delay:${calDelay(metric.reduceTime)},join delay:${calDelay(metric.joinTime)}"
    println(str)
    str

  }

  private def joinFromMockRedis(kv: (SumModel, Metric)): (SumModel, ModelDetail, Metric) = {

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
      logger.info("open jedis")
      jedis = RedisUtils.pool.getResource
    }

    override def map(value: (SumModel, Metric)): (SumModel, ModelDetail, Metric) = {
      val (model, metric) = value
      val modelDetail = extract[ModelDetail](parse(jedis.hget("devices", model.device)))
      (model, modelDetail, metric.copy(joinTime = current()))
    }

    override def close(): Unit = {
      jedis.close()
    }
  }

}
