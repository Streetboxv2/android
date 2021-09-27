package com.zeepos.models.master

import com.zeepos.models.ConstVar
import com.zeepos.models.transaction.Order
import io.objectbox.annotation.Backlink
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToMany

@Entity
data class Profile(
    @Id(assignable = true) var id: Long = 0L,
    var name: String? = ConstVar.EMPTY_STRING,
    var user_name: String? = ConstVar.EMPTY_STRING,
    var phone: String? = ConstVar.EMPTY_STRING,
    var address:String? = ConstVar.EMPTY_STRING,
    var created_at: Long = 0L,
    var updated_at: Long = 0L
)