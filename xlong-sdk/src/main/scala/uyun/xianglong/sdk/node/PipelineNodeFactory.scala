package uyun.xianglong.sdk.node

import java.util

import com.google.common.base.Preconditions
import org.slf4j.LoggerFactory

/**
  * Created By wuhuahe
  * author : 游龙
  * Date : 2018-03-27
  * Time : 19:19
  * Desc :
  */
object PipelineNodeFactory {

  @throws[PipelineNodeException]
  def create(nodeId: Int, prevNodeIds: util.List[Integer], pipelineNodeCategory: String, nodeName: String, tpye: String): PipelineNode = {
    val nodeClass: Class[_ <: PipelineNode] = getClass(tpye)
    try {
      val pipelineNode: PipelineNode = nodeClass.newInstance
      pipelineNode.setId(nodeId)
      pipelineNode.setPrevIds(prevNodeIds)
      pipelineNode.setPipelineNodeCategory(PipelineNodeCategory.getPipelineNodeCategory(pipelineNodeCategory))
      pipelineNode.setName(nodeName)
      pipelineNode
    } catch {
      case ex: Exception =>
        throw new PipelineNodeException("Unable to create PipelineNode: type: " + tpye + ", class: " + nodeClass.getName, ex)
    }
  }

  @throws[PipelineNodeException]
  private def getClass(`type`: String): Class[_ <: PipelineNode] = {
    val nodeClassName = `type`
    try
      Class.forName(nodeClassName).asInstanceOf[Class[_ <: PipelineNode]]
    catch {
      case ex: Exception =>
        throw new PipelineNodeException("Unable to load PipelineNode type: " + `type` + ", class: " + nodeClassName, ex)
    }
  }
}
