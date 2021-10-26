package com.example.dbroom.db.room.enitity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tbl_menu_store")
class MenuItemStore(

    @PrimaryKey
    var id: Int? = null,

    @ColumnInfo(name = "id_merhant")
    val idMerhant: Int? = null,

    @ColumnInfo(name = "title")
    val title: String? = "",

    @ColumnInfo(name = "qty")
    val qty: Int? = 0,

    @ColumnInfo(name = "qtyProduct")
    val qtyProduct: Int? = 0,


    @ColumnInfo(name = "price")
    val price: Long = 0,

    @ColumnInfo(name = "total")
    val total: Long = 0,

    @ColumnInfo(name = "image")
    val image: String? = ""

)