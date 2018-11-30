package cc.bodyplus.health.mvp.contract

import cc.bodyplus.health.mvp.module.EcgDataBean
import cc.bodyplus.health.mvp.module.EcgRecordList
import cc.bodyplus.health.mvp.module.RecordEcgListBean
import cc.bodyplus.health.mvp.presenter.IPresenter
import cc.bodyplus.health.mvp.view.report.view.BaseView

/**
 * Created by rui.gao on 2018-05-18.
 */
interface  EcgContract {

    interface View : BaseView {
        /**
         * ECG返回数据
         */
        fun setEcgData(bean: EcgDataBean)

        fun setEcgRecordList(list : EcgRecordList)
    }

    interface Presenter : IPresenter<View> {
        /**
         * ECG请求数据
         */
        fun requestEcgData(params: Map<String, String>)

        fun requestEcgRecordList(params : Map<String, String>)
    }

}