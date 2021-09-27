package com.zeepos.recruiter.ui.main.form

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.zeepos.models.entities.Menu
import com.zeepos.recruiter.R
import com.zeepos.recruiter.util.FileUtil
import com.zeepos.ui_base.ui.BaseActivity
import io.objectbox.BoxStore.context
import kotlinx.android.synthetic.main.app_bar.*
import kotlinx.android.synthetic.main.galery_photo.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class FormParkingSpaceActivity : BaseActivity<FormParkingSpaceViewEvent, FormParkingSpaceViewModel>() {
    var imagesPathList: MutableList<String> = arrayListOf()
    lateinit var imagePath: String
    var isImage1: Boolean? = false
    var isImage2: Boolean? = false
    var isImage3: Boolean? = false
    var isImage4: Boolean? = false
    var isImage5: Boolean? = false



    override fun initResourceLayout(): Int {
        return R.layout.parking_space
    }

    override fun init() {

    }

    override fun onViewReady(savedInstanceState: Bundle?) {
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.setNavigationOnClickListener { finish() }

        image1.setOnClickListener{
            isImage1 = true
            ImagePicker.with(this)
                .crop()	    			//Crop image(Optional), Check Customization for more option
                .compress(1024)			//Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)
                //Final image resolution will be less than 1080 x 1080(Optional)
                .start()
        }
        image2.setOnClickListener{
            isImage2 = true
            ImagePicker.with(this)
                .crop()	    			//Crop image(Optional), Check Customization for more option
                .compress(1024)			//Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)
                //Final image resolution will be less than 1080 x 1080(Optional)
                .start()
        }

        image3.setOnClickListener{
            isImage3 = true
            ImagePicker.with(this)
                .crop()	    			//Crop image(Optional), Check Customization for more option
                .compress(1024)			//Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)
                //Final image resolution will be less than 1080 x 1080(Optional)
                .start()
        }

        image4.setOnClickListener{
            isImage4 = true
            ImagePicker.with(this)
                .crop()	    			//Crop image(Optional), Check Customization for more option
                .compress(1024)			//Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)
                //Final image resolution will be less than 1080 x 1080(Optional)
                .start()
        }

        image5.setOnClickListener{
            isImage5 = true
            ImagePicker.with(this)
                .crop()	    			//Crop image(Optional), Check Customization for more option
                .compress(1024)			//Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)
                //Final image resolution will be less than 1080 x 1080(Optional)
                .start()
        }

    }

    private fun getPathFromURI(uri: Uri) {
        var path: String = uri.path!! // uri = any content Uri

        val databaseUri: Uri
        val selection: String?
        val selectionArgs: Array<String>?
        if (path.contains("/document/image:")) { // files selected from "Documents"
            databaseUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            selection = "_id=?"
            selectionArgs = arrayOf(DocumentsContract.getDocumentId(uri).split(":")[1])
        } else { // files selected from all other sources, especially on Samsung devices
            databaseUri = uri
            selection = null
            selectionArgs = null
        }
        try {
            val projection = arrayOf(
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.ORIENTATION,
                MediaStore.Images.Media.DATE_TAKEN
            ) // some example data you can query
            val cursor = contentResolver.query(
                databaseUri,
                projection, selection, selectionArgs, null
            )
            if (cursor!!.moveToFirst()) {
                val columnIndex = cursor.getColumnIndex(projection[0])
                imagePath = cursor.getString(columnIndex)
                // Log.e("path", imagePath);
                imagesPathList.add(imagePath)
            }
            cursor!!.close()
        } catch (e: Exception) {
            Log.e("TAG", e.message, e)
        }
    }

    companion object {

        fun getIntent(context: Context): Intent {
            val intent = Intent(context, FormParkingSpaceActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            return intent
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            //Image Uri will not be null for RESULT_OK

            val fileUri = data?.data
            val fileName = fileUri!!.path;
            val file: File? = ImagePicker.getFile(data)
            val imagePath:String = file!!.absolutePath

            if(isImage1==true) {
                image1.setImageURI(fileUri)
                isImage1 = false
            }
            if(isImage2==true) {
                image2.setImageURI(fileUri)
                isImage2 = false
            }

            val datas: MutableList<Menu> = ArrayList()
            if(image1!=null){
            datas.add(Menu(1, imagePath))}
            if(image2!=null){
                datas.add(Menu(2, imagePath))
            }
            if(image3!=null){
                datas.add(Menu(3, imagePath))
            }
            if(image4!=null){
                datas.add(Menu(4, imagePath))
            }
            if(image5!=null){
                datas.add(Menu(5, imagePath))
            }

            //You can get File object from intent


            //You can also get File Path from intent
            val filePath:String? = ImagePicker.getFilePath(data).toString()


            //You can get File object from intent
//            val file:File = ImagePicker.getFile(data)!!

            val encodedUrl = Base64.getUrlEncoder().encodeToString(imagePath.toByteArray())
            println(encodedUrl)
            //You can also get File Path from intent

        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }



    override fun onEvent(useCase: FormParkingSpaceViewEvent) {
        TODO("Not yet implemented")
    }


}
