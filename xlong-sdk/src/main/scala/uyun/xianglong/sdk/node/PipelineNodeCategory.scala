package uyun.xianglong.sdk.node

/**
  * Created By wuhuahe
  * author: 游龙
  * Date : 2018-03-27
  * Time : 9:08
  * Desc : 节点类别
  */
object PipelineNodeCategory extends Enumeration {

  type PipelineNodeCategory = Value

  /**
    * STREAMING_DATASOURCE_LOAD_NODE 流数据源加载操作节点
    * BATCH_DATASOURCE_LOAD_NODE 批数据源加载操作节点
    * FINAL_OUTPUT_NODE 输出操作节点
    * TRANSFORM_NODE 转换操作节点
    */
  val STREAMING_DATASOURCE_LOAD_NODE, BATCH_DATASOURCE_LOAD_NODE, FINAL_OUTPUT_NODE, TRANSFORM_NODE = Value

  def getPipelineNodeCategory(category: String):PipelineNodeCategory ={
    category match {
      case "STREAMING_DATASOURCE_LOAD_NODE" => STREAMING_DATASOURCE_LOAD_NODE
      case "BATCH_DATASOURCE_LOAD_NODE" => BATCH_DATASOURCE_LOAD_NODE
      case "FINAL_OUTPUT_NODE" => FINAL_OUTPUT_NODE
      case "TRANSFORM_NODE" => TRANSFORM_NODE
    }
  }
}
