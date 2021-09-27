package com.zeepos.models.converter

import io.objectbox.converter.PropertyConverter
import org.json.JSONArray

/**
 * Created by Arif S. on 5/20/20
 */
class StringListConverter : PropertyConverter<List<String>, String> {
    override fun convertToDatabaseValue(entityProperty: List<String>?): String? {
        return try {
            if (entityProperty == null) "" else JSONArray(entityProperty).toString()
        } catch (e: java.lang.Exception) {
            null
        }
    }

    override fun convertToEntityProperty(databaseValue: String?): List<String> {
        return if (databaseValue == null) ArrayList() else try {
            val array = JSONArray(databaseValue)
            val ret: ArrayList<String> = ArrayList()
            for (i in 0 until array.length()) {
                ret.add(array.getString(i))
            }
            ret
        } catch (e: Exception) {
            ArrayList<String>()
        }
    }
}