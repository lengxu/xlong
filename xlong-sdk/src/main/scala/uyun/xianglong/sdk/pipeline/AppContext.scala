package uyun.xianglong.sdk.pipeline

/**
  * Created By wuhuahe
  * author: 游龙
  * Date : 2018-03-27
  * Time : 9:40
  * Desc :
  */
trait AppContext[T] {

  def getRunEnv(): T

  def getConfig():PipelineConfig

  def init(pipelineConfig: PipelineConfig):AppContext[T]
}
