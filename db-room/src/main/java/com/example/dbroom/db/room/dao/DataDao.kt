package com.example.dbroom.db.room.dao

import androidx.room.*
import com.example.dbroom.db.room.enitity.MenuItemStore

@Dao
interface DataDao {

    @Query("select * from tbl_menu_store")
    fun getAllDataListMenu(): List<MenuItemStore>

    @Query("select * from tbl_menu_store WHERE id_merhant=:id")
    fun getAllDataListMenuNearby(id: Int): List<MenuItemStore>

    @Insert
    fun addDataMenuStore(menuItemStore: MenuItemStore)

    @Update
    fun editDataMenutStore(menuItemStore: MenuItemStore)

    @Query("select * from tbl_menu_store WHERE id=:id")
    fun getItemMenutStore(id: Int): Int

    @Query("select id_merhant from tbl_menu_store where id_merhant=:id")
    fun getIdMerchantStore(id: Int): Int

    @Query("delete from tbl_menu_store WHERE id_merhant not in (id=:id)")
    fun deleteDataIdMerchant(id: Int)

    @Query("delete from tbl_menu_store WHERE id=:id")
    fun deleteDataMenuStore(id: Int)

    @Query("delete from tbl_menu_store")
    fun deleteAllMenuStore()
}