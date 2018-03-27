package uyun.xianglong.sdk

/**
  * Created By wuhuahe
  * author : 游龙
  * Date : 2018-03-23
  * Time : 13:26
  * Desc : 具有ID号可排序组件
  */
trait OrderedComponent {

  def setId(id: Int):Unit

  def getId:Int

  def setPrevIds(prevIds: java.util.List[Integer]):Unit

  def getPrevIds: java.util.List[Integer]
}
