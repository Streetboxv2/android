package com.zeepos.localstorage

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson

/**
 * Created by Arif S. on 5/2/20
 */
class Storage private constructor(
    preferences: SharedPreferences, gson: Gson
) : GsonStorage(preferences, gson) {

    companion object {

        private var instance: Storage? = null

        @Synchronized
        fun getDefault(context: Context): Storage {
            if (instance == null) {
                instance = createInstance(context, null, Gson())
            }
            return instance as Storage
        }

        @Synchronized
        private fun getDefault(context: Context, name: String): Storage {
            if (instance == null) {
                instance = createInstance(context, name, Gson())
            }
            return instance as Storage
        }

        @Synchronized
        fun getDefault(context: Context, gson: Gson): Storage {
            if (instance == null) {
                instance = createInstance(context, context.packageName, gson)
            }
            return instance as Storage
        }

        private fun createInstance(
            context: Context, sharedPreferencesName: String?,
            gson: Gson
        ): Storage {
            val sharedPreferences = context.getSharedPreferences(
                sharedPreferencesName ?: context.packageName + "_JsonStorage", Context.MODE_PRIVATE
            )
            return Storage(sharedPreferences, gson)
        }
    }
}