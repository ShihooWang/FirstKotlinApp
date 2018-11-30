package cc.bodyplus.health.mvp.view.report.activity

import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.view.View
import android.widget.Toast
import cc.bodyplus.health.App
import cc.bodyplus.health.R
import cc.bodyplus.health.ext.showToast
import cc.bodyplus.health.mvp.contract.ReportContract
import cc.bodyplus.health.mvp.module.ReportInfo
import cc.bodyplus.health.mvp.module.ReportInfo2
import cc.bodyplus.health.mvp.presenter.ReportPersenter
import cc.bodyplus.health.mvp.view.BaseActivity
import cc.bodyplus.health.mvp.view.report.adapter.BaseFragmentAdapter
import cc.bodyplus.health.mvp.view.report.fragment.ReportMonitorFragment
import cc.bodyplus.health.mvp.view.report.fragment.ReportDetailsFragment2
import cc.bodyplus.health.mvp.view.report.fragment.ReportResultFragment
import cc.bodyplus.health.mvp.view.report.view.TabLayoutHelper
import cc.bodyplus.health.utils.Config
import cc.bodyplus.health.utils.SharedPrefHelperUtils
import cc.bodyplus.health.utils.db.HistoryDao
import kotlinx.android.synthetic.main.activity_report.*
import okhttp3.ResponseBody


/**
 * Created by rui.gao on 2018-05-02.
 */
class ReportActivity : BaseActivity(),ReportContract.View{

    private val tabList = ArrayList<String>()

    private val fragments = ArrayList<Fragment>()

    private var detectId:String = ""
    private var titleStr :String = ""
    private var isCheck :String = ""
    private var patientView :String = ""

    private var resultFragment :ReportResultFragment ?= null
    private var monitorFragment : ReportMonitorFragment?= null
    private var detailsFragment :ReportDetailsFragment2?= null

    private val mPresenter by lazy { ReportPersenter() }

    init {
        mPresenter.attachView(this)
    }
    override fun initInject() {
        activityComponent.inject(this)
    }

    override fun layoutId(): Int {
        return R.layout.activity_report
    }

    override fun initData() {

    }

    override fun initView() {
        detectId = intent.getStringExtra("detectId")
        titleStr = intent.getStringExtra("title")
        isCheck = intent.getStringExtra("isCheck")
        patientView = intent.getStringExtra("patientView")
        val reportUrl = intent.getStringExtra("reportUrl")
        tv_header_title.text = titleStr

        title_back.setOnClickListener { finish() }

//        var data = HistoryDao.getInstance(this).queryReportDetails(SharedPrefHelperUtils.getInstance().userId,detectId)
        var data = null
        tabList.clear()
        fragments.clear()

        tabList.add("详情")
        tabList.add("心电片段")
        tabList.add("结论")
        if(data != null){
//            detailsFragment = ReportDetailsFragment2.getInstance("详情","")
//            monitorFragment = ReportMonitorFragment.getInstance("心电片段",data,detectId)
//            resultFragment = ReportResultFragment.getInstance("结论",data,detectId)
        }else{
            detailsFragment = ReportDetailsFragment2.getInstance("详情",reportUrl)
            monitorFragment = ReportMonitorFragment.getInstance("心电片段",detectId)
            resultFragment = ReportResultFragment.getInstance("结论",detectId)
        }
        fragments.add(detailsFragment!!)
        fragments.add(monitorFragment!!)
        fragments.add(resultFragment!!)

        mViewPager.adapter = BaseFragmentAdapter(this.supportFragmentManager, fragments, tabList)
        mTabLayout.setupWithViewPager(mViewPager)
        TabLayoutHelper.setUpIndicatorWidth(mTabLayout)

        if(isCheck == "1" && patientView == "1") {
            redShow.visibility = View.VISIBLE
        }else{
            redShow.visibility = View.GONE
        }

        var map  = mapOf("detectId" to detectId)
        mPresenter.requestReportDetailsData(map)

        mTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                if(tab.text == "结论"){
                    redShow.visibility = View.GONE
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
            }
        })
    }

    override fun showErrorMsg(errorMsg: String) {
        showToast(errorMsg)
    }

    override fun showLoading() {
    }

    override fun dismissLoading() {
    }

    override fun setReportDetailsData(bean: ReportInfo2) {
//        HistoryDao.getInstance(this).addReportDetails(SharedPrefHelperUtils.getInstance().userId,detectId,bean)
        resultFragment?.let {
            ReportResultFragment.setData(it,bean)
        }

        monitorFragment?.let {
            ReportMonitorFragment.setData(it,bean)
        }

        App.instance.execCallBack(Config.UPDATE_REPORT_DETAILS,bean)
    }

    override fun setReportListData(bean: ResponseBody) {
    }
}