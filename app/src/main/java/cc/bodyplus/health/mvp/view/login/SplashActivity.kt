package cc.bodyplus.health.mvp.view.login

import android.content.Intent
import cc.bodyplus.health.R
import cc.bodyplus.health.mvp.contract.LoginContract
import cc.bodyplus.health.mvp.module.AvatarBean
import cc.bodyplus.health.mvp.module.SysCodeBean
import cc.bodyplus.health.mvp.module.UserBean
import cc.bodyplus.health.mvp.presenter.LoginPresenter
import cc.bodyplus.health.mvp.view.BaseActivity
import cc.bodyplus.health.mvp.view.main.MainActivity
import cc.bodyplus.health.utils.SharedPrefHelperUtils
import okhttp3.ResponseBody

/**
 * Created by shihoo.wang on 2018/5/2.
 * Email shihu.wang@bodyplus.cc
 */

class SplashActivity : BaseActivity(), LoginContract.View{

    override fun setAutoLoginData(userBean: ResponseBody) {

    }


    private val mPresenter by lazy { LoginPresenter() }

    init {
        mPresenter.attachView(this)
    }
    override fun initInject() {
        activityComponent.inject(this)
    }

    override fun layoutId(): Int {
        return R.layout.activity_splash
    }

    override fun initData() {

    }

    override fun initView() {
        mHandler.postDelayed(runnable,2000)
    }


    private val runnable : Runnable by lazy {
        Runnable {
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
//            startActivity(Intent(this@SplashActivity, MonitorEcgTagActivity::class.java))
            finish()
        }
    }

    override fun showErrorMsg(errorMsg: String) {
        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
    }

    override fun showLoading() {
    }

    override fun dismissLoading() {
    }

    override fun setLoginData(userBean: UserBean) {
        if(userBean == null){
            return
        }
        SharedPrefHelperUtils.getInstance().userId = userBean.patientId
        SharedPrefHelperUtils.getInstance().mobile = userBean.mobile
        SharedPrefHelperUtils.getInstance().isBeta = userBean.isBeta
        SharedPrefHelperUtils.getInstance().nickname = userBean.realname
        SharedPrefHelperUtils.getInstance().avatar = userBean.avatar
        SharedPrefHelperUtils.getInstance().gender = userBean.gender
        SharedPrefHelperUtils.getInstance().age = userBean.age
        SharedPrefHelperUtils.getInstance().isPerfect = userBean.isPerfect
        SharedPrefHelperUtils.getInstance().autoLogin = true  //设置已经登录过

        if(userBean.isPerfect == "0"){
            startActivity(Intent(this,EnterNameActivity::class.java))
        }else{
            startActivity(Intent(this,MainActivity::class.java))
        }
        finish()
    }

    override fun setSysCodeData(sysCodeBean: SysCodeBean) {
    }

    override fun setUserInfoData(userBean: UserBean) {
    }

    override fun setUserInfoFileData(bean: AvatarBean) {
    }

    override fun onDestroy() {
        mPresenter.detachView()
        super.onDestroy()
        removeCallbacks(runnable)
    }
}