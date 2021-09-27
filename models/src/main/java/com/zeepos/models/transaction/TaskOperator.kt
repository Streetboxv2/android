package com.zeepos.models.transaction

import com.zeepos.models.ConstVar
import com.zeepos.models.master.User
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToOne

/**
 * Created by Arif S. on 5/22/20
 */
@Entity
class TaskOperator {
    @Id(assignable = true)
    var tasksId: Long = 0L
    var uniqueId: String? = ConstVar.EMPTY_STRING
    var userId: Long = 0L
    var parkingSalesId: Long = 0L
    var parkingSlotSalesId: Long = 0L
    var schedule: String? = ConstVar.EMPTY_STRING
    var address: String? = ConstVar.EMPTY_STRING
    var scheduleDate: String? = ConstVar.EMPTY_STRING
    var startDate: String? = ConstVar.EMPTY_STRING
    var endDate: String? = ConstVar.EMPTY_STRING
    var startTime: Long = 0L
    var endTime: Long = 0L
    var status: Int = ConstVar.TASK_STATUS_OPEN
    var name: String? = ConstVar.EMPTY_STRING
    var operatorName: String? = ConstVar.EMPTY_STRING
    var note: String? = ConstVar.EMPTY_STRING
    var pickUpLat: Double = 0.0
    var pickUpLng: Double = 0.0
    var latParkingSpace: Double = 0.0
    var lonParkingSpace: Double = 0.0
    var createdAt: Long = 0L
    var updatedAt: Long = 0L
    var types: String? = ConstVar.EMPTY_STRING
    var typesId: Long = 0L
    var merchantId: Long = 0L
    var merchantName: String? = ConstVar.EMPTY_STRING
    var customerName: String? = ConstVar.EMPTY_STRING
    var logo: String? = ConstVar.EMPTY_STRING
    var banner: String? = ConstVar.EMPTY_STRING
    var merchantIG: String? = ConstVar.EMPTY_STRING

    lateinit var parkingSales: ToOne<ParkingSales>
    lateinit var user: ToOne<User>
    lateinit var parkingSlotSales: ToOne<ParkingSlotSales>
}