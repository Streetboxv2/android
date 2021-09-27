package com.zeepos.utilities

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.view.View
import android.view.Window
import android.view.WindowManager
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import kotlin.math.roundToInt

/**
 * Created by Arif S. on 5/18/20
 */
object Utils {
    fun pxToDp(px: Float): Float {
        val densityDpi = Resources.getSystem().displayMetrics.densityDpi
        return px / (densityDpi / 160f)
    }

    fun dpToPx(dp: Float): Int {
        val density = Resources.getSystem().displayMetrics.density
        return (dp * density).roundToInt()
    }

    fun enableTransparentStatusBar(window: Window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val winParams = window.attributes
            winParams.flags =
                winParams.flags and WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS.inv()
            window.attributes = winParams
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
    }

    fun getScreenWidth(activity: Activity): Int {
        val displayMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)

        return displayMetrics.widthPixels
    }

    fun getScreenHeight(activity: Activity): Int {
        val displayMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)

        return displayMetrics.heightPixels
    }

    fun bitmapToFile(context: Context, bitmap: Bitmap, fileName: String = ""): Uri {
        val name: String = if (fileName.isEmpty()) {
            val currentTime =
                DateTimeUtil.getDateWithFormat(System.currentTimeMillis(), "yyyyMMdd_HHmmss")
            currentTime
        } else {
            fileName
        }

        val path = "StreetBox/"
        val storageDir = getAbsoluteFile(context, path)

        try {

            if (!storageDir.exists()) {
                storageDir.mkdirs()
            }

            val file = File(storageDir, "$name.jpg")
            file.createNewFile()

            // Compress the bitmap and save in jpg format
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val tmpName = DateTimeUtil.getDateWithFormat(System.currentTimeMillis(), "yyyyMMdd_HHmmss")
        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.TITLE, "StreetBox")
            put(MediaStore.MediaColumns.DISPLAY_NAME, tmpName)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/StreetBox")
            }
        }

        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        uri?.let {
            resolver.openOutputStream(it).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
                contentValues.clear()

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    resolver.update(it, contentValues, null, null)
                }
            }
        }

        return Uri.parse(storageDir.absolutePath)
    }

    fun getAbsoluteFile(
        context: Context,
        relativePath: String
    ): File {
        return if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
            File(context.getExternalFilesDir(null), relativePath)
        } else {
            File(context.filesDir, relativePath)
        }
    }
}