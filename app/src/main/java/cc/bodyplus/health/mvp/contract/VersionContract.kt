package cc.bodyplus.health.mvp.contract

import cc.bodyplus.health.mvp.module.AboutInfo
import cc.bodyplus.health.mvp.presenter.IPresenter
import cc.bodyplus.health.mvp.view.report.view.BaseView

/**
 * Created by rui.gao on 2018-05-15.
 */
interface VersionContract {

    interface View : BaseView {
        /**
         * VERSION返回数据
         */
        fun setVersionData(bean: AboutInfo)

    }

    interface Presenter : IPresenter<View> {
        /**
         * 登录请求数据
         */
        fun requestVersionData(params: Map<String, String>)

    }

}