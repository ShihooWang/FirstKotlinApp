package cc.bodyplus.health.mvp.presenter

import cc.bodyplus.health.mvp.view.report.view.BaseView

/**
 * Created by rui.gao on 2018-05-09.
 */
interface IPresenter<in V: BaseView> {

    fun attachView(mRootView: V)

    fun detachView()

}