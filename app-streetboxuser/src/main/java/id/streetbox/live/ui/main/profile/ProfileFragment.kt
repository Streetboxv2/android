package id.streetbox.live.ui.main.profile

import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.graphics.Paint
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.dbroom.db.room.AppDatabase
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.orhanobut.hawk.Hawk
import com.zeepos.models.ConstVar
import com.zeepos.models.master.User
import com.zeepos.ui_base.ui.BaseFragment
import com.zeepos.ui_base.views.GlideApp
import com.zeepos.ui_login.LoginActivity
import com.zeepos.utilities.*
import id.streetbox.live.R
import id.streetbox.live.ui.editprofile.EditProfileActivity
import id.streetbox.live.ui.main.address.AddressDeliveryActivity
import id.streetbox.live.ui.main.doortodoor.DoortoDoorViewEvent
import id.streetbox.live.ui.termsconditions.TermConditionActivity
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.fragment_notification_doorto_door.*
import kotlinx.android.synthetic.main.profile_fragment.*
import kotlinx.android.synthetic.main.profile_fragment.iv_profile
import javax.inject.Inject

/**
 * Created by Arif S. on 6/12/20
 */
class ProfileFragment : BaseFragment<ProfileViewEvent, ProfileViewModel>() {

    @Inject
    lateinit var gson: Gson
    private lateinit var user: User
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    override fun initResourceLayout(): Int {
        return R.layout.profile_fragment
    }

    override fun init() {
        viewModel = ViewModelProvider(this, viewModeFactory).get(ProfileViewModel::class.java)
    }

    override fun onStart() {
        super.onStart()
        activity?.let {
            PermissionUtils.isReadWriteStoragePermissionGranted(it)
        }
    }

    override fun onResume() {
        super.onResume()
        showLoading()
        viewModel.getUserInfoCloud()
        viewModel.callGetListNotif()
    }

    override fun onViewReady(savedInstanceState: Bundle?) {
        btn_see_detail.paintFlags = Paint.UNDERLINE_TEXT_FLAG

        btn_see_detail.setOnClickListener {
            context?.let { context ->
                val bundle = Bundle()
                bundle.putString("user", gson.toJson(user))
                startActivity(EditProfileActivity.getIntent(context, bundle))
            }
        }
        btn_terms.setOnClickListener {
            context?.let { context ->
                val bundle = Bundle()
                bundle.putString("user", gson.toJson(user))
                startActivity(TermConditionActivity.getIntent(context, bundle))
            }
        }

        btn_logout.setOnClickListener {
            context?.let {
                val alertDialogBuilder = AlertDialog.Builder(it)
                alertDialogBuilder.setMessage("Are you sure want to logout?")
                alertDialogBuilder.setPositiveButton(
                    "Logout"
                ) { p0, _ ->
                    signOut(p0)

                }
                alertDialogBuilder.setNegativeButton(
                    "Cancel"
                ) { p0, _ -> p0?.dismiss() }

                alertDialogBuilder.show()
            }
        }

        tv_food_truck.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("user", gson.toJson(user))
            val intent = intentPageData(requireContext(), AddressDeliveryActivity::class.java)
                .putExtra("from", "profile")
                .putExtras(bundle)
            startActivity(intent)
        }

        val user = viewModel.getUserLocal()

        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        val defaultValue = true
        val isNotif = sharedPref.getBoolean("SAVETOPIC", defaultValue)
//        showLoading()

        val getSaveTopic = Hawk.get<String>("saveTopic")

        if (isNotif) {
            suscribeNotif(user?.id.toString())
            switchNotif.isChecked = true
        } else {
            switchNotif.isChecked = false
        }
//        if (getSaveTopic != null) {
//            if (getSaveTopic.equals("save"))
//                switchNotif.isChecked = true
//        } else {
//            suscribeNotif(user?.id.toString())
//            switchNotif.isChecked = false
//        }


        switchNotif.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                suscribeNotif(user?.id.toString())
            } else {
                unSuscribeNotif(user?.id.toString())
            }
        }

    }

    fun suscribeNotif(userId: String) {
        showLoading()
        Hawk.put("saveTopic", "save")
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        with (sharedPref.edit()) {
            putBoolean("SAVETOPIC", true)
            apply()
        }
        FirebaseMessaging.getInstance().subscribeToTopic("blast_$userId")
            .addOnSuccessListener {
                dismissLoading()
            }
    }

    fun unSuscribeNotif(userId: String) {
        showLoading()
        Hawk.delete("saveTopic")
        val getSaveTopic = Hawk.get<String>("saveTopic")
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        with (sharedPref.edit()) {
            putBoolean("SAVETOPIC", false)
            apply()
        }
        FirebaseMessaging.getInstance().unsubscribeFromTopic("blast_$userId")
            .addOnSuccessListener {
                dismissLoading()
            }
    }
    private fun signOut(dialog: DialogInterface) {
        val gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(com.zeepos.ui_login.R.string.server_client_id))
                .build()
        mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
        mGoogleSignInClient.signOut()
            .addOnCompleteListener {
                viewModel.deleteSession()
                context?.let { it1 ->
                    AppDatabase.getInstance(it1)
                        .dataDao().deleteAllMenuStore()
                }
                dialog.dismiss()
                activity?.let { activity ->
                    startActivity(LoginActivity.getIntent(activity))
                }
            }
    }

    override fun onEvent(useCase: ProfileViewEvent) {
        dismissLoading()
        when (useCase) {
            is ProfileViewEvent.GetUserInfoSuccess -> {
                user = useCase.user
                updateUserInfoUi(useCase.user)
            }
            is ProfileViewEvent.GetUserInfoFailed -> {
            }
            is ProfileViewEvent.OnSuccessListNotif -> {
                dismissLoading()
                val dataItem = useCase.responseListNotificationBlast.data
//                if (dataItem!!.isNotEmpty())
//                    initAdapter(dataItem)
//                else {
//                    showView(tvNotFoundNotif)
//                    hideView(rvListNotifBlast)
//                }
            }
            is ProfileViewEvent.OnFailed -> {
                dismissLoading()
            }
        }
    }

    private fun updateUserInfoUi(user: User) {
//        tv_location.text = user.address
        tv_name.text = user.name
//        tv_food_truck.text = user.phone
//        tv_report.text = user.userName

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

            println("respon Url $imageUrl")
        }
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
}