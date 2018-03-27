package uyun.xianglong.examples.sclaz.flink.table

import java.lang
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
import org.apache.flink.table.sinks.{AppendStreamTableSink, TableSinkBase}
import org.apache.flink.types.Row
import org.apache.flink.util.Collector

import scala.language.implicitConversions

object StreamTopNExample {

  def main(args: Array[String]): Unit = {
    import org.apache.flink.streaming.api.scala.createTypeInformation

    val env = StreamExecutionEnvironment.createLocalEnvironment()
    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)

    val tnv = new StreamTableEnvironment(env, new TableConfig)
    val source = env
      .addSource(new LocalSource)
      .assignAscendingTimestamps(_.time)
    import org.apache.flink.table.api.scala._

    val table = tnv.fromDataStream(source, 'key, 'v, UnresolvedFieldReference("t").rowtime)
    tnv.registerTable("test", table)
    tnv
      .sqlQuery("select key,sum(v) v from test group by hop(t,interval '1' second,interval '5' minute),key")
      .writeToSink(new TopNSink("v", Time.seconds(1), 5))


    env.execute()
  }


  class LocalSink extends AppendStreamTableSink[Row] with TableSinkBase[Row] {
    override def emitDataStream(dataStream: DataStream[Row]): Unit = {
      dataStream.print()
    }

    override protected def copy: TableSinkBase[Row] = this

    override def getOutputType: TypeInformation[Row] = createTypeInformation[Row]
  }

  class TopNFunction(index: Int, n: Int) extends AllWindowFunction[Row, Seq[Row], TimeWindow] with Serializable {

    case class ComparableRow(row: Row, index: Int) extends Comparable[ComparableRow] with Serializable {
      override def compareTo(o: ComparableRow): Int = {
        row.getField(index).asInstanceOf[Comparable[Any]].compareTo(o.row.getField(index))
      }
    }

    override def apply(window: TimeWindow, values: lang.Iterable[Row], out: Collector[Seq[Row]]): Unit = {
      import scala.collection.JavaConversions._
      val topN = values.map(ComparableRow(_, index)).toSeq.sorted.reverse.slice(0, n).map(_.row)
      out.collect(topN)
    }
  }

  class TopNSink(field: String, size: Time, n: Int) extends AppendStreamTableSink[Row] with TableSinkBase[Row] {
    override def emitDataStream(dataStream: DataStream[Row]): Unit = {
      dataStream
        .timeWindowAll(size)
        .apply(new TopNFunction(getFieldNames.indexOf(field), n))
        .print()
    }

    override protected def copy: TableSinkBase[Row] = this

    override def getOutputType: TypeInformation[Row] = createTypeInformation[Row]
  }

  case class TestBean(key: String, v: Int, time: Long) extends Serializable


  class LocalSource extends SourceFunction[TestBean] {
    override def cancel(): Unit = {

    }

    override def run(ctx: SourceFunction.SourceContext[TestBean]): Unit = {
      val random = new Random
      while (true) {
        ctx.collect(TestBean("key" + (random.nextGaussian() * 100).abs.toInt, 1, System.currentTimeMillis()))
        Thread.sleep(100)
      }
    }
  }

}
