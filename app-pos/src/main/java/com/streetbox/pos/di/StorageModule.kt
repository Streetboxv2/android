package com.streetbox.pos.di

import android.util.Log
import com.google.gson.Gson
import com.streetbox.pos.App
import com.streetbox.pos.BuildConfig
import com.zeepos.localstorage.Storage
import com.zeepos.models.MyObjectBox
import dagger.Module
import dagger.Provides
import io.objectbox.BoxStore
import io.objectbox.android.AndroidObjectBrowser
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
        val boxStore = MyObjectBox.builder()
            .androidContext(application)
            .build()

        if (BuildConfig.DEBUG) {
            val started = AndroidObjectBrowser(boxStore).start(application)
            Log.i("ObjectBrowser", "Started: $started")
        }

        return boxStore
    }
}