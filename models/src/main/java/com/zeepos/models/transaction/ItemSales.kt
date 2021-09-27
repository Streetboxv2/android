package com.zeepos.models.transaction

import com.zeepos.models.ConstVar
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

/**
 * Created by Arif S. on 6/9/20
 */
@Entity
class ItemSales {
    @Id(assignable = true)
    var id: Long = 0
    var uniqueId: String = ConstVar.EMPTY_STRING
    var foodTruckId: Long = 0
    var merchantId: Long = 0
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var status: Int = 0
}