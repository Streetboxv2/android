package com.zeepos.utilities.gson

/**
 * Created by Arif S. on 7/11/20
 */
@Target(
    AnnotationTarget.FIELD, AnnotationTarget.CLASS,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
@Retention
annotation class Exclude