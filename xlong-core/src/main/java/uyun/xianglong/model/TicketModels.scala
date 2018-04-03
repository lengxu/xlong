package uyun.xianglong.model

import java.sql.Timestamp

object TicketModels {

  case class TicketInput(
                          id: String,
                          name: String,
                          ticketType: String,
                          fromStatus: Int,
                          toStatus: Int,
                          executor: String,
                          time: Timestamp
                        )

  case class TicketModel(
                          id: String,
                          name: String
                        )


}