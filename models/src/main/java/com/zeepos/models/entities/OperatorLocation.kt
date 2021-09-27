package com.zeepos.models.entities

import com.zeepos.models.ConstVar

/**
 * Created by Arif S. on 6/4/20
 */
data class OperatorLocation(
    val tasksId: Long,
    val latitude: Double,
    val longitude: Double,
    val status: Int = 0,
    val logTime: String? = ConstVar.EMPTY_STRING
)