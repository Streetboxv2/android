package com.zeepos.models.entities

/**
 * Created by Arif S. on 5/22/20
 */
data class Message(
    var type: String,
    var data: Any? = Any()
)