package uyun.xianglong.sdk.datastruct.test

import org.scalatest.{FlatSpec, Matchers}
import uyun.xianglong.sdk.datastruct.{DataCollect, DataType}

/**
  * Created By wuhuahe
  * author: 游龙
  * Date : 2018-03-22
  * Time : 16:25
  * Desc :
  */
class DataCollectSpec extends FlatSpec with Matchers {
  val fieldNameWithType = Map[String,DataType.DataType](
    "name" -> DataType.STRING,
    "age" -> DataType.INTEGER,
    "gender" -> DataType.CHAR,
    "salary" -> DataType.DOUBLE
  )
  val fieldNames = Array[String](
    "name",
    "age",
    "gender",
    "salary"
  )
  val fieldTypes = Array[DataType.DataType](
    DataType.STRING,
    DataType.INTEGER,
    DataType.CHAR,
    DataType.DOUBLE
  )
  val values = List(
    ("张三",25,'M',7256.21),
    ("李四",35,'M',35656.3),
    ("王五",45,'M',343456.72)
  )
  val dataCollect = DataCollect(fieldNames, fieldTypes,values)

  "getFieldNames" should "return an Array of String" in {
    dataCollect.getFieldNames should be (Array[String]("name","age","gender","salary"))
  }

  "getFieldTypes" should "return an Array of DataType" in {
    dataCollect.getFieldTypes should be (Array[DataType.DataType](
      DataType.STRING,DataType.INTEGER,DataType.CHAR,DataType.DOUBLE
    ))
  }

  "getValues" should "return a list" in {
    dataCollect.getValues.asInstanceOf[List[_]].foreach(println)
  }
}