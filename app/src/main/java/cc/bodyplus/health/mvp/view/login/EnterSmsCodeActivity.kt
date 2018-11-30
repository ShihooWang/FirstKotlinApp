package cc.bodyplus.health.mvp.view.login

import android.content.Intent
import android.os.Message
import android.text.TextUtils
import android.view.View
import cc.bodyplus.health.App
import cc.bodyplus.health.R
import cc.bodyplus.health.ext.showToast
import cc.bodyplus.health.mvp.contract.LoginContract
import cc.bodyplus.health.mvp.module.AvatarBean
import cc.bodyplus.health.mvp.module.SysCodeBean
import cc.bodyplus.health.mvp.module.UserBean
import cc.bodyplus.health.mvp.presenter.LoginPresenter
import cc.bodyplus.health.mvp.view.BaseActivity
import cc.bodyplus.health.mvp.view.main.MainActivity
import cc.bodyplus.health.net.util.LoginUtil
import cc.bodyplus.health.utils.Config
import cc.bodyplus.health.utils.SharedPrefHelperUtils
import kotlinx.android.synthetic.main.activity_enter_sms_code.*
import okhttp3.ResponseBody

/**
 * Created by rui.gao on 2018-05-09.
 */
class EnterSmsCodeActivity : BaseActivity(), LoginContract.View{
    override fun setAutoLoginData(userBean: ResponseBody) {

    }

    private var timeCount = 59// 重发倒计时
    private val TimeCount_ID = 105
    private val mPresenter by lazy { LoginPresenter() }

    private var text_phone : String ?= ""
    private var text_code : String ?= ""

    init {
        mPresenter.attachView(this)
    }
    override fun initInject() {
        activityComponent.inject(this)
    }

    override fun layoutId(): Int = R.layout.activity_enter_sms_code

    override fun initData() {
        postDelayed(Runnable {
            val encryptNum = LoginUtil.encryptString(text_phone)
            var map  = mapOf("smsType" to "1","smsPhone" to encryptNum)
            mPresenter.requestSmsCodeData(map)
        },300)
    }

    override fun initView() {
        text_phone =  intent.getStringExtra("phone")
        text_code = et_enter_context.text.toString()

        tv_retrieve_code.visibility = (View.VISIBLE)

        tv_next.setOnClickListener {
            text_code = et_enter_context.text.toString()
            if (TextUtils.isEmpty(text_code)) run { showToast("请输入验证码") } else {
               var map  = mapOf("username" to LoginUtil.encryptString(text_phone)!!,"smsCode" to text_code!!)
                mPresenter.requestLoginData(map,1)
            }
        }
        title_back.setOnClickListener { finish() }
        tv_retrieve_code.setOnClickListener {
            val encryptNum = LoginUtil.encryptString(text_phone)
            var map  = mapOf("smsType" to "1","smsPhone" to encryptNum)
            mPresenter.requestSmsCodeData(map)
        }
    }

    override fun showErrorMsg(errorMsg: String) {
        tv_retrieve_code.isEnabled = true
        showToast(errorMsg)
    }

    override fun showLoading() {
        multipleStatusView?.showLoading()
    }

    override fun dismissLoading() {
        multipleStatusView?.showContent()
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
        SharedPrefHelperUtils.getInstance().autoLogin = true

//        startActivity(Intent(this,EnterNameActivity::class.java))


        if(userBean.isPerfect == "0"){
            var intent = Intent()
            intent.setClass(this,EnterNameActivity::class.java)
            startActivity(intent)
        }else{
            var intent = Intent()
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            intent.setClass(this,MainActivity::class.java)
            startActivity(intent)
            App.instance.execCallBack(Config.UPDATE_USER_INFO,"")
            App.instance.execCallBack(Config.UPDATE_LOGIN_DATA,"")

        }
    }

    override fun setUserInfoData(userBean: UserBean) {

    }

    override fun setUserInfoFileData(bean: AvatarBean) {

    }

    override fun setSysCodeData(sysCodeBean: SysCodeBean) {
        runOnUiThread { disableResendBtn() }
    }

    private fun disableResendBtn() {
        tv_retrieve_code.text = ("")
        tv_retrieve_code.setTextColor(resources.getColor(R.color.gray))
        mHandler?.sendEmptyMessageDelayed(TimeCount_ID, 1000)
    }

    private fun normalResendBtn() {
        timeCount = 59
        tv_retrieve_code.text =("重新获取")
        tv_retrieve_code.isEnabled = (true)
        tv_retrieve_code.setTextColor(resources.getColor(R.color.color_text_5))
    }

    override fun onHandle(msg: Message) {
        when(msg.what){
            TimeCount_ID ->{
                tv_retrieve_code.text =(timeCount.toString() + "秒后重试")
                if (timeCount > 0) {
                    timeCount--
                    mHandler?.sendEmptyMessageDelayed(TimeCount_ID, 1000)
                } else {
                    normalResendBtn()
                }
            }
        }
    }


    override fun onDestroy() {
        mPresenter!!.detachView()
        super.onDestroy()
    }
}