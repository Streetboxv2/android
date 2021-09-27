package com.zeepos.models.master

import com.zeepos.models.ConstVar
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

/**
 * Created by Arif S. on 7/10/20
 */
@Entity
class Tax {
    @Id(assignable = true)
    var id: Long = 0
    var name: String? = ConstVar.EMPTY_STRING
    var amount: Double = 0.0
    var type: Int = 0
    var isActive: Boolean = false
    var merchantId: Long = 0
    var createdAt: String? = ConstVar.EMPTY_STRING
    var updatedAt: String? = ConstVar.EMPTY_STRING
}