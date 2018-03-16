package uyun.xianglong.examples.sclaz.benchmark

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

import scala.collection.JavaConversions._

object KafkaUtils {
  private val config: KafkaConfig = implicitly[KafkaConfig]
  private lazy val producer = new KafkaProducer[String, String](Map(
    "bootstrap.servers" -> s"${config.host}:${config.port}",
    "zookeeper.connect" -> config.zkAddress,
    "key.serializer" -> "org.apache.kafka.common.serialization.StringSerializer",
    "value.serializer" -> "org.apache.kafka.common.serialization.StringSerializer",
    "key.deserializer" -> "org.apache.kafka.common.serialization.StringDeserializer",
    "value.deserializer" -> "org.apache.kafka.common.serialization.StringDeserializer",
    "group.id" -> config.group
  ))

  def send(json: String): Unit = {
    producer.send(new ProducerRecord[String, String](config.topic, json))
  }
}
