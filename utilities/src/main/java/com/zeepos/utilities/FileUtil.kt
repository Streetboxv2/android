package com.zeepos.utilities

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.FileUtils
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.File.separator
import java.io.FileOutputStream

/**
 * Created by Arif S. on 7/30/20
 */
object FileUtil {
//    /**
//     * 判断外置存储是否可用
//     *
//     * @return the boolean
//     */
//    fun externalMounted(): Boolean {
//        val state: String = Environment.getExternalStorageState()
//        if (state == Environment.MEDIA_MOUNTED) {
//            return true
//        }
//        Log.w("FileUtil", "external storage unmounted")
//        return false
//    }
//
//    /**
//     * 返回以“/”结尾的内部存储根目录
//     */
//    fun getInternalRootPath(context: Context, type: String): String {
//        val file: File?
//        if (type.isEmpty()) {
//            file = context.filesDir
//        } else {
//            file =
//                File(FileUtils.separator(context.getFilesDir().getAbsolutePath()).toString() + type)
//            file.mkdirs()
//        }
//        var path = ""
//        if (file != null) {
//            path = FileUtils.separator(file.getAbsolutePath())
//        }
//        Log.d("FileUtil", "internal storage root path: $path")
//        return path
//    }
//
//    fun getInternalRootPath(context: Context): String? {
//        return getInternalRootPath(context, null)
//    }
//
//    /**
//     * 返回以“/”结尾的外部存储根目录，外置卡不可用则返回空字符串
//     */
//    fun getExternalRootPath(type: String): String {
//        var file: File? = null
//        if (externalMounted()) {
//            file = Environment.getExternalStorageDirectory()
//        }
//        if (file != null && type.isNotEmpty()) {
//            file = File(file, type)
//            file.mkdirs()
//        }
//        var path = ""
//        if (file != null) {
//            path = FileUtils.separator(file.getAbsolutePath())
//        }
//        Log.i("FileUtil", "external storage root path: $path")
//        return path
//    }
//
//    fun getExternalRootPath(): String? {
//        return getExternalRootPath(null)
//    }

//    /**
//     * 各种类型的文件的专用的保存路径，以“/”结尾
//     *
//     * @return 诸如：/mnt/sdcard/Android/data/[package]/files/[type]/
//     */
//    fun getExternalPrivatePath(context: Context, type: String?): String {
//        var file: File? = null
//        if (externalMounted()) {
//            file = context.getExternalFilesDir(type)
//        }
//        //高频触发java.lang.NullPointerException，是SD卡不可用或暂时繁忙么？
//        var path = ""
//        if (file != null) {
//            path = FileUtils.separator(file.absolutePath)
//        }
//        Log.i("FileUtil", "external storage private path: $path")
//        return path
//    }

//    fun getExternalPrivatePath(context: Context): String? {
//        return getExternalPrivatePath(context, null)
//    }
//
//    /**
//     * 下载的文件的保存路径，必须为外部存储，以“/”结尾
//     *
//     * @return 诸如 ：/mnt/sdcard/Download/
//     */
//    @Throws(RuntimeException::class)
//    fun getDownloadPath(): String? {
//        val file: File = if (externalMounted()) {
//            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
//        } else {
//            throw RuntimeException("外置存储不可用！")
//        }
//        return FileUtils.separator(file.getAbsolutePath())
//    }
//
//    /**
//     * 各种类型的文件的专用的缓存存储保存路径，优先使用外置存储，以“/”结尾
//     */
//    fun getCachePath(context: Context, type: String): String {
//        var file: File?
//        file = if (externalMounted()) {
//            context.getExternalCacheDir()
//        } else {
//            context.getCacheDir()
//        }
//        if (type.isNotEmpty()) {
//            file = File(file, type)
//            file.mkdirs()
//        }
//        var path = ""
//        if (file != null) {
//            path = FileUtils.separator(file.absolutePath)
//        }
//        Log.i("FileUtil", "external or internal storage cache path: $path")
//        return path
//    }
//
//    fun getCachePath(context: Context): String? {
//        return getCachePath(context, null)
//    }
//
//    /**
//     * 返回以“/”结尾的临时存储目录
//     */
//    fun getTempDirPath(context: Context): String {
//        return getExternalPrivatePath(context, "temporary")
//    }
//
//    /**
//     * 返回临时存储文件路径
//     */
//    fun getTempFilePath(context: Context): String? {
//        return try {
//            File.createTempFile("lyj_", ".tmp", context.getCacheDir()).getAbsolutePath()
//        } catch (e: IOException) {
//            getTempDirPath(context) + "lyj.tmp"
//        }
//    }

    /**
     * Get a file from a Uri.
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri     The Uri to query.
     */
    @Throws(Exception::class)
    fun getFileFromUri(context: Context, uri: Uri): File? {
        var path: String? = null

        // DocumentProvider
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (DocumentsContract.isDocumentUri(context, uri)) { // TODO: 2015. 11. 17. KITKAT
                // ExternalStorageProvider
                if (isExternalStorageDocument(uri)) {
                    val docId: String = DocumentsContract.getDocumentId(uri)
                    val split =
                        docId.split(":".toRegex()).toTypedArray()
                    val type = split[0]
                    if ("primary".equals(type, ignoreCase = true)) {
                        path = Environment.getExternalStorageDirectory()
                            .toString() + "/" + split[1]
                    }

                    // TODO handle non-primary volumes
                } else if (isDownloadsDocument(uri)) { // DownloadsProvider
                    val id: String = DocumentsContract.getDocumentId(uri)
                    val contentUri: Uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        java.lang.Long.valueOf(id)
                    )
                    path = getDataColumn(context, contentUri, null, null)
                } else if (isMediaDocument(uri)) { // MediaProvider
                    val docId: String = DocumentsContract.getDocumentId(uri)
                    val split =
                        docId.split(":".toRegex()).toTypedArray()
                    val type = split[0]
                    var contentUri: Uri? = null
                    if ("image" == type) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    } else if ("video" == type) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    } else if ("audio" == type) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    }
                    val selection = "_id=?"
                    val selectionArgs = arrayOf(
                        split[1]
                    )

                    if (contentUri != null)
                        path = getDataColumn(context, contentUri, selection, selectionArgs)
                } else if (isGoogleDrive(uri)) { // Google Drive
                    val TAG = "isGoogleDrive"
                    path = TAG
                    val docId: String = DocumentsContract.getDocumentId(uri)
                    val split =
                        docId.split(";".toRegex()).toTypedArray()
                    val acc = split[0]
                    val doc = split[1]

                    /*
                        * @details google drive document data. - acc , docId.
                        * */return saveFileIntoExternalStorageByUri(context, uri)
                } // MediaStore (and general)
            } else if ("content".equals(uri.scheme, ignoreCase = true)) {
                path = getDataColumn(context, uri, null, null)
            } else if ("file".equals(uri.scheme, ignoreCase = true)) {
                path = uri.path
            }
            if (path != null) File(path) else null
        } else {
            val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)

            if (cursor != null) File(cursor.getString(cursor.getColumnIndex("_data"))) else null
        }
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is GoogleDrive.
     */
    fun isGoogleDrive(uri: Uri): Boolean {
//        return uri.authority.equalsIgnoreCase("com.google.android.apps.docs.storage")
        return false
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    fun getDataColumn(
        context: Context, uri: Uri, selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        var cursor: Cursor? = null
        val column: String = MediaStore.Images.Media.DATA
        val projection = arrayOf(
            column
        )
        try {
            cursor = context.contentResolver.query(
                uri, projection, selection, selectionArgs,
                null
            )
            if (cursor != null && cursor.moveToFirst()) {
                val column_index: Int = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(column_index)
            }
        } finally {
            if (cursor != null) cursor.close()
        }
        return null
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }


    fun makeEmptyFileIntoExternalStorageWithTitle(title: String?): File {
        val root =
            Environment.getExternalStorageDirectory().absolutePath
        return File(root, title)
    }


    fun getFileName(context: Context, uri: Uri): String? {
        var result: String? = null
        if (uri.scheme.equals("content")) {
            val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)
            cursor.use { cursor ->
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/') ?: -1
            if (cut != -1) {
                result = result?.substring(cut + 1) ?: ""
            }
        }
        return result
    }


    @Throws(Exception::class)
    fun saveBitmapFileIntoExternalStorageWithTitle(
        bitmap: Bitmap,
        title: String
    ) {
        val fileOutputStream =
            FileOutputStream(makeEmptyFileIntoExternalStorageWithTitle("$title.png"))
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
        fileOutputStream.close()
    }


    @Throws(Exception::class)
    fun saveFileIntoExternalStorageByUri(
        context: Context,
        uri: Uri
    ): File? {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val originalSize: Int = inputStream.available()
        var bis: BufferedInputStream?
        var bos: BufferedOutputStream?
        val fileName = getFileName(context, uri)
        val file = makeEmptyFileIntoExternalStorageWithTitle(fileName)
        bis = BufferedInputStream(inputStream)
        bos = BufferedOutputStream(
            FileOutputStream(
                file, false
            )
        )

        val buf = ByteArray(originalSize)
        bis.read(buf)

        do {
            bos.write(buf)
        } while (bis.read(buf) != -1)

        bos.flush()
        bos.close()
        bis.close()
        return file
    }

    fun createScaledBitmap(bitmap: Bitmap, mMaxSize: Int): Bitmap {
        var width: Int = bitmap.width
        var height: Int = bitmap.height
        var maxSize = mMaxSize

        when {
            width > height -> {
                // landscape
                if (width < maxSize)
                    maxSize = width
                val ratio = width.toFloat() / maxSize
                width = maxSize
                height = (height / ratio).toInt()
            }
            height > width -> {
                // portrait
                if (height < maxSize)
                    maxSize = height
                val ratio = height.toFloat() / maxSize
                height = maxSize
                width = (width / ratio).toInt()
            }
            else -> {
                // square
                if (height < maxSize || width < maxSize)
                    maxSize = height
                height = maxSize
                width = maxSize
            }
        }

        return Bitmap.createScaledBitmap(bitmap, width, height, false)
    }
}