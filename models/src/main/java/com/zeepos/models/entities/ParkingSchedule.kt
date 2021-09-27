package com.zeepos.models.entities

import com.zeepos.models.ConstVar

/**
 * Created by Arif S. on 8/15/20
 */
class ParkingSchedule {
    var banner: String? = ConstVar.EMPTY_STRING
    var logo: String? = ConstVar.EMPTY_STRING
    var merchantIG: String? = ConstVar.EMPTY_STRING
    var merchantId: Long = 0
    var merchantName: String? = ConstVar.EMPTY_STRING
    var address: String? = ConstVar.EMPTY_STRING
    var isCheckin: Boolean = false
    var schedules: List<Schedule> = arrayListOf()
}