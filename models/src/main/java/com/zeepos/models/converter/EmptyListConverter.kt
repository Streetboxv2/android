package com.zeepos.models.converter

import io.objectbox.converter.PropertyConverter

/**
 * Created by Arif S. on 5/26/20
 */
class EmptyListConverter : PropertyConverter<List<Any>, String> {
    override fun convertToDatabaseValue(entityProperty: List<Any>?): String {
        return ""
    }

    override fun convertToEntityProperty(databaseValue: String?): List<Any> {
        return ArrayList()
    }
}