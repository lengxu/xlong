package uyun.xianglong.sdk.datastruct

/**
  * Created By wuhuahe
  * author: 游龙
  * Date : 2018-03-20
  * Time : 21:00
  * Desc : 数据集
  */
case class DataCollection(val fieldNames: Array[String], val fieldTypes: Array[DataType.DataType], val values: AnyRef) extends Serializable {

  def getFieldNames:Array[String] = fieldNames

  def getFieldTypes:Array[DataType.DataType] = fieldTypes

  def getValues: AnyRef = values
}

/**
  * 数据类型
  */
object DataType extends Enumeration{
  type DataType = Value
  val STRING,BYTE,SHORT,INTEGER,DOUBLE,FLOAT,LONG,CHAR,DATE,BOOLEAN,BIGINT,BIGDEC,VOID = Value

  def getFieldTypesFromStringArr(fieldTypes: Array[String]): Array[DataType.DataType]={
    fieldTypes.map(f => f match{
      case "STRING" => DataType.STRING
      case "INTEGER" => DataType.INTEGER
      case "BYTE" => DataType.BYTE
      case "SHORT" => DataType.SHORT
      case "DOUBLE" => DataType.DOUBLE
      case "FLOAT" => DataType.FLOAT
      case "LONG" => DataType.LONG
      case "CHAR" => DataType.CHAR
      case "DATE" => DataType.DATE
      case "BOOLEAN" => DataType.BOOLEAN
      case "BIGINT" => DataType.BIGINT
      case "BIGDEC" => DataType.BIGDEC
      case "VOID" => DataType.VOID
      case _ => DataType.STRING
    })
  }
}
