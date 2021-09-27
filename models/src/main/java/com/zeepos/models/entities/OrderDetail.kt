package com.zeepos.models.entities

/**
 * Created by Arif S. on 8/11/20
 */
class OrderDetail {
    var productName: String? = ""
    var qty: Int = 0
    var menus: List<MenuItemOrder>? = arrayListOf()
}

class MenuItemOrder(
    var name: String? = "",
    var quantity: Int = 0
)