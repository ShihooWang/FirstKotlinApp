package cc.bodyplus.health.mvp.contract

import cc.bodyplus.health.mvp.module.ReportInfo
import cc.bodyplus.health.mvp.module.ReportInfo2
import cc.bodyplus.health.mvp.presenter.IPresenter
import cc.bodyplus.health.mvp.view.report.view.BaseView
import okhttp3.ResponseBody

/**
 * Created by rui.gao on 2018-05-17.
 */
interface ReportContract {

    interface View : BaseView {
        /**
         * 返回报告详情数据
         */
        fun setReportDetailsData(bean: ReportInfo2)
        fun setReportListData(bean: ResponseBody)

    }

    interface Presenter : IPresenter<View> {
        /**
         * 报告详情请求数据
         */
        fun requestReportDetailsData(params: Map<String, String>)
        fun requestReportListData(params: Map<String, String>)

    }

}