package com.zeepos.models.transaction

import com.google.gson.annotations.SerializedName
import com.zeepos.models.ConstVar
import com.zeepos.models.master.User
import io.objectbox.annotation.Id
import io.objectbox.relation.ToOne

class FreeTaskOperator {
    @Id(assignable = true)
    var schedule: String? = ConstVar.EMPTY_STRING
    var address: String? = ConstVar.EMPTY_STRING
    var startDate: String? = ConstVar.EMPTY_STRING
    var scheduleDate : String? = ConstVar.EMPTY_STRING
    var endDate: String? = ConstVar.EMPTY_STRING
    var status: Int = ConstVar.TASK_STATUS_OPEN
    var name: String? = ConstVar.EMPTY_STRING
    var latParkingSpace: Double = 0.0
    var lonParkingSpace: Double = 0.0
    var createdAt: Long = 0L
    var updatedAt: Long = 0L
    var types: String? = ConstVar.EMPTY_STRING
    var tasksId: Long = 0L
    var typesId: Long = 0L

}