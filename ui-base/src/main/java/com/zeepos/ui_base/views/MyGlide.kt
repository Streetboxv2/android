package com.zeepos.ui_base.views

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import com.zeepos.utilities.SslUtils
import okhttp3.OkHttpClient
import java.io.InputStream
import javax.net.ssl.X509TrustManager

/**
 * Created by Arif S. on 7/21/20
 */
@GlideModule
class MyGlide : AppGlideModule() {
    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        val builder = OkHttpClient.Builder()
        builder.sslSocketFactory(
            SslUtils.getUnSaveSslContext().socketFactory,
            SslUtils.getTrustManager()[0] as X509TrustManager
        )
        builder.hostnameVerifier { _, _ -> true }

        registry.replace(
            GlideUrl::class.java,
            InputStream::class.java,
            OkHttpUrlLoader.Factory(builder.build())
        )
    }
}