package uyun.xianglong.sdk.pipeline

import java.util

import org.apache.commons.cli.Options
import org.apache.commons.lang3.StringUtils
import uyun.xianglong.sdk.NamedComponent
import uyun.xianglong.sdk.common.utz.Cmd
import uyun.xianglong.sdk.node.{PipelineNode, PipelineNodeFactory}

import scala.collection.JavaConversions._

/**
  * Created By wuhuahe
  * author : 游龙
  * Date : 2018-03-24
  * Time : 20:54
  * Desc : 管道
  */
trait Pipeline extends NamedComponent {
  protected var name: String = _
  protected var nodes: Array[PipelineNode] = _

  protected var pipelineConfig: PipelineConfig = _

  override def setName(name: String): Unit = {
    this.name = name
  }

  override def getName: String = name

  def getAllNodes: Array[PipelineNode] = nodes

  def setAllNodes(nodes: Array[PipelineNode]): Unit = {
    this.nodes = nodes
  }

  def setPipelineConfig(pipelineConfig: PipelineConfig): Unit = {
    this.pipelineConfig = pipelineConfig
  }

  def getPipelineConfig: PipelineConfig = pipelineConfig

  /**
    * 初始化应用上下文
    *
    * @param args
    */
  def initAppContext(args: Array[String]): Unit = {
    val options = new Options()
    val cmd = Cmd.build(args, options)
    val configFilePath = cmd.getOptionValue("f")
    val configString = cmd.getOptionValue("s")
    val pipelineConfig = if (StringUtils.isNotBlank(configFilePath)) {
      PipelineConfig.parseFromFile(configFilePath)
    } else if (StringUtils.isNotBlank(configString)) {
      PipelineConfig.parse(configString)
    } else {
      throw new RuntimeException("must special the pipeline config with file or string.")
    }
    setPipelineConfig(pipelineConfig)
    // 设置pipeline名称
    setName(pipelineConfig.getName)

    var allNodes: List[PipelineNode] = List.empty[PipelineNode]
    // 验证参数,并实例化各个节点
    pipelineConfig.getNodes.map(nodeConfig => {
      val nodeName = nodeConfig.getName
      val javaImplClass = nodeConfig.getJavaImplClass
      val nodeId = nodeConfig.getNodeId
      val prevNodeIds = nodeConfig.getPrevNodeIds
      val nodeParemeters: util.Map[String, AnyRef] = nodeConfig.getNodeParameters

      val pipelineNode = PipelineNodeFactory.create(nodeName, tpye = javaImplClass, nodeId = nodeId, prevNodeIds = prevNodeIds, nodeParemeters)
      allNodes = pipelineNode :: allNodes
    })
    if (allNodes.size <= 0) {
      throw new RuntimeException("Pipeline must has nodes")
    }
    setAllNodes(allNodes.toArray)
  }

  def destroy(): Unit

  def execute(): Unit

  def sortAndValidateNodes(): Unit

  /**
    * 配置属性
    */
  def configure(): Unit

  def run(args: Array[String]): Unit = {
    // 1. 初始化应用上下文
    initAppContext(args)

    // 2. 对管道、节点、操作进行配置
    configure()

    // 3. 验证管道内的节点、操作
    // 4. 对管道的节点进行排序
    sortAndValidateNodes()

    // 5. 执行
    execute()

    // 6. 销毁上下文
    destroy()
  }
}
