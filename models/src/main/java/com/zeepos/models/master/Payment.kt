package com.zeepos.models.master

import com.zeepos.models.ConstVar
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

/**
 * Created by Arif S. on 7/10/20
 */
@Entity
class Payment {
    @Id(assignable = true)
    var id: Long = 0
    var name: String = ConstVar.EMPTY_STRING
    var type: String = ConstVar.EMPTY_STRING
    var provider: String = ConstVar.EMPTY_STRING
    var isActive: Boolean = true
}