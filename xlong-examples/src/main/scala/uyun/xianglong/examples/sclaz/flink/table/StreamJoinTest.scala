package uyun.xianglong.examples.sclaz.flink.table

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.datastream.DataStream
import org.apache.flink.streaming.api.functions.source.SourceFunction
import org.apache.flink.streaming.api.scala.{StreamExecutionEnvironment, createTypeInformation}
import org.apache.flink.table.api.TableEnvironment
import org.apache.flink.table.api.scala.{StreamTableEnvironment, _}
import org.apache.flink.table.expressions.UnresolvedFieldReference
import org.apache.flink.table.sinks.{AppendStreamTableSink, TableSinkBase}
import org.apache.flink.types.Row

object StreamJoinTest {

  case class LModel(name: String, age: Int, time: Long)

  case class RModel(name: String, email: String, time: Long)


  val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
  env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)
  val tnv: StreamTableEnvironment = TableEnvironment.getTableEnvironment(env)
  tnv.registerTable("l_model",
    tnv.fromDataStream(
      env.addSource(new LModelSource)
        .assignAscendingTimestamps(_.time),
      'name, 'age, UnresolvedFieldReference("time").rowtime
    )
  )
  tnv.registerTable("r_model",
    tnv.fromDataStream(env
      .addSource(new RModelSource)
      .assignAscendingTimestamps(_.time),
      'name, 'email, UnresolvedFieldReference("time").rowtime)
  )


  class RModelSource extends SourceFunction[RModel] {
    override def cancel(): Unit = {

    }

    override def run(ctx: SourceFunction.SourceContext[RModel]): Unit = {
      while (true) {
        ctx.collect(RModel("test", "test@qq.com", System.currentTimeMillis()))
        Thread.sleep(1000)
      }
    }
  }

  class LModelSource extends SourceFunction[LModel] {
    override def cancel(): Unit = {

    }

    override def run(ctx: SourceFunction.SourceContext[LModel]): Unit = {
      while (true) {
        ctx.collect(LModel("test", 12, System.currentTimeMillis()))
        Thread.sleep(1000)
      }
    }
  }


  class LocalSink extends AppendStreamTableSink[Row] with TableSinkBase[Row] {
    override def emitDataStream(dataStream: DataStream[Row]): Unit = {
      dataStream.print()
    }

    override protected def copy: TableSinkBase[Row] = this

    override def getOutputType: TypeInformation[Row] = createTypeInformation[Row]
  }


  def alias(): Unit = {
    val sql = s"select `name` as name2,`email`,`time` from model"
    println(sql)
    tnv.sqlQuery(sql)
      .printSchema()
  }

  class Tran

  def main(args: Array[String]): Unit = {
    tnv.sqlQuery("SELECT a.name,a.age,b.email,a.`time`,b.`time` from l_model a JOIN r_model b ON a.name=b.name " +
      "WHERE a.`time` BETWEEN b.`time` - INTERVAL '10' SECOND AND b.`time` + INTERVAL '10' SECOND")
      .window(Over partitionBy 'c orderBy 'rowTime preceding 10.seconds as 'ow)
      .select('s.sum over 'te)
      .writeToSink(new LocalSink)

    env.execute()
  }
}
