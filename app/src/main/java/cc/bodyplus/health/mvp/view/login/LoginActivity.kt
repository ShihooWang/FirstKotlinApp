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
import kotlinx.android.synthetic.main.activity_enter_name.*
import okhttp3.ResponseBody

/**
 * Created by shihoo.wang on 2018/4/18.
 * Email shihu.wang@bodyplus.cc
 */
class LoginActivity : BaseActivity(), LoginContract.View {
    override fun setAutoLoginData(userBean: ResponseBody) {

    }

    override fun setSysCodeData(sysCodeBean: SysCodeBean) {

    }

    private var text_name : String ?= ""
    private val mPresenter by lazy { LoginPresenter() }

    init {
        mPresenter.attachView(this)
    }

    override fun initInject() {
        activityComponent.inject(this)
    }

    override fun layoutId(): Int = R.layout.activity_enter_name



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
    override fun initData() {

//        mPresenter.requestLoginData()
    }

    override fun initView() {
        et_enter_name.setOnClickListener {
            text_name =  et_enter_name.text.toString()
            if (TextUtils.isEmpty(text_name)) run { showToast("请输入姓名") } else {
                val intent = Intent(this, RegisterAgeActivity::class.java)
                intent.putExtra("nickName", text_name)
                startActivity(intent)
            }
        }
    }

    override fun showErrorMsg(errorMsg: String) {

    }


    override fun onDestroy() {
        mPresenter!!.detachView()
        super.onDestroy()
    }
}