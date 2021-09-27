package com.zeepos.models.master

import com.zeepos.models.ConstVar
import com.zeepos.utilities.DateTimeUtil
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

/**
 * Created by Arif S. on 6/17/20
 */
@Entity
class SyncData {
    @Id
    var id: Long = 0
    var uniqueId: String = ConstVar.EMPTY_STRING
    var type: String = ConstVar.EMPTY_STRING
    var status: Int = ConstVar.SYNC_STATUS_NOT_SYNC
    var businessDate: Long = DateTimeUtil.getCurrentDateWithoutTime()//must be not here
    var syncDate: Long = DateTimeUtil.getCurrentDateTime()
    var nextPage: Int = 1
    var data: String? = ConstVar.EMPTY_STRING

}