package uyun.xianglong.model

import java.sql.Timestamp

object MetricModels {

  case class MetricInput(
                          id: String,
                          resId: String,
                          value: Double,
                          tags: Map[String, String],
                          metricTime: Timestamp
                        )


  type MetricModel = MetricInput

}
