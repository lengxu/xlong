package uyun.xianglong.sdk.datastruct.test

import java.util

import org.scalatest.{FlatSpec, Matchers}
import uyun.xianglong.sdk.datastruct.DataCollection
import uyun.xianglong.sdk.node.{PipelineNode, PipelineNodeFactory, PipelineNodeParameter}

/**
  * Created By wuhuahe
  * author: 游龙
  * Date : 2018-03-27
  * Time : 19:44
  * Desc :
  */
class PipelineNodeFactorySpec extends FlatSpec with Matchers{

  " create " should " return a corrent instance " in {
    val nodeId: Int = 0
    val prevNodeIds: util.List[Integer] = new util.ArrayList[Integer]()
    prevNodeIds.add(-1)
    val pipelineNodeCategory: String = "STREAMING_DATASOURCE_LOAD_NODE"
    val nodeName: String = "examplePipelineNode"
    val tpye: String = "uyun.xianglong.sdk.datastruct.test.ExamplePipelineNode"
    val pipelineNode = PipelineNodeFactory.create(nodeId, prevNodeIds, pipelineNodeCategory, nodeName, tpye)
    println(pipelineNode.getPipelineNodeCategory())
    pipelineNode.getName should be ("examplePipelineNode")
  }

}

class ExamplePipelineNode extends PipelineNode{
  /**
    * 验证并配置节点参数
    *
    * @param nodeParameters
    */
  override def configure(nodeParameters: util.Map[String, Object], pipelineNodeParameters: List[PipelineNodeParameter]): Unit = {

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
