package com.mahmoud.udacitynotifier

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.webkit.CookieManager
import android.webkit.ValueCallback
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    var title = ""
    var success = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        webview.settings.javaScriptEnabled = true

        webview.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                Log.e("urlll", url)
                webview.postDelayed({
                    webview.evaluateJavascript(
                        "(function() { return (document.getElementsByTagName('title')[0].innerText); })();",
                        ValueCallback<String?> { html ->
                            html?.let {
                                title = it
                            }
                            Log.e("HTML", html)

                            // code here
                        })
                }, 1000)

            }

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                Log.e("url", url)
                view?.loadUrl(url)
                return true
            }

        }

        webview.settings.domStorageEnabled = true

        CookieManager.getInstance().setAcceptCookie(true)
        CookieManager.getInstance().acceptThirdPartyCookies(webview)


        webview.loadUrl(
            "https://mentor-dashboard.udacity.com/queue/overview"
        )
        button_don_login.setOnClickListener {
            //Udacity: Mentor Dashboard
            if (title.contains("Udacity: Mentor Dashboard", true)) {
                success = true
                onBackPressed()
            } else {
                Toast.makeText(this, "It seems login not complete", Toast.LENGTH_LONG).show()
                success = false
                return@setOnClickListener
            }
        }

    }

    override fun onBackPressed() {
        if (!success) setResult(Activity.RESULT_CANCELED) else setResult(Activity.RESULT_OK)
        super.onBackPressed()

    }
}
