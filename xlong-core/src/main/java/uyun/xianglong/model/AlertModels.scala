package uyun.xianglong.model

import java.sql.Timestamp

object AlertModels {

  /**
    *
    * @param id           告警id
    * @param ticketId     工单id
    * @param resId        资源id
    * @param name         告警名称
    * @param severity     告警优先级
    * @param description  告警描述
    * @param source       告警来源
    * @param occurTime    告警发生时间
    * @param tags         告警标签
    * @param autoStatus   自动修复状态
    * @param fromState    告警变更前状态
    * @param toState      告警变更后状态
    * @param executor     告警处理人
    * @param closeMessage 关闭理由,系统提供了三种(故障已解决,计划停机,监控系统误报),允许填自定义的消息
    * @param processTime  告警开始处理时间 //这三个处理人相关时间可以考虑合并
    * @param resolveTime  告警解决时间
    * @param closeTime    告警关闭时间
    */
  case class AlertInput(
                         id: String,
                         resId: String,
                         ticketId: String,
                         name: String,
                         severity: Int,
                         description: String,
                         source: String,
                         occurTime: Timestamp,
                         tags: Map[String, String],
                         fromState: Int,
                         toState: Int,
                         autoStatus: Int,
                         executor: String,
                         processTime: Timestamp,
                         resolveTime: Timestamp,
                         closeMessage: String,
                         closeTime: Timestamp
                       )

  /**
    *
    * @param id               告警id
    * @param ticketId         工单id
    * @param resId            资源id
    * @param name             告警名称
    * @param severity         告警优先级
    * @param description      告警描述
    * @param source           告警来源
    * @param firstOccurTime   首次发生时间
    * @param lastOccurTime    最后发生时间
    * @param tags             告警tags
    * @param executor         告警处理人
    * @param state            告警状态
    * @param processTime      告警开始处理时间
    * @param resolveTime      告警解决时间
    * @param closeTime        告警关闭时间
    * @param count            告警发生次数
    * @param occurDuration    告警持续时长
    * @param responseDuration 告警响应时长
    * @param processDuration  告警处理时长
    * @param hitErrorRate     告警命中故障比率(关闭理由!=监控系统误报的告警/总告警次数)
    * @param unHitErrorRate   告警误报率(关闭理由=监控系统误报的告警/总告警次数)
    * @param autoRecoveryRate 告警自动恢复率((自动修复+告警解决 or完成次数)/总告警次数)
    */
  case class AlertModel(
                         id: String,
                         resId: String,
                         ticketId: String,
                         name: String,
                         severity: Int,
                         description: String,
                         source: String,
                         firstOccurTime: Timestamp,
                         lastOccurTime: Timestamp,
                         tags: Map[String, String],
                         executor: String,
                         state: Int,
                         processTime: Timestamp,
                         resolveTime: Timestamp,
                         closeTime: Timestamp,
                         count: Int,
                         occurDuration: Long,
                         responseDuration: Long,
                         processDuration: Long,
                         hitErrorRate: Double,
                         unHitErrorRate: Double,
                         autoRecoveryRate: Double
                       )

}
