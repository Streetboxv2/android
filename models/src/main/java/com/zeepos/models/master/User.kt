package com.zeepos.models.master

import com.zeepos.models.ConstVar
import com.zeepos.models.transaction.Order
import com.zeepos.utilities.gson.Exclude
import io.objectbox.annotation.Backlink
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToMany

/**
 * Created by Arif S. on 5/3/20
 */
@Entity
class User {
    @Id(assignable = true)
    var id: Long = 0
    var name: String? = ConstVar.EMPTY_STRING
    var userName: String? = ConstVar.EMPTY_STRING
    var token: String? = ConstVar.EMPTY_STRING
    var roleName: String? = ConstVar.EMPTY_STRING
    var tasksId: Long = 0
    var status: Int = 0
    var phone: String? = ConstVar.EMPTY_STRING
    var platNo: String? = ConstVar.EMPTY_STRING
    var address: String? = ConstVar.EMPTY_STRING
    var createdAt: String? = ConstVar.EMPTY_STRING
    var updatedAt: String? = ConstVar.EMPTY_STRING
    var merchantUsersId: Long = 0
    var logo: String? = ConstVar.EMPTY_STRING
    var banner: String? = ConstVar.EMPTY_STRING
    var profilePicture: String? = ConstVar.EMPTY_STRING
    var email: String? = ConstVar.EMPTY_STRING
    var city: String? = ConstVar.EMPTY_STRING
    var igAccount: String? = ConstVar.EMPTY_STRING

    @Exclude
    @Backlink(to = "user")
    lateinit var orders: ToMany<Order>
}