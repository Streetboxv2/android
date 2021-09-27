package com.zeepos.utilities

import android.content.Context
import android.content.SharedPreferences

/**
 * Created by Arif S. on 5/11/20
 */
object SharedPreferenceUtil {
    private const val PREF_FILE = "PREF"

    /**
     * Set a string shared preference
     * @param key - Key to set shared preference
     * @param value - Value for the key
     */
    fun setString(
        context: Context,
        key: String?,
        value: String?
    ) {
        val settings: SharedPreferences = context.getSharedPreferences(PREF_FILE, 0)
        val editor = settings.edit()
        editor.putString(key, value)
        editor.apply()
    }

    /**
     * Set a integer shared preference
     * @param key - Key to set shared preference
     * @param value - Value for the key
     */
    fun setInt(
        context: Context,
        key: String?,
        value: Int
    ) {
        val settings: SharedPreferences = context.getSharedPreferences(PREF_FILE, 0)
        val editor = settings.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    /**
     * Set a Boolean shared preference
     * @param key - Key to set shared preference
     * @param value - Value for the key
     */
    fun setBoolean(
        context: Context,
        key: String?,
        value: Boolean
    ) {
        val settings: SharedPreferences = context.getSharedPreferences(PREF_FILE, 0)
        val editor = settings.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    /**
     * Get a string shared preference
     * @param key - Key to look up in shared preferences.
     * @param defValue - Default value to be returned if shared preference isn't found.
     * @return value - String containing value of the shared preference if found.
     */
    fun getString(
        context: Context,
        key: String?,
        defValue: String?
    ): String? {
        val settings: SharedPreferences = context.getSharedPreferences(PREF_FILE, 0)
        return settings.getString(key, defValue)
    }

    /**
     * Get a integer shared preference
     * @param key - Key to look up in shared preferences.
     * @param defValue - Default value to be returned if shared preference isn't found.
     * @return value - String containing value of the shared preference if found.
     */
    fun getInt(context: Context, key: String?, defValue: Int): Int {
        val settings: SharedPreferences = context.getSharedPreferences(PREF_FILE, 0)
        return settings.getInt(key, defValue)
    }

    /**
     * Get a boolean shared preference
     * @param key - Key to look up in shared preferences.
     * @param defValue - Default value to be returned if shared preference isn't found.
     * @return value - String containing value of the shared preference if found.
     */
    fun getBoolean(
        context: Context,
        key: String?,
        defValue: Boolean
    ): Boolean {
        val settings: SharedPreferences = context.getSharedPreferences(PREF_FILE, 0)
        return settings.getBoolean(key, defValue)
    }
}