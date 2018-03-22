package uyun.xianglong.examples.sclaz.flink.table

import com.alibaba.fastjson.JSON
import org.apache.flink.api.common.typeinfo.{BasicTypeInfo, TypeInformation}
import org.apache.flink.api.java.typeutils.RowTypeInfo
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.table.api.{Table, TableEnvironment}
import org.apache.flink.types.Row
import org.apache.flink.table.api.scala._

/**
  * Created By wuhuahe
  * author: 游龙
  * Date : 2018-03-15
  * Time : 19:39
  * Desc :
  */
object KafkaSQLExample {

  def main(args: Array[String]): Unit = {

    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    val tEnv = TableEnvironment.getTableEnvironment(env)

    val persons: DataStream[String] = env.readTextFile("F:\\idea_workspace\\xlong\\xlong-examples\\datas\\persons.json")

    val types  = Array[TypeInformation[_]](
      BasicTypeInfo.STRING_TYPE_INFO,
      BasicTypeInfo.STRING_TYPE_INFO,
      BasicTypeInfo.STRING_TYPE_INFO,
      BasicTypeInfo.INT_TYPE_INFO,
      BasicTypeInfo.STRING_TYPE_INFO
    )
    val names = Array[String](
      "memberId","name","gender","age","ocupation"
    )
    implicit val tpe: TypeInformation[Row] = new RowTypeInfo(types, names)

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


    val t: Table = ps.toTable(tEnv,'memberId, 'name, 'gender, 'age ,'ocupation)

    t.printSchema()
    t.getSchema.getColumnNames.foreach(println)

    ps.print()


    env.execute()

  }


}
