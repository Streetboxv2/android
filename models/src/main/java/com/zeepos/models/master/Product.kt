package com.zeepos.models.master

import com.zeepos.models.ConstVar
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

/**
 * Created by Arif S. on 6/24/20
 */
@Entity
class Product {
    @Id(assignable = true)
    var id: Long = 0
    var name: String = ConstVar.EMPTY_STRING
    var price: Double = 0.0
    var priceAfterDiscount: Double = 0.0
    var priceOriginal: Double = 0.0
    var discount: Double = 0.0
    var qty: Int = 0
    var isActive: Boolean = true
    var description: String? = ConstVar.EMPTY_STRING
    var photo: String? = ConstVar.EMPTY_STRING
    var createdAt: String? = ConstVar.EMPTY_STRING
    var updatedAt: String? = ConstVar.EMPTY_STRING
    var deletedAt: String? = ConstVar.EMPTY_STRING
    var image: String? = ConstVar.EMPTY_STRING

    @Transient
    var tmpNote: String? = ConstVar.EMPTY_STRING

}