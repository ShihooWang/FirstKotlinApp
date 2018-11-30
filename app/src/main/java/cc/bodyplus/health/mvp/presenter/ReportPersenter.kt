package cc.bodyplus.health.mvp.presenter

import cc.bodyplus.health.mvp.contract.ReportContract
import cc.bodyplus.health.mvp.module.ReportInfo
import cc.bodyplus.health.mvp.module.ReportInfo2
import okhttp3.ResponseBody

/**
 * Created by rui.gao on 2018-05-17.
 */
class ReportPersenter :BasePresenter<ReportContract.View>(), ReportContract.Presenter{

    private val reportModel by lazy { ReportPersenterImpl() }

    override fun requestReportListData(accountInfo : Map<String, String>) {

        checkViewAttached()
        mRootView?.apply {
            showLoading()
        }
        addSubscription(disposable = reportModel.doReportListData(accountInfo)
                .subscribe({bean: ResponseBody? ->
                    mRootView?.apply {
                        dismissLoading()
                        setReportListData(bean!!)
                    }},{ throwable: Throwable? ->
                    mRootView?.apply {
                        //处理异常
                        showErrorMsg(throwable.toString())
                    }
                })
        )
    }

    override fun requestReportDetailsData(accountInfo : Map<String, String>) {

        checkViewAttached()
        mRootView?.apply {
            showLoading()
        }
        addSubscription(disposable = reportModel.doReportDetailsData(accountInfo)
                .subscribe({bean: ReportInfo2? ->
                    mRootView?.apply {
                        dismissLoading()
                        setReportDetailsData(bean!!)
                    }},{ throwable: Throwable? ->
                    mRootView?.apply {
                        //处理异常
                        showErrorMsg(throwable.toString())
                    }
                })
        )
    }
}