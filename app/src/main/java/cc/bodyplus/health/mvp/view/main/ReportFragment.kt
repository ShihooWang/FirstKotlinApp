package cc.bodyplus.health.mvp.view.main

import android.app.Fragment
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import cc.bodyplus.health.App
import cc.bodyplus.health.R
import cc.bodyplus.health.di.component.ActivityComponent
import cc.bodyplus.health.di.component.DaggerActivityComponent
import cc.bodyplus.health.di.module.ActivityModule
import cc.bodyplus.health.ext.showToast
import cc.bodyplus.health.mvp.contract.ReportContract
import cc.bodyplus.health.mvp.module.HistoryBean
import cc.bodyplus.health.mvp.module.ReportInfo
import cc.bodyplus.health.mvp.module.ReportInfo2
import cc.bodyplus.health.mvp.module.ReportRecord
import cc.bodyplus.health.mvp.presenter.ReportPersenter
import cc.bodyplus.health.mvp.view.report.activity.ReportActivity
import cc.bodyplus.health.mvp.view.report.adapter.ReportInfoAdapter
import cc.bodyplus.health.mvp.view.report.adapter.SlideTabPagerAdapter
import cc.bodyplus.health.utils.Action
import cc.bodyplus.health.utils.Config
import cc.bodyplus.health.utils.SharedPrefHelperUtils
import cc.bodyplus.health.utils.db.HistoryDao
import cc.bodyplus.health.widget.tablayout.OnTabSelectListener
import kotlinx.android.synthetic.main.activity_report_list.*
import okhttp3.ResponseBody
import org.joda.time.DateTime
import org.json.JSONObject
import java.util.ArrayList


/**
 * Created by shihoo.wang on 2018/5/2.
 * Email shihu.wang@bodyplus.cc
 */
open class ReportFragment: Fragment(), OnTabSelectListener ,ReportContract.View{

    private var madapter : ReportInfoAdapter?= null

    private var mReportRecordList = ArrayList<ReportRecord>()
    private var linearLayoutManager: LinearLayoutManager? = null
    private var tabLayoutAdapter : SlideTabPagerAdapter?= null

    private val mList = ArrayList<View>()
    private var mReportTimeList = ArrayList<String>()

    private val mPresenter by lazy { ReportPersenter() }

    init {
        mPresenter.attachView(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle?): View {
        activityComponent.inject(this)
        return inflater.inflate(R.layout.activity_report_list,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
    }

    fun initView(){
        var data =  HistoryDao.getInstance(activity).queryReportList(SharedPrefHelperUtils.getInstance().userId)
        data?.let {
            mReportRecordList.clear()
            mReportRecordList.addAll(data)

            val list = ArrayList<String>()
            if (mReportRecordList != null) {
                for (i in 0 until mReportRecordList.size) {
                    val split = mReportRecordList[i].date.split("-")
                    val s = split[1] + "月"
                    if (!list.contains(s)) {
                        list.add(s)
                    }
                }
            }
            mReportTimeList.clear()
            mReportTimeList.addAll(list)

            initReportInfoAdapter()
            initSlideTabInfo()
            refreshUIWithLocalData()
        }

        val isPerfect = SharedPrefHelperUtils.getInstance().isPerfect
        if (isPerfect == "1") {
            var date = DateTime()
            var map  = mapOf("month" to date.toString("yyyy-MM"))
            mPresenter.requestReportListData(map)
        }else{
            view_empty.visibility = (View.VISIBLE)
            view_report_data.visibility = (View.GONE)
        }

        App.instance.regeditAction(action)
    }

    var action: Action = object : Action {
        override fun callBack(code: Int, dataItem: Any): Boolean {
            if (code == Config.UPDATE_LOGIN_DATA) {

                val userId = SharedPrefHelperUtils.getInstance().userId
                if (userId == "" || userId == "-1" ||  userId == null) {
                    view_empty.visibility = (View.VISIBLE)
                    view_report_data.visibility = (View.GONE)
                }else{
                    view_empty.visibility = (View.GONE)
                    view_report_data.visibility = (View.VISIBLE)
                    var date = DateTime()
                    var map  = mapOf("month" to date.toString("yyyy-MM"))
                    mPresenter.requestReportListData(map)
                }
            }
            return false
        }
    }

    override fun setReportListData(value: ResponseBody) {
        try {
            var body = value.string()
            val jsonObj = JSONObject(body)
            val status = jsonObj.optInt("status")
            val msg = jsonObj.optString("msg", "错误")

            if (status == 200) {
                if (jsonObj.has("data")) {
                    mReportRecordList.clear()
                    mReportTimeList.clear()

                    val data = jsonObj.getJSONObject("data")
                    var monthList = data.getJSONArray("monthList")

                    val list = ArrayList<String>()
                    if (monthList != null) {
                        for (i in 0 until monthList.length()) {
                            val split = monthList.getString(i).split("-")
                            val s = split[1] + "月"
                            if (!list.contains(s)) {
                                list.add(s)
                            }
                        }
                    }
                    mReportTimeList.addAll(list)

                    var detectList = data.optString("detectList")
                    var jsonData = JSONObject(detectList)
                    val iterator = jsonData?.keys()
                    var key :String
                    while (iterator!!.hasNext()) {
                        var report = ReportRecord()
                        key = iterator.next()
                        report.date = key
                        val keyJSON = jsonData?.getJSONArray(key)
                        for (i in 0 until keyJSON!!.length()){
                            val detectId = keyJSON.getJSONObject(i).getString("detectId")
                            val detectDate =keyJSON.getJSONObject(i).getString("detectDate")
                            val detectTime =keyJSON.getJSONObject(i).getString("detectTime")
                            val isView =keyJSON.getJSONObject(i).getString("isView")
                            val isCheck =keyJSON.getJSONObject(i).getString("isCheck")
                            val patientView =keyJSON.getJSONObject(i).getString("patientView")
                            val reportUrl =keyJSON.getJSONObject(i).getString("reportUrl")
                            var hisBean = HistoryBean()
                            hisBean.detectDate = detectDate
                            hisBean.detectTime = detectTime
                            hisBean.isView = isView
                            hisBean.detectId = detectId
                            hisBean.isCheck = isCheck
                            hisBean.patientView = patientView
                            hisBean.userID = SharedPrefHelperUtils.getInstance().userId
                            hisBean.reportUrl = reportUrl
                            report.dataList.add(hisBean)
                        }
                        mReportRecordList.add(report)
                    }

                    if(mReportRecordList.size > 0){
                        HistoryDao.getInstance(activity).addMonthReportRecord(SharedPrefHelperUtils.getInstance().userId,mReportRecordList)
                        initReportInfoAdapter()
                        initSlideTabInfo()
                        refreshUIWithLocalData()
                    }else{
                        view_empty.visibility = (View.VISIBLE)
                        view_report_data.visibility = (View.GONE)
                    }
                } else {
                   activity.showToast(msg)
                }
            } else {
                activity.showToast(msg)
            }
        } catch (e: Exception) {
            throw RuntimeException(e.message)
        } finally {
            value.close()
        }
    }



    private fun refreshUIWithLocalData() {
        mList.clear()

        if(mReportTimeList.size > 0) {
            for (i in mReportTimeList.indices) {
                val textView = TextView(activity)
                mList.add(textView)
            }
            tabLayoutAdapter!!.notifyDataSetChanged()
            stl_tablayout.setViewPager(viewPager, mReportTimeList)
        }
    }

    /**
     * 页面上部月份日期 slideTabLayout信息设置
     */
    private fun initSlideTabInfo() {
        tabLayoutAdapter = SlideTabPagerAdapter(mList)
        viewPager.adapter = tabLayoutAdapter
        stl_tablayout.setOnTabSelectListener(this)
    }

    /**
     * 数据列表相关控件的设置
     */

    private fun initReportInfoAdapter() {
        recyclerView.setFocusable(false)
        madapter = ReportInfoAdapter(activity, mReportRecordList)
        linearLayoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = madapter


        madapter!!.setOnReportInfoClickListener(object : ReportInfoAdapter.OnReportInfoClickListener{
            override fun onReportInfoClick(reportInfoPosition: Int, recordItemPosition: Int,view: View) {
                onClickItem(reportInfoPosition, recordItemPosition,view)
            }
        })
        recyclerView.setOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
//                if (recyclerView != null && recyclerView.childCount > 0) {
//                    val topOfFirstItemVisible = recyclerView.getChildAt(0).top == 0
//                    //                    srl.setEnabled(topOfFirstItemVisible);
//
//                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val position = linearLayoutManager!!.findFirstVisibleItemPosition()
                viewPager.setCurrentItem(position)
            }
        })
    }

    private fun onClickItem(reportInfoPosition: Int, recordItemPosition: Int,view: View) {

        var intent = Intent()
        intent.setClass(activity,ReportActivity::class.java)
        val bean = mReportRecordList[reportInfoPosition].dataList[recordItemPosition]
        intent.putExtra("detectId",bean.detectId)
        intent.putExtra("title",bean.detectDate)
        intent.putExtra("isCheck",bean.isCheck)
        intent.putExtra("patientView",bean.patientView)
        intent.putExtra("reportUrl",bean.reportUrl)
        startActivity(intent)

        if(bean.patientView == "0" && bean.isCheck  == "0"){
            bean.patientView = "1"
        }else{
            bean.patientView = "2"
        }
        HistoryDao.getInstance(activity).addMonthReportRecord(SharedPrefHelperUtils.getInstance().userId,mReportRecordList)

        if(view!= null) {
            var image = view.findViewById<ImageView>(R.id.image_new)
            var textView = view.findViewById<TextView>(R.id.tv_status)

            if (bean.patientView == "0") {
                image.visibility = View.VISIBLE
                textView.visibility = View.GONE
            } else if (bean.patientView == "1" && bean.isCheck == "1") {
                image.visibility = View.VISIBLE
                image.setImageResource(R.drawable.ic_img_red_show)
                textView.visibility = View.GONE
            } else if (bean.patientView == "2") {
                image.visibility = View.GONE
                textView.visibility = View.GONE
            } else {
                image.visibility = View.GONE
                textView.visibility = View.GONE
            }
        }

    }

    override fun onTabSelect(position: Int) {
        if (madapter != null && linearLayoutManager != null) {
            madapter!!.notifyDataSetChanged()
            linearLayoutManager!!.scrollToPositionWithOffset(position, 0)
        }
    }

    override fun onTabReselect(position: Int) {
    }

    override fun showErrorMsg(errorMsg: String) {
        activity.showToast(errorMsg)
    }

    override fun showLoading() {
    }

    override fun dismissLoading() {
    }

    protected val activityComponent: ActivityComponent by lazy {
        DaggerActivityComponent.builder()
                .appComponent(App.instance.appComponent)
                .activityModule(ActivityModule(activity))
                .build()
    }

    override fun setReportDetailsData(bean: ReportInfo2) {
    }

    override fun onDestroy() {
        mPresenter.detachView()
        App.instance.removeAction(action)
        super.onDestroy()
    }
}