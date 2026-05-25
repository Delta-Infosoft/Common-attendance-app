package com.i.common.attendance.ui.home.webview.activity

import android.app.AlertDialog
import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.SslErrorHandler
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import com.i.common.attendance.base.BaseActivity
import com.i.common.attendance.databinding.ActivityWebviewBinding

class WebViewActivity : BaseActivity() {
    private lateinit var binding : ActivityWebviewBinding

    companion object{
        const val WEB_VIEW_TITLE = "WEB_VIEW_TITLE"
        const val WEB_VIEW_URL = "WEB_VIEW_URL"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        moveOnClickListeners()
        setUpToolBar()
        setUpData()
        manageBackStack()
    }

    private fun moveOnClickListeners() = with(binding){
        imgViewBack.setOnClickListener {
            this@WebViewActivity.finish()
        }
    }
    private fun setUpToolBar() = with(binding) {
        progressBarWebView.visibility = View.VISIBLE
    }
    private fun manageBackStack(){
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    // Check if back navigation should be allowed
                    this@WebViewActivity.finish()
                }
            }
        this.onBackPressedDispatcher.addCallback(this, callback)
    }

    private fun setUpData() {
        binding.webView.apply {
            // Enable JavaScript if needed
            settings.javaScriptEnabled = true

            // Handle SSL errors gracefully
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                    val newUrl = request?.url.toString()
                    intent?.extras?.let {
                       /* if(it.getBoolean(AppConstant.IS_FROM_WEB_VIEW_REMOVE_AD)){
                            //provideMessage(this@WebViewActivity,"Url:$newUrl")
                            Log.e("WebView", "New URL to be loaded: $newUrl")
                            // Check URL to determine subscription status
                            //checkSubscriptionStatus(newUrl)
                        }*/
                    }
                    // Return false to let WebView load the URL
                    return false
                }
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    binding.progressBarWebView.visibility = View.VISIBLE
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    binding.progressBarWebView.visibility = View.GONE
                    Log.e("WebView", "onPageFinished: $url")
                    // Check URL to determine subscription status
                    //url?.let { adPlacemenetStatus(it) }
                }
                override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
                    // Log the SSL error for debugging purposes
                    val message = "SSL Error: ${error?.primaryError}"
                    Log.e("WebView SSL Error", message)

                    // Show a dialog to the user to choose whether to proceed or cancel
                    val builder = AlertDialog.Builder(this@WebViewActivity)
                    builder.setTitle("SSL Certificate Error")
                    builder.setMessage("The certificate for this site is not trusted. Do you want to continue anyway?")

                    builder.setPositiveButton("Continue") { _, _ ->
                        handler?.proceed() // Proceed with loading the content despite SSL error
                    }

                    builder.setNegativeButton("Cancel") { _, _ ->
                        handler?.cancel() // Cancel the request
                    }

                    val dialog = builder.create()
                    dialog.show()
                }
            }

            intent?.extras?.let {
                binding.txtViewTitle.text = it.getString(WEB_VIEW_TITLE)
                // Load the URL
                it.getString(WEB_VIEW_URL)?.let { it1 -> loadUrl(it1) }
            }
        }
    }
}