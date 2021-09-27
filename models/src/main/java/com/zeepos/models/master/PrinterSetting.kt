package com.zeepos.models.master

import com.zeepos.models.ConstVar
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

/**
 * Created by Arif S. on 7/20/20
 */
@Entity
class PrinterSetting {
    @Id
    var id: Long = 0
    var uniqueId: String = ConstVar.EMPTY_STRING
    var name: String = ConstVar.EMPTY_STRING
    var type: String = ConstVar.EMPTY_STRING
    var attribute: String = ConstVar.EMPTY_STRING
    var length: Int = 32
    var logo: String = ConstVar.EMPTY_STRING
    var header: String = ConstVar.EMPTY_STRING
    var footer: String = "THANK YOU"
    var isActive: Boolean = true
}