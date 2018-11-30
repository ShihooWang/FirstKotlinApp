package cc.bodyplus.health.mvp.view.login

import android.content.Intent
import android.net.Uri
import cc.bodyplus.health.App
import cc.bodyplus.health.R
import cc.bodyplus.health.ext.showToast
import cc.bodyplus.health.mvp.contract.VersionContract
import cc.bodyplus.health.mvp.module.AboutInfo
import cc.bodyplus.health.mvp.presenter.UpdateVersionPresenter
import cc.bodyplus.health.mvp.view.BaseActivity
import cc.bodyplus.health.net.download.DownLoadObserver
import cc.bodyplus.health.net.download.DownloadInfo
import cc.bodyplus.health.net.download.DownloadManager
import cc.bodyplus.health.utils.ApkHelper
import cc.bodyplus.health.utils.Config
import cc.bodyplus.health.widget.dialog.ProgressDialog
import cc.bodyplus.health.widget.dialog.VersionUpdateDialog
import kotlinx.android.synthetic.main.activity_about.*
import java.io.File

/**
 * Created by rui.gao on 2018-05-09.
 */
open class AboutActivity : BaseActivity() ,VersionContract.View{

    private var apkFile: File? = null

    private val progressDialog by lazy { ProgressDialog(this)}

    private val mPresenter by lazy { UpdateVersionPresenter() }

    init {
        mPresenter.attachView(this)
    }
    override fun initInject() {
        activityComponent.inject(this)
    }
    override fun layoutId(): Int = R.layout.activity_about

    override fun initData() {

    }

    override fun initView() {
        title_back.setOnClickListener { finish() }
        linear_version.setOnClickListener {
            var map  = mapOf("data" to "")
            mPresenter.requestVersionData(map)
        }
        tv_version_name.text= "V"+ App.instance.getAppVersion()
    }

    override fun showErrorMsg(errorMsg: String) {
        showToast(errorMsg)
    }

    override fun showLoading() {

    }

    override fun dismissLoading() {
    }

    override fun setVersionData(bean: AboutInfo) {
        if(bean!= null){
            if(Integer.parseInt( bean.verNumber) > ApkHelper.getVersionCode(this)){
                showAppUpdate(bean.verName, bean.updateLog, bean.packageUrl, bean.packageByte, bean.isUpgrade)
            }else{
                showToast("已经是最新版本！")
            }
        }

    }

    private fun showAppUpdate(version: String, content: String, packageUrl: String, packageByte: String, isGrade: String) {
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
                showDownloadDialog(packageUrl, packageByte, version)
            }
        })

        versionUpdateDialog.show()

    }

    private fun showDownloadDialog(url: String, packageByte: String, verName: String) {

        val path = Config.UPDATE_PATH_APP + "/"
        val Files = File(path)
        if (!Files.exists()) {// 目录存在返回false
            Files.mkdirs()// 创建一个目录
        }
        apkFile = File(path + verName + ".apk")

        if (apkFile!!.exists() && packageByte == apkFile!!.length().toString()) {
            installApk(apkFile!!)
            return
        }

        progressDialog.show()
        progressDialog.setCancelable(false)

        DownloadManager.getInstance()
                .download(url, path, verName + ".apk", object : DownLoadObserver() {
                    override fun onNext(value: DownloadInfo) {
                        super.onNext(value)
                        progressDialog.setMessage("正在下载..." + (value.progress  * 100 / value.total ) + "%")
                    }

                    override fun onComplete() {
                        progressDialog.dismiss()
                        if (downloadInfo != null) {
                            showToast("下载成功")
                            installApk(apkFile!!)
                        }
                    }

                   override fun onError(e: Throwable) {
                        super.onError(e)
                        progressDialog.dismiss()
                        showToast(getString(R.string.me_Failed_download))
//                        apkFile?.let { it.delete() }
                    }
                })
    }


    // 安装apk
    protected fun installApk(apkFile: File) {
        if (!apkFile.exists()) {
            return
        }
        val i = Intent(Intent.ACTION_VIEW)
        i.setDataAndType(Uri.parse("file://" + apkFile.toString()), "application/vnd.android.package-archive")
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        this.startActivity(i)
    }

    override fun onDestroy() {
        mPresenter.detachView()
        super.onDestroy()
    }
}