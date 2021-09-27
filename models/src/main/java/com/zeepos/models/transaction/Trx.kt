package com.zeepos.models.transaction

import com.zeepos.models.ConstVar
import com.zeepos.utilities.gson.Exclude
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToOne

/**
 * Created by Arif S. on 5/17/20
 */
@Entity
class Trx {
    @Id
    var id: Long = 0
    var types: Long = 0
    var userId: Long = 0
    var createdAt: Long = 0L
    var updatedAt: Long = 0L
    var deletedAt: Long = 0L
    var status: String = ConstVar.EMPTY_STRING
    var externalId: String = ConstVar.EMPTY_STRING
    var address: String = ConstVar.EMPTY_STRING
    var qrCode: String = ConstVar.EMPTY_STRING

    @Exclude
    var trxId: String = ConstVar.EMPTY_STRING
    lateinit var order: ToOne<Order>
}