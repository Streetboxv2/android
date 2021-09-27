package com.zeepos.streetbox.ui.main.profile

import com.zeepos.models.master.User
import com.zeepos.ui_base.ui.BaseViewEvent
import okhttp3.ResponseBody

/**
 * Created by Arif S. on 5/16/20
 */
sealed class ProfileViewEvent : BaseViewEvent {
    data class GetProfileSuccess(val data: User) : ProfileViewEvent()
    data class GetProfileFailed(val message: String?) : ProfileViewEvent()
    data class GetChangePassword(val message: String?) : ProfileViewEvent()
    data class GetChangePasswordFailed(val message: String?) : ProfileViewEvent()
    data class DownloadReportSuccess(val responseBody: ResponseBody) : ProfileViewEvent()
    object DownloadReportFailed : ProfileViewEvent()

}