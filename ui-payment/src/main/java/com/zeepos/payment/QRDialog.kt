package com.zeepos.payment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.zeepos.models.ConstVar
import com.zeepos.ui_base.ui.BaseDialogFragment
import com.zeepos.utilities.SharedPreferenceUtil
import com.zeepos.utilities.Utils
import com.zeepos.utilities.intentPageData
import kotlinx.android.synthetic.main.dialog_qr.*


/**
 * Created by Arif S. on 7/17/20
 */
class QRDialog : BaseDialogFragment() {

    override fun initResourceLayout(): Int {
        return R.layout.dialog_qr
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {

        } catch (ex: ClassCastException) {

        }
    }

    override fun onResume() {
        super.onResume()
        val params = dialog?.window?.attributes

        if (activity != null) {
            val appType =
                SharedPreferenceUtil.getString(
                    requireActivity(),
                    ConstVar.APP_TYPE,
                    ConstVar.EMPTY_STRING
                )
                    ?: ConstVar.EMPTY_STRING

            if (appType == ConstVar.APP_CUSTOMER) {
                params?.width = (Utils.getScreenWidth(requireActivity()) / 1.3).toInt()
            } else {
                params?.width = Utils.getScreenWidth(requireActivity()) / 2
            }
        } else
            params?.width = ViewGroup.LayoutParams.WRAP_CONTENT
        params?.height = ViewGroup.LayoutParams.WRAP_CONTENT

        dialog?.window?.attributes = params as WindowManager.LayoutParams
    }

    override fun onViewReady(savedInstanceState: Bundle?) {

        val bundle = requireArguments()
        val content = bundle.getString(QR_CONTENT)

        val multiFormatWriter = MultiFormatWriter()
        try {
            val bitMatrix =
                multiFormatWriter.encode(content, BarcodeFormat.QR_CODE, 200, 200)
            val barcodeEncoder = BarcodeEncoder()
            val bitmap: Bitmap = barcodeEncoder.createBitmap(bitMatrix)
            iv_qr?.setImageBitmap(bitmap)

            activity?.let {
                val appType: String =
                    SharedPreferenceUtil.getString(it, ConstVar.APP_TYPE, ConstVar.EMPTY_STRING)
                        ?: ConstVar.EMPTY_STRING

                if (appType == ConstVar.APP_CUSTOMER) {
                    val qrUri = Utils.bitmapToFile(it, bitmap, "QR_StreetBox")
                }
            }
        } catch (e: WriterException) {
            e.printStackTrace()
        }

        btn_done.setOnClickListener {
            activity?.let {
                val appType: String =
                    SharedPreferenceUtil.getString(it, ConstVar.APP_TYPE, ConstVar.EMPTY_STRING)
                        ?: ConstVar.EMPTY_STRING

                if (appType == ConstVar.APP_CUSTOMER) {
                   saveQr()

                }else {
                    dismiss()
                    activity?.setResult(Activity.RESULT_OK)
                    activity?.finish()
                }

            }

        }
    }


    fun saveQr(){
        val alertDialogBuilder = AlertDialog.Builder(context as Context)
        alertDialogBuilder.setMessage("Upload your QR to scan in your Gallery on Streetbox folder")
        alertDialogBuilder.setPositiveButton(
            "OK"
        ) { p0, _ ->
            p0?.dismiss()
            activity?.setResult(Activity.RESULT_OK)
            activity?.finish()
        }
        alertDialogBuilder.show()

    }

    companion object {

        private const val QR_CONTENT: String = "QR_CONTENT"

        fun getInstance(qrContent: String): QRDialog {
            val fragment = QRDialog()
            val bundle = Bundle()
            bundle.putString(QR_CONTENT, qrContent)
            fragment.arguments = bundle
            return fragment
        }
    }
}