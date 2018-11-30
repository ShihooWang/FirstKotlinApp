package cc.bodyplus.health

import android.app.Application
import android.content.Context
import android.view.WindowManager
import cc.bodyplus.health.ble.manager.BleConnectionManger
import cc.bodyplus.health.di.component.AppComponent
import cc.bodyplus.health.di.component.DaggerAppComponent
import cc.bodyplus.health.di.module.ApiModule
import cc.bodyplus.health.di.module.AppModule
import cc.bodyplus.health.utils.Action
import cc.bodyplus.health.utils.ApkHelper
import cc.bodyplus.health.utils.SharedPrefHelperUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.MemoryCategory
import com.bumptech.glide.integration.okhttp.OkHttpUrlLoader
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.request.target.ViewTarget
import java.io.InputStream
import cc.bodyplus.health.mvp.view.BaseActivity
import java.util.*
import android.os.StrictMode
import android.app.Activity
import cc.bodyplus.health.utils.Config
import cc.bodyplus.health.utils.mail.AppException
import cc.bodyplus.health.utils.mail.SendEmailUtils
import java.io.File
import android.support.multidex.MultiDex
import com.mob.MobSDK


/**
 * Created by shihoo.wang on 2018/4/18.
 * Email shihu.wang@bodyplus.cc
 */
class App : Application() {

    private val mActionCtrl = Vector<Action>()
    private var destoryMap = HashMap<String,Activity>()
    public var PHONE_WIDTH: Int = 0
    public var PHONE_HEIGHT: Int = 0
    var lastAuthorizationTime: Long = 0
    // 程序公有的 AppComponent对象
    val appComponent : AppComponent by lazy {
        DaggerAppComponent.builder()
                .apiModule(ApiModule())
                .appModule(AppModule(this))
                .build()
    }

    var activity : BaseActivity ?= null

    companion object {
        lateinit var instance : App

    }

    override fun onCreate() {
        super.onCreate()
        MultiDex.install(this)
        instance = this
        BleConnectionManger.mInstance.init(instance)

        val wm = applicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        PHONE_WIDTH = wm.defaultDisplay.width
        PHONE_HEIGHT = wm.defaultDisplay.height

        Glide.get(this).register(GlideUrl::class.java, InputStream::class.java, OkHttpUrlLoader.Factory(com.squareup.okhttp.OkHttpClient()))
        ViewTarget.setTagId(R.id.glide_request)
        Glide.get(this).setMemoryCategory(MemoryCategory.HIGH)

        // android 7.0系统解决拍照的问题
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        builder.detectFileUriExposure()

        sendEmail()
        MobSDK.init(this)
    }

    private fun sendEmail() {
        // 发送错误信息到邮箱
        AppException.startTask()//保存crash日志在本地
        val mFile = File(Config.LOG_PATH + AppException.LOG_NAME)// 发送错误日志到服务器
        if (mFile.exists()) {
            SendEmailUtils.getInstance().sendEmail(Config.LOG_PATH + AppException.LOG_NAME)
        }
    }

    fun setCurrentActivity(activity: BaseActivity) {
        this.activity = activity
    }

    fun getCurrentActivity(): BaseActivity {
        return activity!!
    }

    fun exitJVM() {
        BleConnectionManger.mInstance.clearAll()
        val task = object : TimerTask() {
            override fun run() {
                System.exit(0)
            }
        }
        Timer().schedule(task, 1000)
    }

    // 请求网络的令牌
    fun getAuthorization(): String {
        return SharedPrefHelperUtils.getInstance().netWorkToken
    }

    @Synchronized
    fun setAuthorization(authorization: String?) {
        if (authorization == null || authorization == "") {
            return
        }
        SharedPrefHelperUtils.getInstance().setNetWorkToken(authorization)
    }

    private var appVersion: String = "1.0.0"
    fun getAppVersion(): String {
        appVersion = ApkHelper.getVersion(this)
        return appVersion
    }


    private var tzID: String = "Africa/Abidjan"
    fun getTimeZone(): String {
        if (null == tzID) {
            val tz = TimeZone.getDefault()
            tzID = tz.id
        }
        return tzID
    }

    private var languageType: String = "CN"
    fun getLanguageType(): String {
        if (null == languageType) {
            val curLocale = Locale.getDefault()
            if (curLocale.language.contains("zh") || curLocale.language.contains("ZH")) {
                languageType = "CN"
            } else {
                languageType = "EN"
            }
        }
        return languageType
    }

    // 注册回调
    fun regeditAction(action: Action) {
        mActionCtrl.add(action)
    }

    // 移除回调
    fun removeAction(key: Action) {
        if (mActionCtrl.contains(key)) {
            mActionCtrl.remove(key)
        }
    }

    // 清空回调
    fun cleanAction() {
        mActionCtrl.clear()
    }

    // 广播回调
    @Synchronized
    fun execCallBack(code: Int, data: Any) {
        val vector = mActionCtrl // 防止遍历过程中集合的大小被改变 ConcurrentModificationException
        for (ac in vector) {
            ac.callBack(code,data)
        }
    }

    /**
     * 添加到销毁队列
     *
     * @param activity 要销毁的activity
     */

    fun addLoginActivity(activity: Activity, activityName: String) {
        destoryMap.put(activityName, activity)
    }

    /**
     * 销毁指定Activity
     */
    fun destoryLoginActivity(activityName: String) {
        val keySet = destoryMap!!.keys
        for (key in keySet) {
            destoryMap[key]?.finish()
        }
    }
}