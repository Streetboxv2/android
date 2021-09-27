package com.zeepos.models.master

import com.zeepos.models.ConstVar
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
class Shift {
    @Id(assignable = true)
    var id: Long = 0L
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var shiftIn: Boolean = false
    var shift : String = ConstVar.EMPTY_STRING

}