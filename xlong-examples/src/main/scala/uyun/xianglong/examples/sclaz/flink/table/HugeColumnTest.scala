package uyun.xianglong.examples.sclaz.flink.table

import java.{util => ju}
import javax.script.{ScriptContext, ScriptEngineManager, SimpleScriptContext}

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.datastream.DataStream
import org.apache.flink.streaming.api.functions.source.SourceFunction
import org.apache.flink.streaming.api.scala.{StreamExecutionEnvironment, createTypeInformation}
import org.apache.flink.table.api.TableEnvironment
import org.apache.flink.table.expressions.UnresolvedFieldReference
import org.apache.flink.table.functions.ScalarFunction
import org.apache.flink.table.sinks.{AppendStreamTableSink, TableSinkBase}
import org.apache.flink.types.Row

object HugeColumnTest {
  def main(args: Array[String]) {
    import org.apache.flink.streaming.api.scala.createTypeInformation
    import org.apache.flink.table.api.scala._
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)
    val tnv = TableEnvironment.getTableEnvironment(env)
    tnv.registerFunction("get_value", new GetValue)
    tnv.registerFunction("get_keys", new GetKeys)
    tnv.registerFunction("eval_script",new JavaScriptFunction)
    val source = env.addSource(new ResourceModelSource)
      .assignAscendingTimestamps(_.updateTime)
    val tableSource = tnv.fromDataStream(source, 'id, 'name, 'attributes, 'tags, 'classCode, 'createTime,
      UnresolvedFieldReference("updateTime").rowtime)
    tnv.registerTable("resource_model", tableSource)
    tnv.sqlQuery("select eval_script(attributes,'value.apply(\"" +
      "a1\")') from resource_model")
      .writeToSink(new LocalSink)

    env.execute()
  }

  class GetValue extends ScalarFunction with Serializable {
    def eval(map: Map[Any, Any], key: String): Any = {
      map.getOrElse(key, null)
    }
  }

  class GetKeys extends ScalarFunction with Serializable {
    def eval(map: Map[String, String]): Iterable[String] = {
      map.keys
    }
  }

  class JavaScriptFunction extends ScalarFunction with Serializable {
    private lazy val scriptEngineManager = new ScriptEngineManager
    private lazy val engine = scriptEngineManager.getEngineByName("nashorn")

    def eval(any: Any, script: String): Any = {
      val ctx = new SimpleScriptContext
      ctx.setAttribute("value", any, ScriptContext.ENGINE_SCOPE)
      engine.eval(script, ctx)
    }
  }

  class LocalSink extends AppendStreamTableSink[Row] with TableSinkBase[Row] {
    override def emitDataStream(dataStream: DataStream[Row]): Unit = {
      dataStream.print()
    }

    override protected def copy: TableSinkBase[Row] = this

    override def getOutputType: TypeInformation[Row] = createTypeInformation[Row]
  }

  case class ResourceModel
  (
    id: String,
    name: String,
    attributes: Map[String, String],
    tags: Map[String, String],
    classCode: String,
    createTime: Long,
    updateTime: Long
  )

  class ResourceModelSource extends SourceFunction[ResourceModel] {
    override def cancel(): Unit = {}

    override def run(ctx: SourceFunction.SourceContext[ResourceModel]): Unit = {
      while (true) {
        ctx.collect(ResourceModel("id", "name", Map(
          "a1" -> "b1",
          "a2" -> "b2",
          "a3" -> "b3"
        ), Map("b4" -> "d5"), "test", System.currentTimeMillis(),
          System.currentTimeMillis()))
        Thread.sleep(1000)
      }
    }
  }

}
