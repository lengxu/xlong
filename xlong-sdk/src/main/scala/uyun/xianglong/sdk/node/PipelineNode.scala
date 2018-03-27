package uyun.xianglong.sdk.node

import uyun.xianglong.sdk.datastruct.DataCollection
import uyun.xianglong.sdk.node.PipelineNodeCategory.PipelineNodeCategory
import uyun.xianglong.sdk.{NamedComponent, OrderedComponent}

/**
  * Created By wuhuahe
  * author : 游龙
  * Date : 2018-03-24
  * Time : 20:52
  * Desc : 管道节点（一种操作）
  */
trait PipelineNode extends Comparable[PipelineNode] with NamedComponent with OrderedComponent{

  protected var name:String = _
  protected var nodeId:Int = _
  protected var prevNodeIds:java.util.List[Integer] = _
  protected var pipelineNodeCategory:PipelineNodeCategory = _


  override def setName(name: String): Unit = {
    this.name = name
  }
  override def getName: String = name

  override def getId: Int = nodeId

  override def getPrevIds: java.util.List[Integer] = prevNodeIds

  override def setId(id: Int): Unit = {
    this.nodeId = id
  }

  override def setPrevIds(prevIds: java.util.List[Integer]): Unit = {
    this.prevNodeIds = prevIds
  }

  def setPipelineNodeCategory(pipelineNodeCategory:PipelineNodeCategory )={
    this.pipelineNodeCategory = pipelineNodeCategory
  }

  def getPipelineNodeCategory(): PipelineNodeCategory = pipelineNodeCategory


  override def compareTo(o: PipelineNode): Int = {
    this.nodeId - o.getId
  }

  /**
    * 验证并配置节点参数
    * @param nodeParameters
    */
  def configure(nodeParameters: java.util.Map[String,Object], pipelineNodeParameters: List[PipelineNodeParameter]):Unit

  /**
    * 解析操作步骤
    * @param dataCollections
    * @return
    */
  def parseOperate(dataCollections: DataCollection*): DataCollection
}
