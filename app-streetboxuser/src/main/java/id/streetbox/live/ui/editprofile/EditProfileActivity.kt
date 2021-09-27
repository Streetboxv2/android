package id.streetbox.live.ui.editprofile

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.zeepos.models.ConstVar
import com.zeepos.models.master.User
import com.zeepos.ui_base.ui.BaseActivity
import com.zeepos.ui_base.views.GlideApp
import id.streetbox.live.R
import com.zeepos.utilities.FileUtil
import kotlinx.android.synthetic.main.activity_edit_profile.*
import java.io.File
import javax.inject.Inject

/**
 * Created by Arif S. on 6/12/20
 */
class EditProfileActivity : BaseActivity<EditProfileViewEvent, EditProfileViewModel>(), ChangePasswordDialog.ChangePasswordListener {

    @Inject
    lateinit var gson: Gson
    private lateinit var user: User
    private var uriPhoto: Uri? = null
    private val changePasswordDialog: ChangePasswordDialog = ChangePasswordDialog()

    override fun initResourceLayout(): Int {
        return R.layout.activity_edit_profile
    }

    override fun init() {
        viewModel = ViewModelProvider(this, viewModeFactory).get(EditProfileViewModel::class.java)

        val bundle = intent.extras!!
        val userStr = bundle.getString("user")
        user = gson.fromJson(userStr, User::class.java)
    }

    override fun onViewReady(savedInstanceState: Bundle?) {
        toolbar.setNavigationOnClickListener { finish() }
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.nv_save -> {
                    updateUser()
                    true
                }
                else -> false
            }
        }

        tv_chang_photo.setOnClickListener {
            openGalleryForImage()
        }

//        btn_change_password.setOnClickListener {
//            changePasswordDialog.show(supportFragmentManager, "")
//        }

        setEditData(user)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == RC_IMAGE) {
            data?.data?.let {
                uriPhoto = it

                val file = FileUtil.getFileFromUri(this, it)

                GlideApp.with(this)
                    .load(file)
                    .override(400, 400)
                    .error(R.mipmap.ic_launcher_round)
                    .into(iv_profile)

            }
        }
    }

    private fun setEditData(user: User) {
        et_name.setText(user.name)
        et_address.setText(user.address)
        et_phone.setText(user.phone)
        et_email.setText(user.userName)

        println("user.profilePicture")
        println(user)

        if (user.profilePicture == "" || user.profilePicture == null) {
            GlideApp.with(this)
                    .load(R.mipmap.ic_launcher_round)
                    .error(R.mipmap.ic_launcher_round)
                    .into(iv_profile)
        }else {
            val imageUrl = ConstVar.PATH_IMAGE + user.profilePicture

            GlideApp.with(this)
                    .load(imageUrl)
                    .error(R.mipmap.ic_launcher_round)
                    .into(iv_profile)
        }
    }

    override fun onEvent(useCase: EditProfileViewEvent) {
        dismissLoading()
        when (useCase) {
            is EditProfileViewEvent.UpdateUserSuccess -> finish()
            is EditProfileViewEvent.UpdateUserFailed -> Toast.makeText(
                this,
                "Failed! update data",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun updateUser() {
        showLoading()
        val user = User()
        user.name = et_name.text.toString()
        user.address = et_address.text.toString()
        user.phone = et_phone.text.toString()
        user.userName = this.user.userName

        var file: File? = null

        uriPhoto?.let {
            file = FileUtil.getFileFromUri(this, it)
//            file = File(it.path!!)
            Log.d(ConstVar.TAG, "${file?.name}")
        }

        viewModel.updateUser(user, file)

    }

    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, RC_IMAGE)
    }

    companion object {
        private const val RC_IMAGE = 1
        fun getIntent(context: Context, bundle: Bundle): Intent {
            val intent = Intent(context, EditProfileActivity::class.java)
            intent.putExtras(bundle)
            return intent
        }
    }

    override fun onChangePassword(password: String) {
        Toast.makeText(this, "Change password", Toast.LENGTH_SHORT).show()
    }
}