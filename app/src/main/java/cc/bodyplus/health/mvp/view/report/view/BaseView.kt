package cc.bodyplus.health.mvp.view.report.view

/**
 * Created by rui.gao on 2018-05-09.
 */
interface BaseView {
    fun showErrorMsg(errorMsg : String)
    fun showLoading()
    fun dismissLoading()
}