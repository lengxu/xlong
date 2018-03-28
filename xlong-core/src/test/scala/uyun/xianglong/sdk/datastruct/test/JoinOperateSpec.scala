package uyun.xianglong.sdk.datastruct.test

import org.apache.flink.api.common.typeinfo.{BasicTypeInfo, TypeInformation}
import org.apache.flink.api.java.typeutils.RowTypeInfo
import org.apache.flink.api.java.utils.ParameterTool
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.table.api.TableEnvironment
import org.apache.flink.types.Row
import org.scalatest.{FlatSpec, Matchers}
import uyun.xianglong.sdk.datastruct.{DataCollection, DataType}

class JoinOperateSpec extends FlatSpec with Matchers{
  "join" should "  " in {
    // parse the parameters
    val params = ParameterTool.fromArgs(Array[String]())
    val windowSize = params.getLong("windowSize", 2000)
    val rate = params.getLong("rate", 3)
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    val tEnv = TableEnvironment.getTableEnvironment(env)

    env.setStreamTimeCharacteristic(TimeCharacteristic.IngestionTime)

    // make parameters available in the web interface
    env.getConfig.setGlobalJobParameters(params)

    val ps = getPersons(env)
    val css = getConsumers(env)

    val psStream = ps.values.asInstanceOf[DataStream[Row]]
    val cssStream  = css.values.asInstanceOf[DataStream[Row]]

//    implicit val typeInfo = TypeInformation.of(classOf[String])
    val joinStream: DataStream[Row] = cssStream.join(psStream)
        .where(row => row.getField(0).asInstanceOf[String])(TypeInformation.of(classOf[String]))
      .equalTo(r => r.getField(0).asInstanceOf[String])
      .window(TumblingEventTimeWindows.of(Time.milliseconds(windowSize)))
        .apply((r1, r2) => {
          val memberId = r1.getField(0)
          val amt = r1.getField(1)
          val name = r2.getField(1)
          val gender = r2.getField(2)
          val age = r2.getField(3)
          val ocupation = r2.getField(4)
          Row.of(memberId,amt, name,gender,age, ocupation)
        })(TypeInformation.of(classOf[Row]))
    joinStream.print()
//    psStream.print()
//    cssStream.print()
    env.execute()

  }

  def getPersons(env: StreamExecutionEnvironment):DataCollection ={
    val persons: DataStream[String] = env.readTextFile("F:\\idea_workspace\\xlong\\xlong-examples\\datas\\persons.json")
    val types  = Array[TypeInformation[_]](
      BasicTypeInfo.STRING_TYPE_INFO
    )
    val names = Array[String](
      "text"
    )
    val pee: DataStream[Row] = persons.map(line => Row.of(line))(new RowTypeInfo(types, names))
    val jsonParserOp = new JsonParserOperate(env, Array[String]("memberId","name","gender","age","ocupation"),
      Array[String]("STRING","STRING","STRING","DOUBLE","STRING"))
    jsonParserOp.process(DataCollection(names, Array[DataType.DataType](DataType.STRING), pee))
  }

  def getConsumers(env: StreamExecutionEnvironment):DataCollection ={
    val consumes: DataStream[String] = env.readTextFile("F:\\idea_workspace\\xlong\\xlong-examples\\datas\\consumes.json")
    val types  = Array[TypeInformation[_]](
      BasicTypeInfo.STRING_TYPE_INFO
    )
    val names = Array[String](
      "text"
    )
    val css: DataStream[Row] = consumes.map(line => Row.of(line))(new RowTypeInfo(types, names))
    val jsonParserOp = new JsonParserOperate(env, Array[String]("memberId","amt"),
      Array[String]("STRING","DOUBLE"))
    jsonParserOp.process(DataCollection(names, Array[DataType.DataType](DataType.STRING), css))
  }
}
