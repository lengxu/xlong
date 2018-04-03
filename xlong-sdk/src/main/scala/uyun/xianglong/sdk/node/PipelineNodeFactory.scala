package uyun.xianglong.sdk.node

import java.util

/**
  * Created By wuhuahe
  * author : 游龙
  * Date : 2018-03-27
  * Time : 19:19
  * Desc :
  */
object PipelineNodeFactory {

  @throws[RuntimeException]
  def create(
              nodeName: String,
              tpye: String,
              nodeId: Int,
              prevNodeIds: util.List[Integer],
              nodeParameters: java.util.Map[String,Object]): PipelineNode = {
    val nodeClass: Class[_ <: PipelineNode] = getClass(tpye)
    try {
      val pipelineNode: PipelineNode = nodeClass.newInstance
      pipelineNode.setId(nodeId)
      pipelineNode.setPrevIds(prevNodeIds)
      pipelineNode.setName(nodeName)
      pipelineNode.configure(nodeParameters)
      pipelineNode
    } catch {
      case ex: Exception =>
        throw new RuntimeException("Unable to create PipelineNode: type: " + tpye + ", class: " + nodeClass.getName, ex)
    }
  }

  @throws[RuntimeException]
  private def getClass(`type`: String): Class[_ <: PipelineNode] = {
    val nodeClassName = `type`
    try
      Class.forName(nodeClassName).asInstanceOf[Class[_ <: PipelineNode]]
    catch {
      case ex: Exception =>
        throw new RuntimeException("Unable to load PipelineNode type: " + `type` + ", class: " + nodeClassName, ex)
    }
  }
}
