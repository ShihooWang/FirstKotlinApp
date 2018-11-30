package cc.bodyplus.health.mvp.presenter

import cc.bodyplus.health.mvp.contract.LoginContract
import cc.bodyplus.health.mvp.module.AvatarBean
import cc.bodyplus.health.mvp.module.SysCodeBean
import cc.bodyplus.health.mvp.module.UserBean
import okhttp3.ResponseBody
import java.io.File

/**
 * Created by rui.gao on 2018-05-09.
 */
class LoginPresenter :BasePresenter<LoginContract.View>(), LoginContract.Presenter{

    private val LoginModel by lazy { LoginInteractorImpl() }

    override fun requestLoginData(accountInfo : Map<String, String>,type: Int) {

        checkViewAttached()
        mRootView?.apply {
            showLoading()
        }
        when(type){
            0 ->{ //自动登录
                addSubscription(disposable = LoginModel.doLoginAuto(accountInfo)
                        .subscribe({userBean: ResponseBody? ->
                            mRootView?.apply {
                                dismissLoading()
                                setAutoLoginData(userBean!!)
                            }},{ throwable: Throwable? ->
                            mRootView?.apply {
                                //处理异常
                                showErrorMsg(throwable.toString())
                            }
                        })
                )
            }
            1->{ //手动登录
                addSubscription(disposable = LoginModel.doDevice(accountInfo)
                        .subscribe({userBean: UserBean? ->
                            mRootView?.apply {
                                dismissLoading()
                                setLoginData(userBean!!)
                            }},{ throwable: Throwable? ->
                            mRootView?.apply {
                                //处理异常
                                showErrorMsg(throwable.toString())
                            }
                        })
                )
            }
        }


    }

    override fun requestSmsCodeData(accountInfo : Map<String, String> ) {

        checkViewAttached()
        mRootView?.apply {
            showLoading()
        }
        addSubscription(disposable = LoginModel.doSysCode(accountInfo)
                .subscribe({bean: SysCodeBean? ->
                    mRootView?.apply {
                        setSysCodeData(bean!!)
                        dismissLoading()
                    }},{ throwable: Throwable? ->
                    mRootView?.apply {
                        //处理异常
                        showErrorMsg(throwable.toString())
                    }
                })
        )
    }

    override fun requestUserInfoData(accountInfo : Map<String, String> ) {

        checkViewAttached()
        mRootView?.apply {
            showLoading()
        }
        addSubscription(disposable = LoginModel.doUserProfile(accountInfo)
                .subscribe({bean: UserBean? ->
                    mRootView?.apply {
                        setUserInfoData(bean!!)
                        dismissLoading()
                    }},{ throwable: Throwable? ->
                    mRootView?.apply {
                        //处理异常
                        showErrorMsg(throwable.toString())
                    }
                })
        )
    }

    override fun requestUserInfoFileData(accountInfo : Map<String, String> ,file : File) {

        checkViewAttached()
        mRootView?.apply {
            showLoading()
        }
        addSubscription(disposable = LoginModel.doUserFileInfo(accountInfo,file)
                .subscribe({bean: AvatarBean? ->
                    mRootView?.apply {
                        setUserInfoFileData(bean!!)
                        dismissLoading()
                    }},{ throwable: Throwable? ->
                    mRootView?.apply {
                        //处理异常
                        showErrorMsg(throwable.toString())
                    }
                })
        )
    }

}