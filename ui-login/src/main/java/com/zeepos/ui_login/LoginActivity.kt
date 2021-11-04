package com.zeepos.ui_login

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.zeepos.models.ConstVar
import com.zeepos.models.entities.RegisterParams
import com.zeepos.ui_base.ui.BaseActivity
import com.zeepos.ui_password.ForgotPasswordActivity
import com.zeepos.utilities.SharedPreferenceUtil
import io.objectbox.BoxStore.context
import kotlinx.android.synthetic.main.activity_login.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import javax.inject.Inject


/**
 * Created by Arif S. on 5/1/20
 */
class LoginActivity : BaseActivity<LoginViewEvent, LoginViewModel>() {

    @Inject
    lateinit var loginUiEvent: LoginUiEvent
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    private val appType: String by lazy {
        SharedPreferenceUtil.getString(this, ConstVar.APP_TYPE, ConstVar.EMPTY_STRING)
            ?: ConstVar.EMPTY_STRING
    }

    override fun initResourceLayout(): Int {
        return R.layout.activity_login
    }

    companion object {

        // Scope for reading user's contacts
        const val CONTACTS_SCOPE = "https://www.googleapis.com/auth/contacts.readonly"
        const val RC_SIGN_IN = 9001

        fun getIntent(context: Context): Intent {
            val intent = Intent(context, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            return intent
        }
    }

    override fun init() {
        viewModel = ViewModelProvider(this, viewModeFactory).get(LoginViewModel::class.java)

        if (appType == ConstVar.APP_MERCHANT) {
            viewModel.checkIsLoggedIn()
        }

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    override fun onStart() {
        super.onStart()

        if (appType == ConstVar.APP_CUSTOMER) {
            val account =
                GoogleSignIn.getLastSignedInAccount(this)

            account?.let {
                val loginParams = RegisterParams()
                loginParams.idToken = it.idToken
                loginParams.name = it.displayName
                viewModel.loginOrRegister(loginParams)
            }
        }
    }

    override fun onViewReady(savedInstanceState: Bundle?) {

        initLoginType()

        btn_login.setOnClickListener {
            val username = et_username.text.toString()
            val password = et_password.text.toString()
            showLoading()
            viewModel.login(username.trim(), password)
        }

        et_forgotpassword.setOnClickListener {
            startActivity(ForgotPasswordActivity.getIntent(this))
        }

        et_appversion.setText(GetAppVersion(this))
    }

    fun GetAppVersion(context: Context): String {
        var version = ""
        try {
            val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            version = pInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        return version
    }

    private fun initLoginType() {
        when (appType) {
            ConstVar.APP_CUSTOMER -> {
                logo.setImageResource(R.drawable.logo_end_user_with_text)
                ll_login_user.visibility = View.VISIBLE
                ll_login.visibility = View.GONE
                ll_main_bg.setBackgroundColor(ContextCompat.getColor(this, R.color.blue))

                initGoogleSignIn()
            }

            ConstVar.APP_RECRUITER -> {
                //TODO : Login recruiter
            }

            ConstVar.APP_MERCHANT -> {
                logo.setImageResource(R.drawable.logo_with_text)
                ll_login_user.visibility = View.GONE
                ll_login.visibility = View.VISIBLE
                ll_main_bg.setBackgroundColor(ContextCompat.getColor(this, R.color.orange))
            }
        }
    }

    private fun initGoogleSignIn() {
        val gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestScopes(Scope(CONTACTS_SCOPE))
                .requestEmail()
                .requestIdToken(getString(R.string.server_client_id))
                .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        sign_in_button.setSize(SignInButton.SIZE_WIDE)

        sign_in_button.setOnClickListener {
            signIn()
        }
    }

    private fun signIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onEvent(useCase: LoginViewEvent) {
        when (useCase) {
            LoginViewEvent.GoToMainScreen ->
                loginUiEvent.goToMainScreen(this)
            LoginViewEvent.GoToOperatorMainScreen ->
                loginUiEvent.goToOperatorFTScreen(this)
            is LoginViewEvent.LoginFailed ->
                if (useCase.errorMessage == "Http 401 Unauthorized") {
                    loginUiEvent.goToLoginScreen(this)
                } else {
                    Toast.makeText(
                        applicationContext,
                        useCase.errorMessage,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            LoginViewEvent.DismissLoading -> dismissLoading()
        }

        dismissLoading()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {

            val task =
                GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val account = task.getResult(ApiException::class.java)

                account?.let {
                    showLoading()
                    val loginParams = RegisterParams()
                    loginParams.idToken = it.idToken
                    loginParams.personId = it.id
                    loginParams.name = it.displayName
                    loginParams.photo = it.photoUrl.toString()
                    viewModel.loginOrRegister(loginParams)
                }
            } catch (e: ApiException) {
                Toast.makeText(this, "Google sign in failed :(", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun getContacts(account: GoogleSignInAccount) {
        val mAccount = account.account

        if (mAccount == null) {
            Log.w(ConstVar.TAG, "getContacts: null account")
            return
        }
//        GetContactsTask(this).execute(mAccount)
    }

    fun isEmailValid(email: String): Boolean {
        val expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
        val pattern: Pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
        val matcher: Matcher = pattern.matcher(email)
        return matcher.matches()
    }

//    private class GetContactsTask(activity: LoginActivity) :
//        AsyncTask<Account, Void, List<Person>>() {
//        private val mActivityRef: WeakReference<LoginActivity> = WeakReference(activity)
//
//        override fun doInBackground(vararg accounts: Account): List<Person> {
//            if (mActivityRef.get() == null) {
//                return arrayListOf()
//            }
//            val context: Context = mActivityRef.get()!!.applicationContext
//            try {
//                val credential: GoogleAccountCredential = GoogleAccountCredential.usingOAuth2(
//                    context,
//                    Collections.singleton(CONTACTS_SCOPE)
//                )
//                credential.setSelectedAccount(accounts[0])
//                val service: PeopleService = Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
//                    .setApplicationName("Google Sign In Quickstart")
//                    .build()
//                val connectionsResponse: ListConnectionsResponse = service
//                    .people()
//                    .connections()
//                    .list("people/me")
//                    .setFields("names,emailAddresses")
//                    .execute()
//                return connectionsResponse.getConnections()
//            } catch (recoverableException: UserRecoverableAuthIOException) {
//                if (mActivityRef.get() != null) {
//                    mActivityRef.get().onRecoverableAuthException(recoverableException)
//                }
//            } catch (e: IOException) {
//                Log.w(
//                    ConstVar.TAG,
//                    "getContacts:exception",
//                    e
//                )
//            }
//            return arrayListOf()
//        }
//
//        protected fun onPostExecute(people: List<Person?>?) {
//            super.onPostExecute(people)
//            if (mActivityRef.get() != null) {
//                mActivityRef.get().onConnectionsLoadFinished(people)
//            }
//        }
//
//    }

}
