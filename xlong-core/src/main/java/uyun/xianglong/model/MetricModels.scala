package uyun.xianglong.model

import java.sql.Timestamp

object MetricModels {

  /**
    *
    * @param id         事件id
    * @param resId      资源id
    * @param name       指标名
    * @param value      指标值
    * @param tags       指标tags
    * @param metricTime 指标时间
    */
  case class MetricInput(
                          id: String,
                          resId: String,
                          name: String,
                          value: Double,
                          tags: Map[String, String],
                          metricTime: Timestamp
                        )


  type MetricModel = MetricInput

}
