package cc.bodyplus.health.mvp.view.login

import android.content.Intent
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
import cc.bodyplus.health.utils.Config
import cc.bodyplus.health.utils.SharedPrefHelperUtils
import kotlinx.android.synthetic.main.activity_register_gender.*
import okhttp3.ResponseBody

/**
 * Created by rui.gao on 2018-05-09.
 */
class RegisterGenderActivity :BaseActivity() , LoginContract.View {
    override fun setAutoLoginData(userBean: ResponseBody) {

    }

    private var age: String = ""
    private var nickName: String = ""
    private var gender: String = ""
    private var femaleChecked = true

    private val mPresenter by lazy { LoginPresenter() }

    init {
        mPresenter.attachView(this)
    }
    override fun initInject() {
        activityComponent.inject(this)
    }

    override fun layoutId(): Int = R.layout.activity_register_gender

    override fun initView() {
        nickName = intent.getStringExtra("nickName")
        age = intent.getStringExtra("age")

        view_female.setOnClickListener {
            femaleChecked = !femaleChecked
            if (femaleChecked) {
                iv_female.setImageDrawable(resources.getDrawable(R.drawable.ic_gender_pressed))
                iv_male.setImageDrawable(resources.getDrawable(R.drawable.ic_gender_normal))
            } else {
                iv_female.setImageDrawable(resources.getDrawable(R.drawable.ic_gender_normal))
                iv_male.setImageDrawable(resources.getDrawable(R.drawable.ic_gender_pressed))
            }
        }
        view_male.setOnClickListener {
            femaleChecked = !femaleChecked
            if (femaleChecked) {
                iv_female.setImageDrawable(resources.getDrawable(R.drawable.ic_gender_pressed))
                iv_male.setImageDrawable(resources.getDrawable(R.drawable.ic_gender_normal))
            } else {
                iv_female.setImageDrawable(resources.getDrawable(R.drawable.ic_gender_normal))
                iv_male.setImageDrawable(resources.getDrawable(R.drawable.ic_gender_pressed))
            }
        }

        title_back.setOnClickListener { finish() }

        tv_next.setOnClickListener{
            gender = if (femaleChecked) {  //1为男，2为女
                "1"
            } else {
                "2"
            }
            var map  = mapOf("realname" to nickName!!,"gender" to gender!!,"age" to age!!)
            mPresenter.requestUserInfoData(map)
        }
    }

    override fun initData() {

    }

    override fun showErrorMsg(errorMsg: String) {
        showToast(errorMsg)
    }

    override fun showLoading() {
    }

    override fun dismissLoading() {
    }

    override fun setSysCodeData(sysCodeBean: SysCodeBean) {
    }

    override fun setUserInfoFileData(bean: AvatarBean) {

    }

    override fun setLoginData(userBean: UserBean) {
    }

    override fun setUserInfoData(userBean: UserBean) {
        if(userBean == null){
            return
        }
        SharedPrefHelperUtils.getInstance().nickname = userBean.realname
        SharedPrefHelperUtils.getInstance().avatar = userBean.avatar
        SharedPrefHelperUtils.getInstance().gender = userBean.gender
        SharedPrefHelperUtils.getInstance().age = userBean.age
        SharedPrefHelperUtils.getInstance().isPerfect = userBean.isPerfect
        var intent = Intent()
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        intent.setClass(this,MainActivity::class.java)
        startActivity(intent)
        finish()
        App.instance.execCallBack(Config.UPDATE_USER_INFO,"")
        App.instance.execCallBack(Config.UPDATE_LOGIN_DATA,"")

    }

    override fun onDestroy() {
        mPresenter.detachView()
        super.onDestroy()
    }

}