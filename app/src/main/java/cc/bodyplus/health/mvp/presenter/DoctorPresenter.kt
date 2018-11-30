package cc.bodyplus.health.mvp.presenter

import cc.bodyplus.health.mvp.contract.DoctorContract
import cc.bodyplus.health.mvp.module.DoctorBean

/**
 * Created by rui.gao on 2018-05-17.
 */
class DoctorPresenter :BasePresenter<DoctorContract.View>(), DoctorContract.Presenter{

    private val doctorModel by lazy { DoctorPresenterImpl() }

    override fun requestDoctorData(accountInfo : Map<String, String>) {

        checkViewAttached()
        mRootView?.apply {
            showLoading()
        }
        addSubscription(disposable = doctorModel.doDoctor(accountInfo)
                .subscribe({bean: ArrayList<DoctorBean>? ->
                    mRootView?.apply {
                        dismissLoading()
                        setDoctorData(bean!!)
                    }},{ throwable: Throwable? ->
                    mRootView?.apply {
                        //处理异常
                        showErrorMsg(throwable.toString())
                    }
                })
        )


    }
}