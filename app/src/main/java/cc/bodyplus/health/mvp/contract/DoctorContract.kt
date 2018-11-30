package cc.bodyplus.health.mvp.contract

import cc.bodyplus.health.mvp.module.DoctorBean
import cc.bodyplus.health.mvp.presenter.IPresenter
import cc.bodyplus.health.mvp.view.report.view.BaseView

/**
 * Created by rui.gao on 2018-05-17.
 */
interface DoctorContract {

    interface View : BaseView {
        /**
         * 医生列表返回数据
         */
        fun setDoctorData(bean: ArrayList<DoctorBean>)
    }

    interface Presenter : IPresenter<View> {
        /**
         * 登录请求数据
         */
        fun requestDoctorData(params: Map<String, String>)
    }

}