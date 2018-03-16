package uyun.xianglong.examples.sclaz.flink.table

import java.lang
import java.util.Properties

import com.alibaba.fastjson.JSON
import org.apache.commons.io.Charsets
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.api.common.typeinfo.{TypeInformation, Types}
import org.apache.flink.api.java.typeutils.RowTypeInfo
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010
import org.apache.flink.table.api.{Table, TableEnvironment}
import org.apache.flink.types.Row
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.flink.table.api.scala._
import org.apache.flink.api.scala._

/**
  * Created By wuhuahe
  * author: 游龙
  * Date : 2018-03-15
  * Time : 19:39
  * Desc :
  */
object KafkaSQLExample {

  def main(args: Array[String]): Unit = {

    val env = StreamExecutionEnvironment.getExecutionEnvironment
    val tEnv = TableEnvironment.getTableEnvironment(env)

//    val properties = new Properties()
//    properties.setProperty("bootstrap.servers", "10.1.53.65:9192")
//    properties.setProperty("group.id", "kafkasqlexample")
//    properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
//
//    val consumer010 = new FlinkKafkaConsumer010[String]("consumers", new SimpleStringSchema(Charsets.UTF_8), properties)
//
//    val types = Array[TypeInformation[_]](
//      Types.STRING,
//      Types.DOUBLE)
//    val names =  Array("memberId", "amt")
//
//    implicit val tpe: TypeInformation[Row] = new RowTypeInfo(types, names)
//
//    val rows: DataStream[Row] = env.addSource(consumer010).map(line =>{
//      //val fields: Array[String] = line.split(",")
//      //{"memberId":"00001","amt":2.35}
//      val json = JSON.parseObject(line)
//      val memberId: java.lang.String = json.getString("memberId")
//      val amt: lang.Double = json.getDouble("amt")
//      val row = new Row(2)
//      row.setField(0, memberId)
//      row.setField(1, amt)
//      row
//    })
//
//    val consumers: Table = rows.toTable(tEnv, "consumers", 'memberId, 'amt)

    val types = Array[TypeInformation[_]](
      Types.STRING,
      Types.STRING,
      Types.STRING,
      Types.INT,
      Types.STRING)
    val names =  Array("memberId", "name", "gender", "age", "occupation")

    implicit val tpe: TypeInformation[Row] = new RowTypeInfo(types, names)

    val persons: DataStream[String] = env.readTextFile("F:\\idea_workspace\\xianglong\\xianglong-examples\\datas\\persons.json")
    val ps: DataStream[Row] = persons.map(line =>{
      val json = JSON.parseObject(line)
      //{"memberId":"00001","name":"Merry","gender":"F","age":34,"ocupation":"Doctor"}
      val memberId = json.getString("memberId")
      val name = json.getString("name")
      val gender = json.getString("gender")
      val age: Integer = json.getInteger("age")
      val occupation = json.getString("ocupation")
      Row.of(memberId, name, gender, age, occupation)
    })

    //val ptable = ps.toTable(tEnv, "persons", 'memberId, 'name, 'gender, 'age, 'occupation)
    ps.print()

    //ptable.printSchema()
    //ptable.print()

//    val aggResult: Table = consumers.join(ptable).where('memberId === 'memberId).groupBy('gender, 'age).select('gender, 'age, 'amt.sum, 'amt.avg)
//
//    aggResult.printSchema()

    env.execute()

  }


}
