package cc.bodyplus.health.mvp.presenter

import cc.bodyplus.health.App
import cc.bodyplus.health.mvp.module.ReportInfo
import cc.bodyplus.health.mvp.module.ReportInfo2
import cc.bodyplus.health.net.util.NetLoginConfig
import cc.bodyplus.health.net.util.RxjavaHelperUtil
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import okhttp3.ResponseBody

/**
 * Created by rui.gao on 2018-05-17.
 */
class ReportPersenterImpl {
    fun doReportListData(accountInfo: Map<String, String>): Observable<ResponseBody> {
        return  App.instance.appComponent.getDataApi().doReportListData(NetLoginConfig.REPORT_LIST_DATA, accountInfo).compose(RxjavaHelperUtil.schedulersTransformer() as ObservableTransformer<ResponseBody, ResponseBody>)
    }

    fun doReportDetailsData(accountInfo: Map<String, String>): Observable<ReportInfo2> {
        return  App.instance.appComponent.getDataApi().doReportDetailsData(NetLoginConfig.REPORT_DETAILS_DATA, accountInfo).compose(RxjavaHelperUtil.schedulersTransformer() as ObservableTransformer<ReportInfo2, ReportInfo2>)
    }
}