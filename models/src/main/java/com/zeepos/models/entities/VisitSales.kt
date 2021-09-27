package com.zeepos.models.entities

/**
 * Created by Arif S. on 8/6/20
 */
class VisitSales {
    var deposit: Long = 0
    var salesId: Long = 0
    var scheduleDate: Long = 0
    var startTime: Long = 0
    var endTime: Long = 0
    var menus: MutableList<MenuItem> = mutableListOf()
}

class MenuItem(
    var menu_id: Long = 0,
    var quantity: Int = 0
)