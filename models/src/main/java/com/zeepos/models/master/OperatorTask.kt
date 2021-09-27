package com.zeepos.models.master

import com.zeepos.models.ConstVar
import com.zeepos.models.converter.StringListConverter
import io.objectbox.annotation.Backlink
import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToMany

@Entity
class OperatorTask {
    @Id(assignable = true)
    var taskId : Long = 0L
    var name : String? = ConstVar.EMPTY_STRING
    var address : String? = ConstVar.EMPTY_STRING
    var startDate : String? = ConstVar.EMPTY_STRING
    var endDate : String? = ConstVar.EMPTY_STRING
    var latParkingSpace: Double = 0.0
    var lonParkingSpace: Double = 0.0

    @Convert(converter = StringListConverter::class, dbType = String::class)
    var operatorParkingTask: List<String>? = ArrayList()
}