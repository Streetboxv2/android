package com.zeepos.models.entities

import com.zeepos.models.ConstVar

/**
 * Created by Arif S. on 7/20/20
 */
data class ListData(
    var title: String = ConstVar.EMPTY_STRING,
    var icon: Int = 0
)