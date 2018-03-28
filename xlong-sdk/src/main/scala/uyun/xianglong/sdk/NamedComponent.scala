package uyun.xianglong.sdk

/**
  * Created By wuhuahe
  * author : 游龙
  * Date : 2018-03-23
  * Time : 13:25
  * Desc : 命名组件
  */
trait NamedComponent {
  def setName(name:String):Unit
  def getName:String
}
