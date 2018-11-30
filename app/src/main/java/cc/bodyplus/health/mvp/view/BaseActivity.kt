package cc.bodyplus.health.mvp.view

import android.content.pm.ActivityInfo
import android.os.*
import android.support.v7.app.AppCompatActivity
import cc.bodyplus.health.App
import cc.bodyplus.health.di.component.ActivityComponent
import cc.bodyplus.health.di.component.DaggerActivityComponent
import cc.bodyplus.health.di.module.ActivityModule
import cc.bodyplus.health.ext.getAppComponent

/**
 * Created by shihoo.wang on 2018/4/19.
 * Email shihu.wang@bodyplus.cc
 */
abstract class BaseActivity : AppCompatActivity() {

//    protected val mHandler : Handler by lazy {
//        Handler()
//    }
    protected var isNeedLandSpace = false

    protected val activityComponent: ActivityComponent by lazy {
        DaggerActivityComponent.builder()
                .appComponent(getAppComponent())
                .activityModule(ActivityModule(this))
                .build()
    }

    override fun onResume() {
        super.onResume()
        (application as App).setCurrentActivity(this)
    }

    abstract fun initInject()

    override fun onCreate(savedInstanceState: Bundle?) {
//        if (savedInstanceState != null){
//            finish()
//        }
        super.onCreate(savedInstanceState)
        if (isNeedLandSpace) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }else{
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
        initInject()
        setContentView(layoutId())
        initView()
        initData()
    }

//    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
//        super.onSaveInstanceState(outState, outPersistentState)
//    }
//
//    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
//        super.onRestoreInstanceState(savedInstanceState)
//    }

    /**
     *  加载布局
     */
    abstract fun layoutId(): Int

    /**
     * 初始化数据
     */
    abstract fun initData()

    /**
     * 初始化 View
     */
    abstract fun initView()

    fun removeCallbacks(r: Runnable) {
        mHandler.removeCallbacks(r)
    }

    // 创建一个Handler
    protected val mHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            onHandle(msg!!)
        }
    }

    open fun onHandle(msg: Message){

    }

    fun postDelayed(r: Runnable, delayMillis: Long) {
        if (mHandler != null) {
            mHandler.postDelayed({
                if (!isFinishing) {
                    r.run()
                }
            }, delayMillis)
        }
    }
}