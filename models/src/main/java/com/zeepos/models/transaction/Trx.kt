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
    var ids: Long = 0
    var types: String = ConstVar.EMPTY_STRING
    var userId: Long = 0
    var createdAt: String = ConstVar.EMPTY_STRING
    var updatedAt: String = ConstVar.EMPTY_STRING
    var deletedAt:String = ConstVar.EMPTY_STRING

    var externalId: String = ConstVar.EMPTY_STRING
    var address: String = ConstVar.EMPTY_STRING
    var qrCode: String = ConstVar.EMPTY_STRING
    var id: String = ConstVar.EMPTY_STRING
    var trxId: String = ConstVar.EMPTY_STRING
    var status: String = ConstVar.EMPTY_STRING

    @Exclude
    lateinit var order: ToOne<Order>
}