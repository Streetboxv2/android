package com.zeepos.models.master

import com.zeepos.models.ConstVar
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

/**
 * Created by Arif S. on 5/17/20
 */
@Entity
class PaymentMethod {
    @Id(assignable = true)
    var id: Long = 0L
    var name: String? = ConstVar.EMPTY_STRING
    var type: Int = 0
    var isActive: Boolean = true
    var createdAt: Long = 0L
    var updatedAt: Long = 0L
    var providerName: String? = ConstVar.EMPTY_STRING
    var image: String? = ConstVar.EMPTY_STRING
}