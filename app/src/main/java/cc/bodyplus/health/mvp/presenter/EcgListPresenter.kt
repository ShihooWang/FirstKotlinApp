package cc.bodyplus.health.mvp.presenter

import cc.bodyplus.health.mvp.contract.EcgContract
import cc.bodyplus.health.mvp.module.EcgDataBean
import cc.bodyplus.health.mvp.module.EcgRecordList

/**
 * Created by rui.gao on 2018-05-18.
 */
class EcgListPresenter:BasePresenter<EcgContract.View>(), EcgContract.Presenter{


    private val ecgModel by lazy { EcgListPresenterImpl() }

    override fun requestEcgData(accountInfo : Map<String, String>) {

        checkViewAttached()
        mRootView?.apply {
            showLoading()
        }
        addSubscription(disposable = ecgModel.doEcgData(accountInfo)
                .subscribe({bean: EcgDataBean? ->
                    mRootView?.apply {
                        dismissLoading()
                        setEcgData(bean!!)
                    }},{ throwable: Throwable? ->
                    mRootView?.apply {
                        //处理异常
                        showErrorMsg(throwable.toString())
                    }
                })
        )

    }

    override fun requestEcgRecordList(params: Map<String, String>) {
        checkViewAttached()
        mRootView?.apply {
            showLoading()
        }
        addSubscription(disposable = ecgModel.doRecordList(params)
                .subscribe({bean: EcgRecordList? ->
                    mRootView?.apply {
                        dismissLoading()
                        setEcgRecordList(bean!!)
                    }},{ throwable: Throwable? ->
                    mRootView?.apply {
                        //处理异常
                        showErrorMsg(throwable.toString())
                    }
                })
        )
    }



}