package com.zeepos.models.master

import com.zeepos.models.ConstVar.EMPTY_STRING

/**
 * Created by Arif S. on 5/2/20
 */
data class UserAuthData(
    var accessToken: String? = EMPTY_STRING,
    var role_name: String? = EMPTY_STRING,
    var user_id: String? = EMPTY_STRING,
    var exp: Long = 0,
    var expiresIn: Long = 0,
    var tokenType: String? = EMPTY_STRING,
    var refreshToken: String? = EMPTY_STRING,
    var accountUsername: String? = EMPTY_STRING,
    var accountId: Long = 0,
    var userName: String? = EMPTY_STRING,
    var password: String? = EMPTY_STRING
)