package com.zeepos.models.entities

import com.zeepos.models.ConstVar

data class CreateHomeVisitTask(
    val trxVisitSalesId: Long= 0L,
    val usersId: Long = 0L

) {
    //this is for response
    var createdAt: String? = ConstVar.EMPTY_STRING
    var id: Long = 0
    var merchantUsersId: Long = 0
    var status: Long = 0
    var types: String? = ConstVar.EMPTY_STRING
    var updatedAt: String? = ConstVar.EMPTY_STRING
}