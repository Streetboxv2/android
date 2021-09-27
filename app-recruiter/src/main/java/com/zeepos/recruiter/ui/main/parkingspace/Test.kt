package com.zeepos.recruiter.ui.main.parkingspace
//
//package com.zeepos.recruiter.ui.main
//
//import android.annotation.SuppressLint
//import android.app.Activity
//import android.content.ContentValues
//import android.content.Context
//import android.content.Intent
//import android.content.pm.PackageManager
//import android.graphics.Bitmap
//import android.net.Uri
//import android.os.Build
//import android.os.Bundle
//import android.provider.MediaStore
//import android.widget.Toast
//import androidx.annotation.RequiresApi
//import androidx.appcompat.app.AppCompatActivity
//import com.github.dhaval2404.imagepicker.ImagePicker
//import com.zeepos.recruiter.R
//import com.zeepos.recruiter.util.FileUtil
//import kotlinx.android.synthetic.main.activity_main.*
//import java.io.BufferedReader
//import java.io.File
//import java.io.InputStreamReader
//import java.util.*
//
//
//class MainActivity : AppCompatActivity() {
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//        imagebrowse.setOnClickListener{
//            ImagePicker.with(this)
//                .crop()	    			//Crop image(Optional), Check Customization for more option
//                .compress(1024)			//Final image size will be less than 1 MB(Optional)
//                .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
//                .start()
//        }
//    }
//
//
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (resultCode == Activity.RESULT_OK) {
//            //Image Uri will not be null for RESULT_OK
//
//            val fileUri = data?.data
//            val fileName = fileUri!!.path;
//            imageprofile.setImageURI(fileUri)
//            //You can get File object from intent
//            val file: File? = ImagePicker.getFile(data)
//
//            //You can also get File Path from intent
//            val filePath:String? = ImagePicker.getFilePath(data).toString()
//
//            //You can get File object from intent
////            val file:File = ImagePicker.getFile(data)!!
//            val a = FileUtil.getFileInfo(file)
//            val b:String = file!!.absolutePath
//            val encodedUrl = Base64.getUrlEncoder().encodeToString(b.toByteArray())
//            println(encodedUrl)
//            //You can also get File Path from intent
//
//        } else if (resultCode == ImagePicker.RESULT_ERROR) {
//            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
//        } else {
//            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//
//    companion object {
//        fun getIntent(context: Context): Intent {
//            val intent = Intent(context, MainActivity::class.java)
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            return intent
//        }
//    }
//}