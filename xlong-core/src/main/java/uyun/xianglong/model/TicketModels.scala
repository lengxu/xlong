package uyun.xianglong.model

import java.sql.Timestamp

object TicketModels {

  /**
    *
    * @param id              工单id
    * @param title           工单标题(内置字段之一,但是必填)
    * @param fromState       工单变更前状态
    * @param toState         工单变更后状态
    * @param stateChangeTime 状态变更时间
    * @param source          工单来源
    * @param ticketType      工单类型
    * @param executorId      处理人id
    * @param executorName    处理人姓名
    * @param priority        工单优先级(内置字段之一,但是必填)
    * @param expiredTime     工单逾期时间
    * @param innerFields     工单内置字段,元数据信息见[[metadata/ticket.inner.fields.json]]
    * @param userFields      工单用户自定义字段
    */
  case class TicketInput(
                          id: String,
                          title: String,
                          fromState: Int,
                          toState: Int,
                          stateChangeTime: Timestamp,
                          source: String,
                          ticketType: String,
                          executorId: String,
                          executorName: String,
                          priority: Int,
                          expiredTime: Timestamp,
                          innerFields: Map[String, Any],
                          userFields: Map[String, Any]
                        )

  /**
    *
    * @param id              工单id
    * @param title           工单标题(内置字段之一,但是必填)
    * @param state           工单状态
    * @param source          工单来源
    * @param ticketType      工单类型
    * @param executorId      处理人id
    * @param executorName    处理人姓名
    * @param priority        工单优先级(内置字段之一,但是必填)
    * @param isTicketExpired 工单是否逾期
    * @param todoTime        工单创建时间
    * @param doingTime       工单处理时间
    * @param doneTime        工单完成时间
    * @param closeTime       工单关闭时间
    * @param suspendTime     工单挂起时间
    * @param deleteTime      工单删除时间
    * @param processDuration 工单处理时间
    * @param innerFields     工单内置字段
    * @param userFields      工单用户自定义字段
    */
  case class TicketModel(
                          id: String,
                          title: String,
                          state: Int,
                          source: String,
                          ticketType: String,
                          executorId: String,
                          executorName: String,
                          priority: Int,
                          isTicketExpired: Boolean,
                          todoTime: Timestamp,
                          doingTime: Timestamp,
                          doneTime: Timestamp,
                          closeTime: Timestamp,
                          suspendTime: Timestamp,
                          deleteTime: Timestamp,
                          processDuration: Long,
                          innerFields: Map[String, Any],
                          userFields: Map[String, Any]
                        )


}