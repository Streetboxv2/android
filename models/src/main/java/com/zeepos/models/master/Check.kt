package com.zeepos.models.master

import com.zeepos.models.ConstVar
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
class Check {
    @Id(assignable = true)
    var id: Long = 0L
    var tasksId: Long = 0L
    var typesId: Long = 0L
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var parkingspaceName : String? = ConstVar.EMPTY_STRING
    var types : String? = ConstVar.EMPTY_STRING
    var status: Long = 0L
    var customerName : String? = ConstVar.EMPTY_STRING

}