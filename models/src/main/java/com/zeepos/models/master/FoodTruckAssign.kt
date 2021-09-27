package com.zeepos.models.master

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

/**
 * Created by Arif S. on 7/7/20
 */
@Entity
class FoodTruckAssign {
    @Id
    var id: Long = 0
    var foodTruckId: Long = 0
    var parkingSlotSalesId: Long = 0
    var scheduleDate: Long = 0
}