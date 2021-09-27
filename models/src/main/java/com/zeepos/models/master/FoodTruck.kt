package com.zeepos.models.master

import com.zeepos.models.ConstVar
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

/**
 * Created by Arif S. on 6/9/20
 */
@Entity
class FoodTruck {
    @Id(assignable = true)
    var id: Long = 0
    var merchantId: Long = 0
    var merchantUsersId: Long = 0
    var merchantName: String? = ConstVar.EMPTY_STRING
    var category: String? = ConstVar.EMPTY_STRING
    var merchantCategory: String? = ConstVar.EMPTY_STRING
    var categoryColor: String? = ConstVar.EMPTY_STRING
    var name: String? = ConstVar.EMPTY_STRING
    var address: String? = ConstVar.EMPTY_STRING
    var description: String? = ConstVar.EMPTY_STRING
    var logo: String? = ConstVar.EMPTY_STRING
    var banner: String? = ConstVar.EMPTY_STRING
    var city: String? = ConstVar.EMPTY_STRING
    var schedule: String? = ConstVar.EMPTY_STRING
    var rating: Double = 0.0
    var distance: Double = 0.0
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var status: Int = 0
    var tasksId: Long = 0
    var types: String? = ConstVar.EMPTY_STRING
    var merchantIG: String? = ConstVar.EMPTY_STRING
    var terms: String? = ConstVar.EMPTY_STRING
    var typesId: Long = 0
}