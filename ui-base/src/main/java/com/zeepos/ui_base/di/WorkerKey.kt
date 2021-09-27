package com.zeepos.ui_base.di

import androidx.work.RxWorker
import dagger.MapKey
import kotlin.reflect.KClass

/**
 * Created by Arif S. on 7/9/20
 */
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
@Retention(AnnotationRetention.RUNTIME)
@MapKey
annotation class WorkerKey(val value: KClass<out RxWorker>)