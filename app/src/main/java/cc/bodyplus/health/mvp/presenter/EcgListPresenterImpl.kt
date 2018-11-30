package cc.bodyplus.health.mvp.presenter

import cc.bodyplus.health.App
import cc.bodyplus.health.mvp.module.EcgDataBean
import cc.bodyplus.health.mvp.module.EcgRecordList
import cc.bodyplus.health.mvp.module.RecordEcgListBean
import cc.bodyplus.health.net.util.NetLoginConfig
import cc.bodyplus.health.net.util.RxjavaHelperUtil
import io.reactivex.Observable
import io.reactivex.ObservableTransformer

/**
 * Created by rui.gao on 2018-05-18.
 */
class EcgListPresenterImpl {
    fun doEcgData(accountInfo: Map<String, String>): Observable<EcgDataBean> {
        return  App.instance.appComponent.getDataApi().doEcgData(NetLoginConfig.GET_ECG_DATA, accountInfo).compose(RxjavaHelperUtil.schedulersTransformer() as ObservableTransformer<EcgDataBean, EcgDataBean>)
    }

    fun doRecordList(param: Map<String, String>) : Observable<EcgRecordList> {
        return  App.instance.appComponent.getDataApi().doRecordList(NetLoginConfig.GET_ECG_RECORD_LIST, param).compose(RxjavaHelperUtil.schedulersTransformer() as ObservableTransformer<EcgRecordList, EcgRecordList>)
    }
}