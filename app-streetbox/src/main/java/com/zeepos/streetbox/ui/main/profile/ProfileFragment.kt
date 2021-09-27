package com.zeepos.streetbox.ui.main.profile

import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.DatePicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.messaging.FirebaseMessaging
import com.zeepos.models.ConstVar
import com.zeepos.streetbox.BuildConfig
import com.zeepos.streetbox.R
import com.zeepos.streetbox.ui.dialog.MonthYearPickerDialog
import com.zeepos.streetbox.ui.operatormerchant.OperatorActivity
import com.zeepos.ui_base.ui.BaseFragment
import com.zeepos.ui_login.LoginActivity
import com.zeepos.utilities.DateTimeUtil
import com.zeepos.utilities.PermissionUtils
import com.zeepos.utilities.Utils
import kotlinx.android.synthetic.main.dialog_food_truck_detail.*
import kotlinx.android.synthetic.main.profile_fragment.*
import kotlinx.android.synthetic.main.profile_fragment.tv_address
import okhttp3.ResponseBody
import java.io.*
import java.lang.reflect.Field
import java.util.*


class ProfileFragment : BaseFragment<ProfileViewEvent, ProfileViewModel>(), ChangePasswordListener {

    private lateinit var changePasswordDialog: ChangePasswordDialog

    override fun initResourceLayout(): Int {
        return R.layout.profile_fragment
    }

    override fun init() {
        viewModel = ViewModelProvider(this, viewModeFactory).get(ProfileViewModel::class.java)
//        viewModel.getProfile()
        changePasswordDialog = ChangePasswordDialog()

        val profile = Profile()
        profile.title = "Sales Report"
        profile.icon = R.mipmap.sales

    }

    override fun onViewReady(savedInstanceState: Bundle?) {

        showLoading()
        viewModel.getMerchantProfile()
//        val user = viewModel.getUserLocal()
//        tv_name.text = user?.name
//        tv_address.text = user?.address
//        tv_phone.text = user?.phone
//        tv_username.text = user?.userName

        bt_logout.setOnClickListener {
            context?.let {
                val alertDialogBuilder = AlertDialog.Builder(it)
                alertDialogBuilder.setMessage("Are you sure want to logout?")
                alertDialogBuilder.setPositiveButton(
                    "Logout"
                ) { p0, _ ->
                    viewModel.deleteSession()

                    FirebaseMessaging.getInstance().unsubscribeFromTopic("blast_${viewModel.getUser()?.id}")
                        .addOnSuccessListener {
                            dismissLoading()
                        }

                    p0?.dismiss()
                    activity?.let { activity ->
                        startActivity(LoginActivity.getIntent(activity))
                    }
                }
                alertDialogBuilder.setNegativeButton(
                    "Cancel"
                ) { p0, _ -> p0?.dismiss() }

                alertDialogBuilder.show()
            }
        }

        bt_changepassword.setOnClickListener {
            changePasswordDialog.show(childFragmentManager, "")
        }

        tv_food_truck.setOnClickListener {
            context?.let {
                startActivity(OperatorActivity.getIntent(it))
            }
        }

        tv_report.setOnClickListener {
//            selectDate()
//            createDialogWithoutDateField().show()
            val pd = MonthYearPickerDialog()
            pd.setListener(datePickerListener)
            pd.show(childFragmentManager, "MonthYearPickerDialog")
        }
    }

    private val datePickerListener = DatePickerDialog.OnDateSetListener { p0, p1, p2, p3 ->
        val selectedMonth = p2.toString()
        val selectedYear = p1.toString()

        showLoading()
        viewModel.downloadReport(selectedMonth, selectedYear, BuildConfig.SERVER)
    }

    override fun onEvent(useCase: ProfileViewEvent) {

        dismissLoading()
        when (useCase) {
            is ProfileViewEvent.GetChangePassword -> Toast.makeText(
                activity,
                "success",
                Toast.LENGTH_SHORT
            ).show()
            is ProfileViewEvent.GetChangePasswordFailed -> {
                Toast.makeText(activity, "failed", Toast.LENGTH_SHORT).show()
            }
            is ProfileViewEvent.GetProfileSuccess -> {
                dismissLoading()
                tv_name.text = useCase.data.name
                tv_address.text = useCase.data.address
                tv_phone.text = useCase.data.phone
                tv_username.text = useCase.data.email
            }
            is ProfileViewEvent.GetProfileFailed -> {
                dismissLoading()
                Toast.makeText(requireContext(), useCase.message, Toast.LENGTH_SHORT).show()
            }
            is ProfileViewEvent.DownloadReportSuccess -> {
                val task = object : AsyncTask<Void, Int, Void>() {
                    override fun doInBackground(vararg p0: Void): Void? {
                        writeResponseBodyToDownload(useCase.responseBody)
//                        writeResponseBodyToDisk(useCase.responseBody)
                        return null
                    }

                    override fun onPreExecute() {
                        super.onPreExecute()
                        dismissLoading()
                        val storageDir =
                            Utils.getAbsoluteFile(requireContext(), Environment.DIRECTORY_DOWNLOADS)
                        context?.let {
                            val alertDialogBuilder = AlertDialog.Builder(it)
//                            alertDialogBuilder.setMessage("Report has downloaded in ${storageDir.absolutePath}")
                            alertDialogBuilder.setMessage("Report has downloaded to Download folder")
                            alertDialogBuilder.setPositiveButton(
                                "OK"
                            ) { p0, _ ->
                                p0?.dismiss()
                            }
                            alertDialogBuilder.show()
                        }
                    }
                }
                task.execute()
            }
            ProfileViewEvent.DownloadReportFailed -> {
                dismissLoading()
                Toast.makeText(requireContext(), "Failed download report", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    }

    override fun onStart() {
        super.onStart()
        activity?.let {
            PermissionUtils.isReadWriteStoragePermissionGranted(it)
        }
    }

    private fun selectDate() {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        calendar.clear()

        calendar.timeInMillis = MaterialDatePicker.todayInUtcMilliseconds()
        calendar.roll(Calendar.MONTH, 1)

        val builder: MaterialDatePicker.Builder<Long> = MaterialDatePicker.Builder.datePicker()
        val currentTimeMillis = DateTimeUtil.getCurrentDateTime()
        val constraintBuilder = CalendarConstraints.Builder()

        builder.setTitleText("Select date")
        builder.setSelection(currentTimeMillis)

        constraintBuilder.setStart(currentTimeMillis)
        constraintBuilder.setOpenAt(currentTimeMillis)

        builder.setCalendarConstraints(constraintBuilder.build())

        val picker = builder.build()
        picker.addOnPositiveButtonClickListener {
            val selectedMonth = DateTimeUtil.getDateWithFormat(it, "MM")
            val selectedYear = DateTimeUtil.getDateWithFormat(it, "yyyy")

            showLoading()
            viewModel.downloadReport(selectedMonth, selectedYear, BuildConfig.SERVER)

        }
        picker.show(childFragmentManager, picker.toString())
    }

    private fun writeResponseBodyToDownload(body: ResponseBody): Boolean {
        return try {
            val tmpName =
                DateTimeUtil.getDateWithFormat(System.currentTimeMillis(), "yyyyMMdd_HHmmss")
            val futureStudioIconFile =
                File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    "${tmpName}_report.xlsx"
                )
            var inputStream: InputStream? = null
            var outputStream: OutputStream? = null
            try {
                val fileReader = ByteArray(4096)
                val fileSize = body.contentLength()
                var fileSizeDownloaded: Long = 0
                inputStream = body.byteStream()
                outputStream = FileOutputStream(futureStudioIconFile)
                while (true) {
                    val read: Int = inputStream.read(fileReader)
                    if (read == -1) {
                        break
                    }
                    outputStream.write(fileReader, 0, read)
                    fileSizeDownloaded += read.toLong()
                    Log.d(
                        ConstVar.TAG,
                        "file download: $fileSizeDownloaded of $fileSize"
                    )
                }
                outputStream.flush()
                true
            } catch (e: IOException) {
                false
            } finally {
                inputStream?.close()
                outputStream?.close()
            }
        } catch (e: IOException) {
            false
        }
    }

    private fun writeResponseBodyToDisk(body: ResponseBody): Boolean {
        return try {
            val storageDir =
                Utils.getAbsoluteFile(requireContext(), Environment.DIRECTORY_DOWNLOADS)

            if (!storageDir.exists()) {
                storageDir.mkdirs()
            }

            val tmpName =
                DateTimeUtil.getDateWithFormat(System.currentTimeMillis(), "yyyyMMdd_HHmmss")
            val file = File(storageDir, "${tmpName}_report.xlsx")
            file.createNewFile()

            var inputStream: InputStream? = null
            var outputStream: OutputStream? = null
            try {
                val fileReader = ByteArray(4096)
                val fileSize = body.contentLength()
                var fileSizeDownloaded: Long = 0
                inputStream = body.byteStream()
                outputStream = FileOutputStream(file)
                while (true) {
                    val read: Int = inputStream.read(fileReader)
                    if (read == -1) {
                        break
                    }
                    outputStream.write(fileReader, 0, read)
                    fileSizeDownloaded += read.toLong()
                    Log.d(
                        ConstVar.TAG,
                        "file download: $fileSizeDownloaded of $fileSize"
                    )
                }

//                val file2 = File(
//                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
//                    tmpName
//                )
//
//                var inputStream2: InputStream? = null
//                var outputStream2: OutputStream? = null
//                try {
//                    val fileReader2 = ByteArray(4096)
//                    var fileSizeDownloaded2: Long = 0
//                    inputStream2 = body.byteStream()
//                    outputStream2 = FileOutputStream(file2)
//                    while (true) {
//                        val read: Int = inputStream2.read(fileReader2)
//                        if (read == -1) {
//                            break
//                        }
//                        outputStream2.write(fileReader2, 0, read)
//                        fileSizeDownloaded2 += read.toLong()
//                    }
//                    outputStream2.flush()
//                } catch (e: IOException) {
//                    e.printStackTrace()
//                } finally {
//                    inputStream2?.close()
//                    outputStream2?.close()
//                }

                val resolver = context?.contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.TITLE, "StreetBox")
                    put(MediaStore.MediaColumns.DISPLAY_NAME, tmpName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/*")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        put(
                            MediaStore.MediaColumns.RELATIVE_PATH,
                            "${Environment.DIRECTORY_DOWNLOADS}/StreetBox"
                        )
                    }
                }

                val uri = resolver?.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    contentValues
                )

                uri?.let {
                    resolver.openOutputStream(it).use { outputStream ->
                        while (true) {
                            val read: Int = inputStream.read(fileReader)
                            if (read == -1) {
                                break
                            }
                            outputStream?.write(fileReader, 0, read)
                        }
                        contentValues.clear()
                    }
                }

                outputStream.flush()
                true
            } catch (e: IOException) {
                false
            } finally {
                inputStream?.close()
                outputStream?.close()
            }

        } catch (e: IOException) {
            false
        }
    }

    private fun createDialogWithoutDateField(): DatePickerDialog {
        val dpd = DatePickerDialog(requireContext(), null, 2014, 1, 24)
        try {
            val datePickerDialogFields: Array<Field> =
                dpd::class.java.declaredFields
            for (datePickerDialogField in datePickerDialogFields) {
                if (datePickerDialogField.name == "mDatePicker") {
                    datePickerDialogField.isAccessible = true
                    val datePicker: DatePicker = datePickerDialogField[dpd] as DatePicker
                    val datePickerFields =
                        datePickerDialogField.type.declaredFields
                    for (datePickerField in datePickerFields) {
                        Log.i("test", datePickerField.name)
                        if ("mDaySpinner" == datePickerField.name) {
                            datePickerField.isAccessible = true
                            val dayPicker = datePickerField[datePicker]
                            (dayPicker as View).visibility = View.GONE
                        }
                    }
                }
            }
        } catch (ex: Exception) {
        }
        return dpd
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PermissionUtils.RC_READ_WRITE_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    activity?.let {
                        PermissionUtils.isReadWriteStoragePermissionGranted(it)
                    }
                    Toast.makeText(
                        requireContext(),
                        getString(com.zeepos.map.R.string.location_permission_not_granted),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    companion object {
        fun newInstance(): ProfileFragment {
            return ProfileFragment()
        }
    }

    override fun onChangePassword(password: String) {
        viewModel.changePassword(password)
    }
}