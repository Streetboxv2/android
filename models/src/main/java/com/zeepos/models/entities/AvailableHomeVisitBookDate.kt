package com.zeepos.models.entities

import com.zeepos.models.ConstVar

/**
 * Created by Arif S. on 8/3/20
 */
class AvailableHomeVisitBookDate {
    var deposit: Double = 0.0
    var id: Long = 0
    var scheduleDate: Long = 0
    var endDate: String? = ConstVar.EMPTY_STRING
    var startDate: String? = ConstVar.EMPTY_STRING
}