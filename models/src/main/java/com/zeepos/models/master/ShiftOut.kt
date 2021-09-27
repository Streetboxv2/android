package com.zeepos.models.master

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
class ShiftOut {
    @Id(assignable = true)
    var id: Long = 0L
    var latitude: Double = 0.0
    var longitude: Double = 0.0
}