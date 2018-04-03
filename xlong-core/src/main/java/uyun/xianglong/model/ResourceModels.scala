package uyun.xianglong.model

import java.sql.Timestamp

object ResourceModels {

  case class TagValue(value: String)

  case class Source(code: String, time: Timestamp)

  /**
    *
    * @param id         资源id
    * @param name       资源名称
    * @param resType    资源类型
    * @param fields     资源属性
    * @param tags       资源tag
    * @param extension  资源扩展字段
    * @param sources    资源数据源
    * @param createTime 资源创建时间
    * @param updateTime 资源更新时间
    */
  case class ResourceInput(
                            id: String,
                            name: String,
                            resType: String,
                            fields: Map[String, Any],
                            tags: Map[String, Seq[TagValue]],
                            extension: Map[String, Map[String, Any]],
                            sources: Seq[Source],
                            createTime: Timestamp,
                            updateTime: Timestamp
                          )

  /**
    *
    * @param id              资源id
    * @param name            资源名称
    * @param resType         资源类型
    * @param fields          资源属性
    * @param tags            资源tag
    * @param isClaimed       是否认领,目前当`extension.CMDB.circleId`不为null/"0"时为true,其他为false
    * @param isAutomatic     是否自动,目前当`sources`中包含code为"user"的`Source`对象时为false
    * @param resTotalCount   资源属性总量,从`pacific.Class.attrConfs`获取
    * @param resCount        资源数量,等价于`fields.size`
    * @param resCompleteRate 资源填写完整率,等价于`resCount`/`resTotalCount`
    * @param createTime      创建时间
    * @param updateTime      更新时间
    */
  case class ResourceModel(
                            id: String,
                            name: String,
                            resType: String,
                            fields: Map[String, Any],
                            tags: Map[String, Seq[TagValue]],
                            isClaimed: Boolean,
                            isAutomatic: Boolean,
                            resTotalCount: Int,
                            resCount: Int,
                            resCompleteRate: Double,
                            createTime: Timestamp,
                            updateTime: Timestamp
                          )


}