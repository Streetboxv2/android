package com.zeepos.utilities.gson

import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes

/**
 * Created by Arif S. on 7/11/20
 */
class HiddenAnnotationExclusionStrategy : ExclusionStrategy {
    override fun shouldSkipClass(clazz: Class<*>): Boolean {
        return clazz.getAnnotation(Exclude::class.java) != null
    }

    override fun shouldSkipField(f: FieldAttributes): Boolean {
        val hiddenAnnotation = f.getAnnotation(Exclude::class.java)
        return hiddenAnnotation != null
    }

}