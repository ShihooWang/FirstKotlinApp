package cc.bodyplus.health.mvp.view.main

import android.Manifest
import android.app.Fragment
import android.app.FragmentTransaction
import android.content.Intent

import cc.bodyplus.health.R
import android.support.v4.app.ActivityCompat
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v4.content.PermissionChecker.PERMISSION_GRANTED
import android.support.v7.app.ActionBarDrawerToggle
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import cc.bodyplus.health.App
import cc.bodyplus.health.ble.manager.BleService
import cc.bodyplus.health.ble.utils.BleSharedPrefHelperUtils
import cc.bodyplus.health.mvp.contract.LoginContract
import cc.bodyplus.health.mvp.module.AvatarBean
import cc.bodyplus.health.mvp.module.SysCodeBean
import cc.bodyplus.health.mvp.module.UserBean
import cc.bodyplus.health.mvp.presenter.LoginPresenter
import cc.bodyplus.health.ext.showToast
import cc.bodyplus.health.ext.tryCatch
import cc.bodyplus.health.mvp.contract.VersionContract
import cc.bodyplus.health.mvp.module.AboutInfo
import cc.bodyplus.health.mvp.presenter.UpdateVersionPresenter
import cc.bodyplus.health.mvp.view.BaseActivity
import cc.bodyplus.health.mvp.view.login.*
import cc.bodyplus.health.net.download.DownLoadObserver
import cc.bodyplus.health.net.download.DownloadInfo
import cc.bodyplus.health.net.download.DownloadManager
import cc.bodyplus.health.utils.*
import cc.bodyplus.health.widget.dialog.GlobalDialog.DialogClickListener
import cc.bodyplus.health.widget.dialog.GlobalDialog.showSelectDialog
import cc.bodyplus.health.widget.dialog.ProgressDialog
import cc.bodyplus.health.widget.dialog.VersionUpdateDialog
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.view_navigation.*
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.*


class MainActivity : BaseActivity(), View.OnClickListener, LoginContract.View, VersionContract.View{

    private val progressDialog by lazy { ProgressDialog(this) }
    private val updatePresenter by lazy { UpdateVersionPresenter() }
    private val mPresenter by lazy { LoginPresenter() }
    private var apkFile: File? = null
    init {
        mPresenter.attachView(this)
        updatePresenter.attachView(this)
    }

    override fun layoutId(): Int = R.layout.activity_main

    override fun initInject() {
        activityComponent.inject(this)
    }

    override fun initData() {

    }

    override fun initView() {
//        main_toolbar.title = ""
//        setSupportActionBar(main_toolbar)
//        main_toolbar.setNavigationIcon(R.drawable.ic_main_user)

        val actionBarDrawerToggle  = object : ActionBarDrawerToggle(this,main_dl_left,main_toolbar,R.string.main_open,R.string.main_closed){

            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                //侧滑栏打开
            }

            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)
                //侧滑栏关闭
            }
        }
        main_dl_left.addDrawerListener(actionBarDrawerToggle)
        image_user.setOnClickListener { main_dl_left.openDrawer(view_navigation) }

        ll_monitor.setOnClickListener(this)
        ll_report.setOnClickListener(this)
        view_about_me.setOnClickListener(this)
        view_profile.setOnClickListener(this)
        view_device.setOnClickListener(this)
        view_my_doctor.setOnClickListener(this)
        view_logout.setOnClickListener(this)
        setSelectFragment(0)
        requestAllPower()

        updateUserInfo()

        AutoUploadEcgRecord(SharedPrefHelperUtils.getInstance().userId,this).uploadDbRecord()

        if(SharedPrefHelperUtils.getInstance().autoLogin){
            var map  = mapOf("token" to App.instance.getAuthorization())
            mPresenter.requestLoginData(map,0)
        }

        var map  = mapOf("data" to "")
        updatePresenter.requestVersionData(map)

        App.instance.regeditAction(action)
    }



//    private var monitorFragment : MonitorFragment? = null
//    private var reportFragment : ReportFragment? = null
    private var selectId : Int = -1

    override fun onClick(v: View) {
        when(v.id){
            R.id.ll_monitor -> setSelectFragment(0)
            R.id.ll_report -> setSelectFragment(1)
            R.id.view_about_me -> startActivity(Intent(this,AboutActivity::class.java))
            R.id.view_profile -> OnToActivity()
            R.id.view_device -> BleSharedPrefHelperUtils.getInstance().coreInfo?.let {
                startActivity(Intent(this, DeviceInfoActivity::class.java))
            }?:let {
                this.showToast("请先连接设备！")
            }
            R.id.view_my_doctor -> startActivity(Intent(this, MyDoctorActivity::class.java))
            R.id.view_logout -> showAccoutLogoutDialog()
        }
    }

    private fun OnToActivity(){
        val isPerfect = SharedPrefHelperUtils.getInstance().isPerfect
        if(isPerfect == "1"){
            startActivity(Intent(this, ProfileActivity::class.java))
        }else{
            startActivity(Intent(this, EnterPhoneNumberActivity::class.java))
        }
    }

    private fun setSelectFragment(id : Int=0){
        if (selectId == id){
            return
        }
        selectId = id
        val fragmentTransaction = fragmentManager.beginTransaction()
        hideAllFragment(fragmentTransaction)
        when(id){
            0 -> {
                var monitorFragment = fragmentManager.findFragmentByTag("monitor")
                monitorFragment?.let {
                    fragmentTransaction.show(it)
                }?:let {
                    monitorFragment = MonitorFragment()
                    fragmentTransaction.add(R.id.fg_content,monitorFragment,"monitor")
                }
                tv_monitor.setTextColor(Color.rgb(49,212,209))
                ig_monitor.setImageResource(R.drawable.ic_main_monitor_select)
            }

            1 -> {
                var reportFragment = fragmentManager.findFragmentByTag("report")
                reportFragment?.let {
                    fragmentTransaction.show(it)
                }?:let {
                    reportFragment = ReportFragment()
                    fragmentTransaction.add(R.id.fg_content,reportFragment,"report")
                }
                tv_report.setTextColor(Color.rgb(49,212,209))
                ig_report.setImageResource(R.drawable.ic_main_report_select)
            }
        }
        fragmentTransaction.commitAllowingStateLoss()
    }

    private fun hideAllFragment(fragmentTransaction : FragmentTransaction){
        tv_monitor.setTextColor(Color.rgb(165,165,182))
        ig_monitor.setImageResource(R.drawable.ic_main_monitor)
        tv_report.setTextColor(Color.rgb(165,165,182))
        ig_report.setImageResource(R.drawable.ic_main_report)

        var reportFragment = fragmentManager.findFragmentByTag("report")
        var monitorFragment = fragmentManager.findFragmentByTag("monitor")
        monitorFragment?.let {
            fragmentTransaction.hide(it)
        }
        reportFragment?.let {
            fragmentTransaction.hide(it)
        }
    }


    override fun onAttachFragment(fragment: Fragment?) {

    }

    private fun showAccoutLogoutDialog() {
        showSelectDialog(this, "是否确定退出账号?", true, object : DialogClickListener{
            override fun confirm() {
//                SharedPrefHelperUtils.getInstance().clearUserProfile()
                SharedPrefHelperUtils.getInstance().clearAll()
//                val dao = HistoryDao.getInstance(this@MainActivity)
//                dao.deleteHistoryRecord()
//                dao.deleteAllMonitorRecord()
                App.instance.setAuthorization("")
                App.instance.execCallBack(Config.UPDATE_LOGIN_DATA,"")

                updateUserInfo()
            }

            override fun cancel() {
            }
        })
    }

    private fun requestAllPower() {
        val permissions = arrayOf( Manifest.permission.WRITE_EXTERNAL_STORAGE , Manifest.permission.ACCESS_FINE_LOCATION
                , Manifest.permission.ACCESS_COARSE_LOCATION , Manifest.permission.BLUETOOTH_ADMIN)
        val mPermissionList = ArrayList<String>()
        mPermissionList.clear()
        (0 until permissions.size)
                .filter { ContextCompat.checkSelfPermission(this,permissions[it])!= PackageManager.PERMISSION_GRANTED }
                .mapTo(mPermissionList) { permissions[it] }
        if (mPermissionList.isEmpty()) {//未授予的权限为空，表示都授予了

        } else {//请求权限方法
            val permissions = mPermissionList.toArray(arrayOfNulls<String>(mPermissionList.size))
            ActivityCompat.requestPermissions(this@MainActivity, permissions, 1)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            for (i in permissions.indices) {
                if (grantResults[i] == PERMISSION_GRANTED) {
                } else { //权限请求失败，退出
//                   finish()
                    exit()
                }
            }
        }
    }

    var action: Action = object : Action {
        override fun callBack(code: Int, `object`: Any): Boolean {
            if (code == Config.UPDATE_USER_INFO) {
                updateUserInfo()
            }
            return false
        }
    }

    fun updateUserInfo(){
        val userId = SharedPrefHelperUtils.getInstance().userId
        if ( userId != "-1" && userId != "" ) {
            view_logout.visibility = View.VISIBLE
            image_login.visibility = View.GONE
            tv_nick.text = SharedPrefHelperUtils.getInstance().nickname
            val mobile = SharedPrefHelperUtils.getInstance().mobile //154****3234
            if (!TextUtils.isEmpty(mobile)) {
                val mobileText = mobile.substring(0, 3) + "****" + mobile.substring(7, mobile.length)
                tv_mobile.text = mobileText
            }
            try {
                if(SharedPrefHelperUtils.getInstance().avatar!= null) {
                    Glide.with(this).load(SharedPrefHelperUtils.getInstance().avatar)
                            .asBitmap()
                            .centerCrop()
                            .placeholder(R.drawable.ic_avatar_female)
                            .into(civ_avatar)
                } else {
                    // 性别 1为男，2为女
                    if (SharedPrefHelperUtils.getInstance().gender == "1") {
                        civ_avatar.setImageDrawable(resources.getDrawable(R.drawable.ic_avatar_female))
                    } else {
                        civ_avatar.setImageDrawable(resources.getDrawable(R.drawable.ic_avatar_male))
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        } else {
            image_login.visibility = View.VISIBLE
            tv_nick.text = ("未登录")
            tv_mobile.text = ("点击登录")
            civ_avatar.setImageDrawable(resources.getDrawable(R.drawable.ic_avatar_female))
            view_logout.visibility = (View.GONE)
        }
    }

    override fun onDestroy() {
        App.instance.cleanAction()
        mPresenter.detachView()
        updatePresenter.detachView()
        super.onDestroy()
    }

    private var isQuit: Boolean = false
    private var timer : Timer ?= null
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!isQuit) {
                isQuit = true
                showToast("再按一次退出")
                timer = Timer()
                val task = object : TimerTask() {
                    override fun run() {
                        isQuit = false
                    }
                }
                timer?.schedule(task, 2000)
            } else {
                exit()
            }
        }
        return true
    }

    private fun exit(){
        stopService(Intent(this, BleService::class.java))
        finish()
        (application as App).exitJVM()
    }

    override fun showErrorMsg(errorMsg: String) {
    }

    override fun showLoading() {
    }

    override fun dismissLoading() {
    }

    override fun setLoginData(userBean: UserBean) {
        SharedPrefHelperUtils.getInstance().userId = userBean.patientId
        SharedPrefHelperUtils.getInstance().mobile = userBean.mobile
        SharedPrefHelperUtils.getInstance().isBeta = userBean.isBeta
        SharedPrefHelperUtils.getInstance().nickname = userBean.realname
        SharedPrefHelperUtils.getInstance().avatar = userBean.avatar
        SharedPrefHelperUtils.getInstance().gender = userBean.gender
        SharedPrefHelperUtils.getInstance().age = userBean.age
        SharedPrefHelperUtils.getInstance().isPerfect = userBean.isPerfect
        SharedPrefHelperUtils.getInstance().autoLogin = true  //设置已经登录过

        App.instance.execCallBack(Config.UPDATE_LOGIN_DATA,"")

        updateUserInfo()
    }

    override fun setAutoLoginData(value: ResponseBody) {
        tryCatch ({it.printStackTrace()}){
            val jsonObj = JSONObject(value.string())
            val status = jsonObj.optInt("status")
            if (status == 200) {
                val data = jsonObj.getJSONObject("data")
                SharedPrefHelperUtils.getInstance().userId = data.getString("patientId")
                SharedPrefHelperUtils.getInstance().mobile = data.getString("mobile")
                SharedPrefHelperUtils.getInstance().isBeta = data.getString("isBeta")
                SharedPrefHelperUtils.getInstance().nickname = data.getString("realname")
                SharedPrefHelperUtils.getInstance().avatar = data.getString("avatar")
                SharedPrefHelperUtils.getInstance().gender = data.getString("gender")
                SharedPrefHelperUtils.getInstance().age = data.getString("age")
                SharedPrefHelperUtils.getInstance().isPerfect = data.getString("isPerfect")
                SharedPrefHelperUtils.getInstance().autoLogin = true  //设置已经登录过
                App.instance.execCallBack(Config.UPDATE_LOGIN_DATA,"")
                updateUserInfo()
            } else if (status == 407) {
                SharedPrefHelperUtils.getInstance().clearAll()
                App.instance.setAuthorization("")
                App.instance.execCallBack(Config.UPDATE_LOGIN_DATA,"")
                updateUserInfo()
            }
        }
    }

    override fun setSysCodeData(sysCodeBean: SysCodeBean) {
    }

    override fun setUserInfoData(userBean: UserBean) {
    }

    override fun setUserInfoFileData(bean: AvatarBean) {
    }
    override fun setVersionData(bean: AboutInfo) {
        if(bean!= null){
            if(Integer.parseInt( bean.verNumber) > ApkHelper.getVersionCode(this)){
                showAppUpdate(bean.verName, bean.updateLog, bean.packageUrl, bean.packageByte, bean.isUpgrade)
            }
        }
    }

    private fun showAppUpdate(version: String, content: String, packageUrl: String, packageByte: String, isGrade: String) {
        val path = Config.UPDATE_PATH_APP + "/"
        val Files = File(path)
        if (!Files.exists()) {// 目录存在返回false
            Files.mkdirs()// 创建一个目录
        }
        apkFile = File(path + version + ".apk")
        if (apkFile!!.exists() && packageByte.equals(apkFile!!.length().toString() + "", ignoreCase = true)) {
            showUpdateApp(version, content, isGrade)
            return
        } else {
            if (!UIutils.isWifi(this)) {
                return
            }
            DownloadManager.getInstance().download(packageUrl, path, version + ".apk", object : DownLoadObserver() {
                override fun onNext(value: DownloadInfo) {
                    super.onNext(value)
                }
                override fun onComplete() {
                    if(apkFile!= null) {
                        showUpdateApp(version, content, isGrade)
                    }
                }
                override fun onError(e: Throwable) {
                    super.onError(e)
                }
            })
        }
    }

    private fun showUpdateApp(version: String, content: String, isGrade: String) {
        val versionUpdateDialog = VersionUpdateDialog(this)
        versionUpdateDialog.setTitleTxt(version)
        versionUpdateDialog.setDialogContent(content)
        versionUpdateDialog.setDialogClickListener(object : VersionUpdateDialog.OnUpdateDialogClickListener{
            override fun onCancelBtnClick() {
                versionUpdateDialog.dismiss()
                if (isGrade.equals("1", ignoreCase = true)) {
                    finish()
                }
            }

            override fun onConfirmBtnClick() {
                versionUpdateDialog.dismiss()
                installApk(apkFile!!)
            }
        })

        versionUpdateDialog.show()

    }

    // 安装apk
    private fun installApk(apkFile: File) {
        if (!apkFile.exists()) {
            return
        }
        val i = Intent(Intent.ACTION_VIEW)
        i.setDataAndType(Uri.parse("file://" + apkFile.toString()), "application/vnd.android.package-archive")
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        this.startActivity(i)
    }

}
