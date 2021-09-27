package com.zeepos.models.master

import com.zeepos.models.ConstVar
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

/**
 * Created by Arif S. on 6/14/20
 */
@Entity
class Merchant {
    @Id(assignable = true)
    var id: Long = 0
    var name: String? = ConstVar.EMPTY_STRING
    var address: String? = ConstVar.EMPTY_STRING
    var description: String? = ConstVar.EMPTY_STRING
    var phone: String? = ConstVar.EMPTY_STRING
    var logo: String? = ConstVar.EMPTY_STRING
    var banner: String? = ConstVar.EMPTY_STRING
}