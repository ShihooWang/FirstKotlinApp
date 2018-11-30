package cc.bodyplus.health.mvp.view.login

import android.content.Intent
import android.text.TextUtils
import cc.bodyplus.health.R
import cc.bodyplus.health.ext.showToast
import cc.bodyplus.health.mvp.contract.LoginContract
import cc.bodyplus.health.mvp.module.AvatarBean
import cc.bodyplus.health.mvp.module.SysCodeBean
import cc.bodyplus.health.mvp.module.UserBean
import cc.bodyplus.health.mvp.presenter.LoginPresenter
import cc.bodyplus.health.mvp.view.BaseActivity
import kotlinx.android.synthetic.main.activity_enter_phone_code.*
import okhttp3.ResponseBody
import java.util.regex.Pattern

/**
 * Created by rui.gao on 2018-05-09.
 */
class EnterPhoneNumberActivity : BaseActivity(), LoginContract.View {
    override fun setAutoLoginData(userBean: ResponseBody) {

    }


    private var phone:String = ""

    private val mPresenter by lazy { LoginPresenter() }

    init {
        mPresenter.attachView(this)
    }
    override fun initInject() {
        activityComponent.inject(this)
    }

    override fun layoutId(): Int = R.layout.activity_enter_phone_code

    override fun initData() {
    }

    override fun initView() {
        tv_next.setOnClickListener {
            phone = et_enter_context.text.toString().trim({ it <= ' ' })
            if (checkPhoneAcount(phone)) {
                val intent = Intent(this, EnterSmsCodeActivity::class.java)
                intent.putExtra("phone", phone)
                startActivity(intent)
            }
        }
        title_back.setOnClickListener { finish() }
    }

    override fun showErrorMsg(errorMsg: String) {
        showToast(errorMsg)
    }

    override fun showLoading() {
    }

    override fun dismissLoading() {
    }

    override fun setLoginData(userBean: UserBean) {
    }

    override fun setUserInfoData(userBean: UserBean) {

    }

    override fun setUserInfoFileData(bean: AvatarBean) {

    }

    override fun setSysCodeData(sysCodeBean: SysCodeBean) {
        val intent = Intent(this, EnterSmsCodeActivity::class.java)
        intent.putExtra("phone", phone)
        startActivity(intent)
    }

    fun checkPhoneAcount(account: String): Boolean {
        if (TextUtils.isEmpty(account)) {
            showToast("手机号码不能为空")
            return false
        } else if (!isMobileNum(account)) {
            showToast("请输入有效的手机号")
            return false
        } else {
            return true
        }
    }

    /**
     * 验证手机号规则
     */
    fun isMobileNum(mobiles: String): Boolean {
        val p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(17[0-9])|(14[0-9])|(18[0-9]))\\d{8}$")
        val m = p.matcher(mobiles)
        return m.matches()
    }
}