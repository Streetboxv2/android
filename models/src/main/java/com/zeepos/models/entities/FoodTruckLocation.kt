package com.zeepos.models.entities

import com.zeepos.models.ConstVar
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

/**
 * Created by Arif S. on 8/7/20
 */
@Entity
class FoodTruckLocation {
    @Id
    var taskId: Long = 0
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var tag: String = ConstVar.EMPTY_STRING
}