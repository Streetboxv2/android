package com.zeepos.streetbox.di

import com.google.gson.Gson
import com.zeepos.localstorage.Storage
import com.zeepos.models.MyObjectBox
import com.zeepos.streetbox.App
import dagger.Module
import dagger.Provides
import io.objectbox.BoxStore
import javax.inject.Singleton


/**
 * Created by Arif S. on 5/2/20
 */

@Module
class StorageModule {
    @Provides
    @Singleton
    internal fun provideStorage(application: App, gson: Gson): Storage {
        return Storage.getDefault(application, gson)
    }

    @Provides
    @Singleton
    internal fun provideObjectBox(application: App): BoxStore {
        return MyObjectBox.builder()
            .androidContext(application)
            .build()

//        if (BuildConfig.DEBUG) {
//            val started = AndroidObjectBrowser(boxStore).start(application)
//            Log.i("ObjectBrowser", "Started: $started")
//        }

//        return boxStore
    }
}