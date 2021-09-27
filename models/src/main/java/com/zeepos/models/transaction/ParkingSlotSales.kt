package com.zeepos.models.transaction

import com.zeepos.models.ConstVar
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToOne

/**
 * Created by Arif S. on 5/17/20
 */
@Entity
class ParkingSlotSales {
    @Id(assignable = true)
    var id: Long = 0L
    var uniqueId: String? = ConstVar.EMPTY_STRING
    var parkingSlotId: Long = 0L
    var parkingSalesUniqueId: String? = ConstVar.EMPTY_STRING
    var name: String = ConstVar.EMPTY_STRING
    var description: String? = ConstVar.EMPTY_STRING
    var schedule: String? = ConstVar.EMPTY_STRING
    var startDate: String? = ConstVar.EMPTY_STRING
    var endDate: String? = ConstVar.EMPTY_STRING
    var price: Double = 0.0
    var qty: Int = 0
    var totalSlot: Int = 0
    var subtotal: Double = 0.0
    var createdAt: Long = 0L
    var updatedAt: Long = 0L
    var trxVisitSalesId: Long = 0L

    lateinit var parkingSales: ToOne<ParkingSales>

}