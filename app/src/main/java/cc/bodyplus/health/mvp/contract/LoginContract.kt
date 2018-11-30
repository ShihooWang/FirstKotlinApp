package cc.bodyplus.health.mvp.contract

import cc.bodyplus.health.mvp.module.AvatarBean
import cc.bodyplus.health.mvp.module.SysCodeBean
import cc.bodyplus.health.mvp.module.UserBean
import cc.bodyplus.health.mvp.presenter.IPresenter
import cc.bodyplus.health.mvp.view.report.view.BaseView
import okhttp3.ResponseBody
import java.io.File

/**
 * Created by rui.gao on 2018-05-09.
 */
interface LoginContract {

    interface View : BaseView {
        /**
         * 登录返回数据
         */
        fun setLoginData(userBean: UserBean)
        fun setAutoLoginData(userBean: ResponseBody)
        fun setSysCodeData(sysCodeBean: SysCodeBean)
        fun setUserInfoData(userBean: UserBean)
        fun setUserInfoFileData(bean: AvatarBean)
    }

    interface Presenter : IPresenter<View> {
        /**
         * 登录请求数据
         */
        fun requestLoginData(params: Map<String, String>,type : Int)
        fun requestSmsCodeData(params: Map<String, String>)
        fun requestUserInfoFileData(params: Map<String, String>, file : File)
        fun requestUserInfoData(params: Map<String, String>)
    }

}