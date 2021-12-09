package id.streetbox.live.ui.main.doortodoor.notification

import android.os.Bundle
import android.view.View
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import id.streetbox.live.R
import kotlinx.android.synthetic.main.activity_notif_instagram.*

class NotifInstagramActivity : AppCompatActivity() {
    var merchantIg: String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notif_instagram)

        initial()
    }

    private fun initial() {
        tvToolbarIg.title = "Instagram"
        tvToolbarIg.setNavigationOnClickListener {
            finish()
        }

        merchantIg = intent.getStringExtra("merchangIg")
        initWebView(merchantIg)

    }

    private fun initWebView(merchantIg: String?) {
        webViewIg.settings.loadsImagesAutomatically = true
        webViewIg.settings.javaScriptEnabled = true
        webViewIg.settings.domStorageEnabled = true
        webViewIg.settings.setSupportZoom(true)
        webViewIg.settings.builtInZoomControls = true
        webViewIg.settings.displayZoomControls = false
        webViewIg.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
        webViewIg.webViewClient = WebViewClient()
        webViewIg.loadUrl("https://www.instagram.com/$merchantIg")
    }
}