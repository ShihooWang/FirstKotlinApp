package cc.bodyplus.health.mvp.view.report.fragment

import android.annotation.SuppressLint
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import cc.bodyplus.health.App
import cc.bodyplus.health.R
import cc.bodyplus.health.R.id.webview
import cc.bodyplus.health.mvp.module.ReportInfo2
import cc.bodyplus.health.mvp.view.BaseFragment
import cc.bodyplus.health.utils.Action
import cc.bodyplus.health.utils.Config
import kotlinx.android.synthetic.main.fragment_report_details2.*

/**
 * Created by rui.gao on 2018-05-02.
 */
class ReportDetailsFragment2 : BaseFragment(){

    private var mTitle: String? = null
    private var webUrl: String?= null

    companion object {
        fun getInstance(title: String, url: String): ReportDetailsFragment2 {
            val fragment = ReportDetailsFragment2()
            fragment.mTitle = title
            fragment.webUrl = url
            return fragment
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_report_details2
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun initView() {

        App.instance.regeditAction(action)
        webview.settings.javaScriptEnabled = true
        webview.settings.useWideViewPort = true
        webview.settings.setSupportZoom(true)
        webview.settings.builtInZoomControls = true
        webview.settings.cacheMode = WebSettings.LOAD_NO_CACHE
        webview.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        webview.settings.loadWithOverviewMode = true
        webview.webViewClient = object : WebViewClient() {}
        webview.webChromeClient = object :WebChromeClient() {}
        /**
         * webView与ViewPager所带来的滑动冲突问题解决方法
         */
        webview.setOnTouchListener { v, event ->
            webview.parent.requestDisallowInterceptTouchEvent(true)
            val x = event.rawX.toInt()
            val y = event.rawY.toInt()
            var lastX = 0
            var lastY = 0
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    webview.performClick()
                    lastX = x
                    lastY = y
                }
                MotionEvent.ACTION_MOVE -> {
                    val deltaY = y - lastY
                    val deltaX = x - lastX
                    if (Math.abs(deltaX) < Math.abs(deltaY)) {
                        webview.parent.requestDisallowInterceptTouchEvent(false)
                    } else {
                        webview.parent.requestDisallowInterceptTouchEvent(true)
                    }
                }
                else -> {
                }
            }
            false
        }
//        webUrl?.let {
//            webview.loadUrl(it)
//        }
    }

    var action: Action = Action { code, dataItem ->
        if (code == Config.UPDATE_REPORT_DETAILS) {
            if (dataItem != null && dataItem is ReportInfo2) {
                dataItem.reportUrl?.let {
                    webview.loadUrl(it)
                }
            }
        }
        false
    }

    override fun onResume() {
        super.onResume()
        webview.onResume()
    }

    override fun onPause() {
        super.onPause()
        webview.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        App.instance.removeAction(action)
        webview?.apply {
            destroy()
        }
    }



    override fun lazyLoad() {

    }


}