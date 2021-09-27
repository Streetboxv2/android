package com.zeepos.models.master

import com.zeepos.models.ConstVar
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class UserPassword(
    @Id(assignable = true) var id: Long = 0L,
    var email: String? = ConstVar.EMPTY_STRING,
    var password: String? = ConstVar.EMPTY_STRING,
    var token: String? = ConstVar.EMPTY_STRING,
    var createdDate: Long = 0L,
    var updatedDate: Long = 0L
)