package com.example.dbroom.db.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.dbroom.db.room.dao.DataDao
import com.example.dbroom.db.room.enitity.MenuItemStore

@Database(
    entities = [MenuItemStore::class],
    version = 4
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dataDao(): DataDao

    companion object {
        @Volatile
        var INSTANCE: AppDatabase? = null
        fun getInstance(context: Context): AppDatabase {
            if (INSTANCE == null) {
                synchronized(this) {
                    INSTANCE = Room.databaseBuilder(
                        context,
                        AppDatabase::class.java,
                        "data_menu_store.db"
                    ).allowMainThreadQueries().fallbackToDestructiveMigration()
                        .build()
                }
            }

            return INSTANCE as AppDatabase
        }
    }
}