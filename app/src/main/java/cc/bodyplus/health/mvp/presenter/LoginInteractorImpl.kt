package cc.bodyplus.health.mvp.presenter

import cc.bodyplus.health.App
import cc.bodyplus.health.mvp.module.AvatarBean
import cc.bodyplus.health.mvp.module.SysCodeBean
import cc.bodyplus.health.mvp.module.UserBean
import cc.bodyplus.health.net.upload.UpLoadUtil
import cc.bodyplus.health.net.util.NetLoginConfig
import cc.bodyplus.health.net.util.RxjavaHelperUtil
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import okhttp3.ResponseBody
import java.io.File


/**
 * Created by rui.gao on 2018-05-10.
 */


class LoginInteractorImpl{

    fun doDevice(accountInfo: Map<String, String>): Observable<UserBean> {
        return  App.instance.appComponent.getLoginApi().login(NetLoginConfig.USER_LOGIN_URL, accountInfo).compose(RxjavaHelperUtil.schedulersTransformer() as ObservableTransformer<UserBean, UserBean>)
    }

    fun doLoginAuto(accountInfo: Map<String, String>): Observable<ResponseBody> {
        return  App.instance.appComponent.getLoginApi().autoLogin(NetLoginConfig.USER_LOGIN_AUTO, accountInfo).compose(RxjavaHelperUtil.schedulersTransformer() as ObservableTransformer<ResponseBody, ResponseBody>)
    }

    fun doSysCode(accountInfo: Map<String, String>): Observable<SysCodeBean> {
        return  App.instance.appComponent.getLoginApi().syscode(NetLoginConfig.SMS_CODE_URL, accountInfo).compose(RxjavaHelperUtil.schedulersTransformer() as ObservableTransformer<SysCodeBean, SysCodeBean>)
    }

    fun doUserProfile(accountInfo: Map<String, String>): Observable<UserBean> {
        return  App.instance.appComponent.getLoginApi().login(NetLoginConfig.USER_PROFILE, accountInfo).compose(RxjavaHelperUtil.schedulersTransformer() as ObservableTransformer<UserBean, UserBean>)
    }

    fun doUserFileInfo(accountInfo: Map<String, String>, file : File): Observable<AvatarBean> {
        return  App.instance.appComponent.getLoginApi().uploadUserFileInfo(NetLoginConfig.USER_UPDATE_ACATAR, accountInfo, UpLoadUtil.filePart(file)).compose(RxjavaHelperUtil.schedulersTransformer() as ObservableTransformer<AvatarBean, AvatarBean>)
    }
}