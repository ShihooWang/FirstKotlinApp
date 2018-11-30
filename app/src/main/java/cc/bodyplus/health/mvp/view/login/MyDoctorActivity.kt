package cc.bodyplus.health.mvp.view.login

import cc.bodyplus.health.R
import cc.bodyplus.health.ext.showToast
import cc.bodyplus.health.mvp.contract.DoctorContract
import cc.bodyplus.health.mvp.module.DoctorBean
import cc.bodyplus.health.mvp.presenter.DoctorPresenter
import cc.bodyplus.health.mvp.view.BaseActivity
import cc.bodyplus.health.mvp.view.report.adapter.MyDoctorAdapter
import cc.bodyplus.health.utils.SharedPrefHelperUtils
import com.scwang.smartrefresh.header.MaterialHeader
import kotlinx.android.synthetic.main.activity_my_doctor.*

/**
 * Created by rui.gao on 2018-05-09.
 */
class MyDoctorActivity : BaseActivity(), DoctorContract.View{

    private var mMaterialHeader: MaterialHeader? = null
    private var itemList = ArrayList<DoctorBean>()
    private val myAdapter by lazy { MyDoctorAdapter(itemList ,this!!) }

    private val mPresenter by lazy { DoctorPresenter() }

    init {
        mPresenter.attachView(this)
    }
    override fun initInject() {
        activityComponent.inject(this)
    }
    override fun layoutId(): Int  = R.layout.activity_my_doctor

    override fun initData() {
        listView.adapter = myAdapter
        listView.emptyView = view_empty

        if(SharedPrefHelperUtils.getInstance().userId == "-1"){
            return
        }

        var map  = mapOf("data" to "1")
        mPresenter.requestDoctorData(map)
    }

    override fun initView() {
        title_back.setOnClickListener { finish() }
        mRefreshLayout.setEnableHeaderTranslationContent(true)
        mRefreshLayout.setOnRefreshListener {
            if(SharedPrefHelperUtils.getInstance().userId != "-1"){
                var map  = mapOf("data" to "1")
                mPresenter.requestDoctorData(map)
            }
        }
        mMaterialHeader = mRefreshLayout.refreshHeader as MaterialHeader?
        //打开下拉刷新区域块背景:
        mMaterialHeader?.setShowBezierWave(true)
        //设置下拉刷新主题颜色
        mRefreshLayout.setPrimaryColorsId(R.color.color_text_6, R.color.color_title_bg)
    }
    override fun setDoctorData(bean: ArrayList<DoctorBean>) {
        if(bean!= null){
            myAdapter.addData(bean)
            mRefreshLayout.finishRefresh()
        }

    }

    override fun showErrorMsg(errorMsg: String) {
        showToast(errorMsg)
        mRefreshLayout.finishRefresh()
    }

    override fun showLoading() {
    }

    override fun dismissLoading() {
    }

    override fun onDestroy() {
        mPresenter.detachView()
        super.onDestroy()
    }
}