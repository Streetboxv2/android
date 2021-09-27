package com.zeepos.models.master

import com.zeepos.models.ConstVar
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToOne

/**
 * Created by Arif S. on 5/12/20
 */
@Entity
class ParkingSlot {
    @Id(assignable = true)
    var id: Long = 0L
    var parkingSpaceId: Long = 0L
    var name: String? = ConstVar.EMPTY_STRING
    var description: String? = ConstVar.EMPTY_STRING
    var point: Double = 0.0
    var qty: Int = 0
    var totalSlot: Int = 0
    var availableSlot: Int = 0
    var schedule: String? = ConstVar.EMPTY_STRING
    var startDate: String? = ConstVar.EMPTY_STRING
    var endDate: String? = ConstVar.EMPTY_STRING
    var createdAt: String? = ConstVar.EMPTY_STRING
    var updatedAt: String? = ConstVar.EMPTY_STRING

    lateinit var parkingSpace: ToOne<ParkingSpace>

}