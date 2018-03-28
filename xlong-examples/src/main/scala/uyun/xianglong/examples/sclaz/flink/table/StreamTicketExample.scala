package uyun.xianglong.examples.sclaz.flink.table

import java.lang
import java.sql.Timestamp
import java.util.Random

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.datastream.DataStream
import org.apache.flink.streaming.api.functions.source.SourceFunction
import org.apache.flink.streaming.api.functions.windowing.AllWindowFunction
import org.apache.flink.streaming.api.scala.{StreamExecutionEnvironment, createTypeInformation}
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.apache.flink.table.api.TableConfig
import org.apache.flink.table.api.scala.StreamTableEnvironment
import org.apache.flink.table.expressions.UnresolvedFieldReference
import org.apache.flink.table.functions.{AggregateFunction, ScalarFunction}
import org.apache.flink.table.sinks.{AppendStreamTableSink, TableSinkBase}
import org.apache.flink.types.Row
import org.apache.flink.util.Collector

import scala.collection.mutable

object StreamTicketExample {

  def main(args: Array[String]) {
    import org.apache.flink.streaming.api.scala.createTypeInformation

    val env = StreamExecutionEnvironment.createLocalEnvironment()
    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)

    val tnv = new StreamTableEnvironment(env, new TableConfig)
    tnv.registerFunction("last_item", new TimestampLastAgg)
    tnv.registerFunction("to_long", new CastTimeToLong)
    val source = env
      .addSource(new LocalSource)
      .assignAscendingTimestamps(_.updateTime)
    import org.apache.flink.table.api.scala._

    val table = tnv.fromDataStream(source, 'ticket, 'state, 'createTime, UnresolvedFieldReference("updateTime").rowtime)
    tnv.registerTable("test", table)
    tnv
      .sqlQuery("select ticket,last_item(state,updateTime) state,last_item(to_long(updateTime)-createTime,updateTime) duration " +
        "from test group by hop(updateTime,interval '1' second,interval '5' second),ticket")
      .writeToSink(new PrintSink())
    env.execute()
  }

  case class LastAccumulator(var value: Any, var time: Long)

  class CastTimeToLong extends ScalarFunction with Serializable {
    def eval(timestamp: Timestamp): Long = timestamp.getTime
  }

  class TimestampLastAgg extends AggregateFunction[Any, LastAccumulator] with Serializable {

    def accumulate(acc: LastAccumulator, value: Any, time: Timestamp): Unit = {
      if (acc.time < time.getTime) {
        acc.time = time.getTime
        acc.value = value
      }
    }


    override def createAccumulator(): LastAccumulator = {
      LastAccumulator(null, Long.MinValue)
    }

    override def getValue(accumulator: LastAccumulator): Any = accumulator.value
  }

  class RateCountFunction() extends AllWindowFunction[Row, String, TimeWindow] with Serializable {
    override def apply(window: TimeWindow, values: lang.Iterable[Row], out: Collector[String]): Unit = {
      import scala.collection.JavaConversions._
      val tickets = values.toSeq
      val state0Ticket = tickets.filter(_.getField(1) == 0).map(ticket => ticket.getField(0)).mkString(",")
      val state1Ticket = tickets.filter(_.getField(1) == 1).map(ticket => ticket.getField(0) -> ticket.getField(2)).mkString(",")
      out.collect(s"status0:$state0Ticket")
      out.collect(s"status1:$state1Ticket")
    }
  }


  class PrintSink() extends AppendStreamTableSink[Row] with TableSinkBase[Row] {
    override def emitDataStream(dataStream: DataStream[Row]): Unit = {
      dataStream
        .timeWindowAll(Time.seconds(1))
        .apply(new RateCountFunction)
        .print()
    }

    override protected def copy: TableSinkBase[Row] = this

    override def getOutputType: TypeInformation[Row] = createTypeInformation[Row]
  }


  case class Ticket(ticket: String, state: Int, createTime: Long, updateTime: Long) extends Serializable

  case class TicketMetadata(status: Int, time: Long)

  class LocalSource extends SourceFunction[Ticket] {
    override def cancel(): Unit = {

    }

    override def run(ctx: SourceFunction.SourceContext[Ticket]): Unit = {
      val random = new Random

      val stateMap = mutable.Map[String, TicketMetadata]()

      //随机发出工单，一旦工单只会从未完成->已完成状态转换,当一个工单完成了，会创建一个新的工单
      val totalTicketCount = 20
      var count = 0
      0 until totalTicketCount foreach { i =>
        val id = "ticket" + i
        stateMap += id -> TicketMetadata(-1, -1)
      }
      while (true) {
        val (id, meta) = stateMap.toList.apply(random.nextInt(20))
        if (meta.status == -1) {
          val createTime = System.currentTimeMillis()
          stateMap(id) = TicketMetadata(0, createTime)
          ctx.collect(Ticket(id, 0, createTime, System.currentTimeMillis()))
        } else if (meta.status == 0) {
          if (random.nextDouble() > 0.6) {
            stateMap.remove(id)
            stateMap += ("ticket" + (totalTicketCount + count)) -> TicketMetadata(-1, -1)
            count += 1
            ctx.collect(Ticket(id, 1, meta.time, System.currentTimeMillis()))
          }
        }
        Thread.sleep(500)
      }
    }
  }

}
