package com.zeepos.models.entities

class CreateSync (
    val businessDate: Long,
    val data: String,
    val status: Int,
    val syncDate: Long,
    val type: String,
    val uniqueId: String
)
