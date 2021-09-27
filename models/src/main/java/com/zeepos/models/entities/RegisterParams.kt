package com.zeepos.models.entities

import com.zeepos.models.ConstVar

/**
 * Created by Arif S. on 7/3/20
 */
class RegisterParams {
    var idToken: String? = ConstVar.EMPTY_STRING
    var personId: String? = ConstVar.EMPTY_STRING
    var name: String? = ConstVar.EMPTY_STRING
    var phone: String? = ConstVar.EMPTY_STRING
    var photo: String? = ConstVar.EMPTY_STRING
}