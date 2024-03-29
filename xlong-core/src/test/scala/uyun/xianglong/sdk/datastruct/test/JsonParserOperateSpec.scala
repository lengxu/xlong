package uyun.xianglong.sdk.datastruct.test

import java.util

import com.google.gson.Gson
import org.apache.flink.api.common.typeinfo.{BasicTypeInfo, TypeInformation, Types}
import org.apache.flink.api.java.typeutils.RowTypeInfo
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.table.api.{Table, TableEnvironment}
import org.apache.flink.types.Row
import org.scalatest.{FlatSpec, Matchers}
import uyun.xianglong.sdk.datastruct.{DataCollection, DataType}

import scala.language.implicitConversions

/**
  * Created By wuhuahe
  * author: 游龙
  * Date : 2018-03-22
  * Time : 16:37
  * Desc : 本地json文件输入操作
  */
class JsonParserOperate(val env: StreamExecutionEnvironment, val fieldNames:Array[String], val fieldTypes:Array[String]) {

  def process(dataCollect:DataCollection): DataCollection ={
    val persons: DataStream[Row] = dataCollect.values.asInstanceOf[DataStream[Row]]
    val types:Array[TypeInformation[_]]  = fieldTypes.map(t => {
      t match {
        case "STRING" => BasicTypeInfo.STRING_TYPE_INFO
        case "BYTE" => BasicTypeInfo.BYTE_TYPE_INFO
        case "SHORT" => BasicTypeInfo.SHORT_TYPE_INFO
        case "INTEGER" => BasicTypeInfo.INT_TYPE_INFO
        case "DOUBLE" => BasicTypeInfo.DOUBLE_TYPE_INFO
        case "FLOAT" => BasicTypeInfo.FLOAT_TYPE_INFO
        case "LONG" => BasicTypeInfo.LONG_TYPE_INFO
        case "CHAR" => BasicTypeInfo.CHAR_TYPE_INFO
        case "DATE" => BasicTypeInfo.DATE_TYPE_INFO
        case "BOOLEAN" => BasicTypeInfo.BOOLEAN_TYPE_INFO
        case "BIGINT" => BasicTypeInfo.BIG_INT_TYPE_INFO
        case "BIGDEC" => BasicTypeInfo.BIG_DEC_TYPE_INFO
        case "VOID" => BasicTypeInfo.VOID_TYPE_INFO
        case _ => BasicTypeInfo.STRING_TYPE_INFO
      }
    })
//    implicit val tpe: TypeInformation[Row] = new RowTypeInfo(types, fieldNames)
    val innerFieldNames = fieldNames
    val innerFieldTypes = fieldTypes
    val fieldNamesWithTypes = fieldNames.zip(fieldTypes)
    val ps: DataStream[Row] = persons.map(row =>{
      val json = new Gson().fromJson(row.getField(0).asInstanceOf[String], classOf[util.Map[String, Object]])
      //{"memberId":"00001","name":"Merry","gender":"F","age":34,"ocupation":"Doctor"}
      var newRow = new Row(innerFieldNames.length)
      for(((fieldName,fieldType),index) <- fieldNamesWithTypes.zipWithIndex) yield {
        val v = json.get(fieldName)
//        fieldType match {
//          case "STRING" => newRow.setField(index ,v.asInstanceOf[String])
//          case "BYTE" => newRow.setField(index ,v.asInstanceOf[Byte])
//          case "SHORT" => newRow.setField(index ,v.asInstanceOf[Short])
//          case "INTEGER" => newRow.setField(index ,v.asInstanceOf[Double])
//          case "DOUBLE" => newRow.setField(index ,v.asInstanceOf[Double])
//          case "FLOAT" => newRow.setField(index ,v.asInstanceOf[Float])
//          case "LONG" => newRow.setField(index ,v.asInstanceOf[Long])
//          case "CHAR" => newRow.setField(index ,v.asInstanceOf[Char])
//          case "DATE" => newRow.setField(index ,v.asInstanceOf[Date])
//          case "BOOLEAN" => newRow.setField(index ,v.asInstanceOf[Boolean])
//          case "BIGINT" => newRow.setField(index ,v.asInstanceOf[java.math.BigInteger])
//          case "BIGDEC" => newRow.setField(index ,v.asInstanceOf[java.math.BigDecimal])
//          case "VOID" => newRow.setField(index ,v.asInstanceOf[Void])
//          case _ => newRow.setField(index ,v.asInstanceOf[String])
//        }
        newRow.setField(index, v)
      }
      newRow
    })(new RowTypeInfo(types, fieldNames))

    DataCollection(fieldNames,DataType.getFieldTypesFromStringArr(fieldTypes), ps)
  }
}

class JsonParserOperateSpec extends FlatSpec with Matchers{
  "process" should "  jsonParserOp   " in {
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    val tEnv = TableEnvironment.getTableEnvironment(env)

    val persons: DataStream[String] = env.readTextFile("F:\\idea_workspace\\xlong\\xlong-examples\\datas\\persons.json")
    val types  = Array[TypeInformation[_]](
      Types.STRING
    )
    val names = Array[String](
      "text"
    )
//    implicit val tpe: TypeInformation[Row] = new RowTypeInfo(types, names)
//    val stringInfo: TypeInformation[String] = createTypeInformation[String]
    val pee: DataStream[Row] = persons.map(line => Row.of(line))(Types.ROW_NAMED(names,types:_*))
    val jsonParserOp = new JsonParserOperate(env, Array[String]("memberId","name","gender","age","ocupation"),
      Array[String]("STRING","STRING","STRING","DOUBLE","STRING"))
    val dataCollect = jsonParserOp.process(DataCollection(Array[String]("text"), Array[DataType.DataType](DataType.STRING), pee))
    dataCollect.getFieldNames.foreach(println)
    dataCollect.getFieldTypes.foreach(println)

    val ps = dataCollect.values.asInstanceOf[DataStream[Row]]
    ps.print()
    tEnv.registerDataStream("persons",ps)
    val t: Table = tEnv.sqlQuery("SELECT avg(age) as age_avg,gender from persons GROUP BY gender")
    t.printSchema()
    val result = tEnv.toRetractStream(t)(new RowTypeInfo(Array[TypeInformation[_]](
      BasicTypeInfo.DOUBLE_TYPE_INFO,BasicTypeInfo.STRING_TYPE_INFO), Array[String]("age_avg","gender")))
    result.printToErr()
//    ps.print()

    env.execute()
  }
}
