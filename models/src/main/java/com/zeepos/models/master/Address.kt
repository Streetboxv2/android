package com.zeepos.models.master

import com.zeepos.models.ConstVar
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

/**
 * Created by Arif S. on 8/26/20
 */
@Entity
class Address {
    @Id
    var id: Long = 0
    var name: String? = ConstVar.EMPTY_STRING
    var address: String? = ConstVar.EMPTY_STRING
    var phone: String? = ConstVar.EMPTY_STRING
    var note: String? = ConstVar.EMPTY_STRING
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var createdAt: Long = 0
    var updatedAt: Long = 0
    var isSelected: Boolean = false
}