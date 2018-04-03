package uyun.xianglong.sdk.pipeline.test

import java.util

import org.scalatest.{FlatSpec, Matchers}
import org.slf4j.LoggerFactory
import uyun.xianglong.sdk.datastruct.DataCollection
import uyun.xianglong.sdk.node.{PipelineNode, PipelineNodeCategory}
import uyun.xianglong.sdk.node.PipelineNodeCategory.PipelineNodeCategory
import uyun.xianglong.sdk.pipeline.Pipeline
import scala.collection.JavaConversions._


/**
  * Created By wuhuahe
  * author: 游龙
  * Date : 2018-03-28
  * Time : 10:04
  * Desc : 测试pipeline
  */
class PipelineSpec extends FlatSpec with Matchers {

//  "Pipeline initAppContext" should "throw a runtimeException" in {
//    assertThrows[RuntimeException] { // Result type: Assertion
//      try {
//        val examplePipeline = new ExamplePipeline
//        val args = Array[String](
//          "-f",
//          "pipeline_example.json"
//        )
//        examplePipeline.run(args)
//        println(examplePipeline.getName)
//        println(examplePipeline.getAllNodes)
//        println(examplePipeline.getPipelineConfig)
//        examplePipeline.getAllNodes.foreach(node => {
//          println("********************")
//          println(node.getName)
//          println(node.getId)
//          println(node.getPipelineNodeCategory())
//          println(node.getPrevIds)
//          println("**************************")
//        })
//        examplePipeline.getName should be("examplePipeline")
//      } catch {
//        case e: RuntimeException => {
//          e.printStackTrace
//          throw e
//        }
//      }
//    }
//  }

  "Pipeline initAppContext" should "correct" in {
    val examplePipeline = new ExamplePipeline
    val args = Array[String](
      "-f",
      "pipeline_example.json"
    )
    examplePipeline.run(args)
    println(examplePipeline.getName)
    println(examplePipeline.getAllNodes)
    println(examplePipeline.getPipelineConfig)
    examplePipeline.getAllNodes.foreach(node => {
      println("********************")
      println(node.getName)
      println(node.getId)
      println(node.getPipelineNodeCategory())
      println(node.getPrevIds)
      println("**************************")
    })
    examplePipeline.getName should be("examplePipeline")
  }

}

class ExamplePipeline extends Pipeline {

  private val logger = LoggerFactory.getLogger(classOf[ExamplePipeline])

  /**
    *
    */
  override def destroy(): Unit = {
    logger.info("destroy")
  }

  override def execute(): Unit = {
    logger.info("execute")
  }

  override def sortAndValidateNodes(): Unit = {
    logger.info("sortAndValidateNodes")
  }

  /**
    * 配置属性
    */
  override def configure(): Unit = {
    logger.info("configure")
  }
}

class KafkaJsonInputOpExample extends PipelineNode {
  private val logger = LoggerFactory.getLogger(classOf[KafkaJsonInputOpExample])
  /*
        "bootstrap.servers":"10.1.61.106:9192",
        "group.id":"consumerGroup1",
        "topic":"metrics",
        "outputDatasetName":"CONSUME"
   */
  private var bootstrapServers:String = _
  private var groupId:String = _
  private var topic:String =  _
  private var outputDatasetName:String = _
  /**
    * 验证并配置节点参数
    *
    * @param nodeParameters
    */
  override def configure(nodeParameters: util.Map[String, AnyRef]): Unit = {
    logger.info("KafkaJsonInputOpExample:" + nodeParameters)
    // 验证参数
    bootstrapServers = nodeParameters.get("bootstrap.servers").asInstanceOf[String]
    groupId = nodeParameters.get("group.id").asInstanceOf[String]
    topic = nodeParameters.get("topic").asInstanceOf[String]
    outputDatasetName = nodeParameters.get("outputDatasetName").asInstanceOf[String]
  }

  /**
    * 解析操作步骤
    *
    * @param dataCollections
    * @return
    */
  override def parseOperate(dataCollections: DataCollection*): DataCollection = {
    null
  }

  override def getPipelineNodeCategory(): PipelineNodeCategory = PipelineNodeCategory.STREAMING_DATASOURCE_LOAD_NODE
}

case class JoinOnCondition(leftDatasetField:String, rightDatasetField:String, relation:String) extends Serializable

class JoinOperator extends PipelineNode{
  private val logger = LoggerFactory.getLogger(classOf[KafkaJsonInputOpExample])

  override def getPipelineNodeCategory(): PipelineNodeCategory = PipelineNodeCategory.TRANSFORM_NODE

  /*
        "joinType":"innerJoin",
        "joinCondition":[
          {
            "leftDatasetField":"resourceId",
            "rightDatasetField":"resourceId",
            "relation":"="
          },
          {
            "leftDatasetField":"resourceId2",
            "rightDatasetField":"resourceId",
            "relation":"="
          }
        ]

   */
  private var joinType:String = _
  private var joinConditions:List[JoinOnCondition] = _

  /**
    * 验证并配置节点参数
    *
    * @param nodeParameters
    */
  override def configure(nodeParameters: util.Map[String, Object]): Unit = {
    logger.info("KafkaJsonInputOpExample:" + nodeParameters)
    // 验证参数
    joinType = nodeParameters.get("joinType").asInstanceOf[String]
    if(!List[String]("innerJoin","leftOuterJoin","rightOuterJoin").contains(joinType)){
      throw new RuntimeException(s"Not valid joinType [$joinType]")
    }
    val joinConditionsStr = nodeParameters.get("joinCondition").asInstanceOf[util.ArrayList[util.Map[String,String]]]
    println("joinConditionsStr = " + joinConditionsStr)
    joinConditionsStr.foreach(println)
    joinConditionsStr.map(j => {
      val leftDatasetField:String = j.get("leftDatasetField")
      val rightDatasetField: String =   j.get("rightDatasetField")
      val relation: String =   j.get("relation")
      JoinOnCondition(leftDatasetField, rightDatasetField, relation)
    }).foreach(j => println(j))
  }

  /**
    * 解析操作步骤
    *
    * @param dataCollections
    * @return
    */
  override def parseOperate(dataCollections: DataCollection*): DataCollection = {
    null
  }
}


class KafkaJsonOutputOpExample extends PipelineNode {
  private val logger = LoggerFactory.getLogger(classOf[KafkaJsonOutputOpExample])
  private var bootstrapServers:String = _
  private var topic:String =  _
  private var outputDatasetName:String = _
  /**
    * 验证并配置节点参数
    *
    * @param nodeParameters
    */
  override def configure(nodeParameters: util.Map[String, AnyRef]): Unit = {
    logger.info("KafkaJsonOutputOpExample:" + nodeParameters)
    // 验证参数
    bootstrapServers = nodeParameters.get("bootstrap.servers").asInstanceOf[String]
    topic = nodeParameters.get("topic").asInstanceOf[String]
    outputDatasetName = nodeParameters.get("outputDatasetName").asInstanceOf[String]
  }

  /**
    * 解析操作步骤
    *
    * @param dataCollections
    * @return
    */
  override def parseOperate(dataCollections: DataCollection*): DataCollection = {
    null
  }

  override def getPipelineNodeCategory(): PipelineNodeCategory = PipelineNodeCategory.FINAL_OUTPUT_NODE
}

class SqlOperatorExample extends PipelineNode{
  private val logger = LoggerFactory.getLogger(classOf[KafkaJsonOutputOpExample])

  private var sql:String = _
  private  var outputDatasetName:String = _

  override def getPipelineNodeCategory(): PipelineNodeCategory = PipelineNodeCategory.TRANSFORM_NODE

  /**
    * 验证并配置节点参数
    *
    * @param nodeParameters
    */
  override def configure(nodeParameters: util.Map[String, Object]): Unit = {
      sql = nodeParameters.get("sql").asInstanceOf[String]
      outputDatasetName = nodeParameters.get("outputDatasetName").asInstanceOf[String]
    println(s"sql = $sql, outputDatasetName=$outputDatasetName")
  }

  /**
    * 解析操作步骤
    *
    * @param dataCollections
    * @return
    */
  override def parseOperate(dataCollections: DataCollection*): DataCollection = {
    null
  }
}