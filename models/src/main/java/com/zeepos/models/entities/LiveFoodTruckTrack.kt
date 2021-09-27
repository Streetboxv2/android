package com.zeepos.models.entities

import com.zeepos.models.ConstVar

/**
 * Created by Arif S. on 9/14/20
 */
class LiveFoodTruckTrack {
    var merchantId: Long = 0
    var merchantName: String? = ConstVar.EMPTY_STRING
    var logo: String? = ConstVar.EMPTY_STRING
    var distance: String? = ConstVar.EMPTY_STRING
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var tasksId: Long = 0
    var types: String? = ConstVar.EMPTY_STRING
    var logTime: String? = ConstVar.EMPTY_STRING
    var status: Int = 0
    var merchantIG: String? = ConstVar.EMPTY_STRING
    var banner: String? = ConstVar.EMPTY_STRING
}