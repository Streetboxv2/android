package com.zeepos.models.entities

import com.zeepos.models.ConstVar

/**
 * Created by Arif S. on 8/15/20
 */
class Schedule {
    var endDate: String? = ConstVar.EMPTY_STRING
    var salesId: Long = 0
    var startDate: String? = ConstVar.EMPTY_STRING
}