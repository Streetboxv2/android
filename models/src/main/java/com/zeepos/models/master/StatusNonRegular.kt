package com.zeepos.models.master

import com.zeepos.models.ConstVar
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
class StatusNonRegular {
    @Id(assignable = true)
    var id: Long = 0L
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var createdAt: String =ConstVar.EMPTY_STRING
    var merchantUsersId : Long = 0L
    var status: Int = 0
    var types : String = ConstVar.EMPTY_STRING
    var updatedAt: String = ConstVar.EMPTY_STRING
}