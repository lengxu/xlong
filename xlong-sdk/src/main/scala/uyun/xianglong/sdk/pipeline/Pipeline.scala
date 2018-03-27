package uyun.xianglong.sdk.pipeline

import java.util

import org.apache.commons.cli.Options
import org.apache.commons.lang3.StringUtils
import uyun.xianglong.sdk.NamedComponent
import uyun.xianglong.sdk.common.utz.Cmd
import uyun.xianglong.sdk.node.{PipelineNode, PipelineNodeConfigModel, PipelineNodeFactory, PipelineNodeParameter}

import scala.collection.JavaConversions._
import scala.collection.mutable

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
  protected var pipelineNodeConfigModels: Map[String, PipelineNodeConfigModel] = _

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

  def setPipelineNodeConfigModels(pipelineNodeConfigModels: Map[String, PipelineNodeConfigModel]): Unit = {
    this.pipelineNodeConfigModels = pipelineNodeConfigModels
  }

  def getPipelineNodeConfigModels: Map[String, PipelineNodeConfigModel] = pipelineNodeConfigModels

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
    val parameterDefinitionFilePath = cmd.getOptionValue("p")
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

    if (StringUtils.isBlank(parameterDefinitionFilePath)) {
      throw new RuntimeException("parameterDefinitionFilePath not be null or blank.")
    }
    val models: util.List[PipelineNodeConfigModel] = PipelineNodeConfigModel.parseFromFilePath(parameterDefinitionFilePath)
    val nodeTypes: mutable.Seq[String] = pipelineConfig.getNodes.map(node => node.getType).distinct

    val pipelineNodeConfigModels: Map[String, PipelineNodeConfigModel] =
      models.filter(model => nodeTypes.contains(model.getType)).map(model => (model.getType, model)).toMap

    setPipelineNodeConfigModels(pipelineNodeConfigModels)

    val allNodes: List[PipelineNode] = List[PipelineNode]()
    // 验证参数,并实例化各个节点
    pipelineConfig.getNodes.map(nodeConfig => {
      val tpye = nodeConfig.getType
      val nodeName = nodeConfig.getName
      val nodeId = nodeConfig.getNodeId
      val prevNodeIds = nodeConfig.getPrevNodeIds
      val nodeParemeters: util.Map[String, AnyRef] = nodeConfig.getNodeParameters
      val pipelineNodeConfigModel: Option[PipelineNodeConfigModel] = pipelineNodeConfigModels.get(tpye)
      pipelineNodeConfigModel match {
        case Some(model) => {
          val pipelineNodeCategory: String = model.getPipelineNodeCategory
          val implClass = model.getPipelineNodeImplClass
          val pipelineNodeParameters: util.List[PipelineNodeParameter] = model.getPipelineNodeParameters
          //验证参数
          // 验证必要的参数
          // 对不是必要的参数，如果有默认值，则补充默认值
          pipelineNodeParameters.foreach(p => {
            val name = p.getName
            val defaultValue: String = p.getDefaultValue
            val valueType = p.getValueType
            val validValues = p.getValidValues
            val isRequired = p.isRequired
            if (isRequired && !nodeParemeters.containsKey(name)) {
              if (defaultValue == null) throw new RuntimeException(s"Required parameter $name must exist and may not be null")
              else {
                valueType match {
                  case "STRING" => nodeParemeters.put(name, defaultValue.asInstanceOf[AnyRef])
                  case "INTEGER" => nodeParemeters.put(name, defaultValue.toInt.asInstanceOf[AnyRef])
                  case "DOUBLE" => nodeParemeters.put(name, defaultValue.toDouble.toInt.asInstanceOf[AnyRef])
                  case "FLOAT" => nodeParemeters.put(name, java.lang.Float.parseFloat(defaultValue).asInstanceOf[AnyRef])
                  case "LONG" => nodeParemeters.put(name, java.lang.Long.parseLong(defaultValue).asInstanceOf[AnyRef])
                  case "BOOLEAN" => nodeParemeters.put(name, java.lang.Boolean.parseBoolean(defaultValue).asInstanceOf[AnyRef])
                  case _ => nodeParemeters.put(name, defaultValue)
                }
              }
            }
            if (nodeParemeters.containsKey(name) && validValues.size() > 0) {
              // 合法值验证
              val v: AnyRef = nodeParemeters.get(name)
              val isValid = valueType match {
                case "INTEGER" => validValues.map(vv => Integer.parseInt(vv)).contains(v.asInstanceOf[Integer])
                case "DOUBLE" => validValues.map(vv => java.lang.Double.parseDouble(vv)).contains(v.asInstanceOf[java.lang.Double])
                case "FLOAT" => validValues.map(vv => java.lang.Float.parseFloat(vv)).contains(v.asInstanceOf[java.lang.Float])
                case "LONG" => validValues.map(vv => java.lang.Long.parseLong(vv)).contains(v.asInstanceOf[java.lang.Long])
                case "BOOLEAN" => validValues.map(vv => java.lang.Boolean.parseBoolean(vv)).contains(v.asInstanceOf[java.lang.Boolean])
                case _ => validValues.contains(v.asInstanceOf[String])
              }
              if (!isValid) throw new RuntimeException(s"the value [$v] of parameter [$name] is not valid. the valid values are $validValues")
            }
          })
          // 实例化
          val pipelineNode = PipelineNodeFactory.create(nodeId, prevNodeIds, pipelineNodeCategory, nodeName, implClass)
          allNodes.add(pipelineNode)
        }
        case None => throw new RuntimeException("the type [" + `tpye` + "] has not a config model.")
      }
    })
    if (allNodes.size <= 0) {
      throw new RuntimeException("Pipeline must has nodes")
    }
    setAllNodes(allNodes.toArray)
  }

  /**
    *
    */
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
