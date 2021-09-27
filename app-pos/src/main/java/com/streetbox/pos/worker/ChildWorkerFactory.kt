package com.streetbox.pos.worker

import android.content.Context
import androidx.work.RxWorker
import androidx.work.WorkerParameters

/**
 * Created by Arif S. on 7/9/20
 */
interface ChildWorkerFactory {
    fun create(appContext: Context, params: WorkerParameters): RxWorker
}