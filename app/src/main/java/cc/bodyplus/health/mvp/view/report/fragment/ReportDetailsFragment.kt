package cc.bodyplus.health.mvp.view.report.fragment

import android.graphics.Typeface
import android.os.Bundle
import cc.bodyplus.health.App
import cc.bodyplus.health.R
import cc.bodyplus.health.mvp.module.EventList
import cc.bodyplus.health.mvp.module.ReportInfo
import cc.bodyplus.health.mvp.view.BaseFragment
import cc.bodyplus.health.mvp.view.report.adapter.ReportDetailsDetailAdapter
import cc.bodyplus.health.utils.Action
import cc.bodyplus.health.utils.Config
import kotlinx.android.synthetic.main.fragment_report_details.*

/**
 * Created by rui.gao on 2018-05-02.
 */
class ReportDetailsFragment : BaseFragment(){

    private var mTitle: String? = null
    private val typeFaceYaHei by lazy { Typeface.createFromAsset(App.instance!!.assets, "fonts/DIN.ttf") }
    private var mAdapter : ReportDetailsDetailAdapter? = null
    private var itemList = ArrayList<EventList>()
    private var data : ReportInfo ?= null

    companion object {
        fun getInstance(title: String,bean : ReportInfo): ReportDetailsFragment {
            val fragment = ReportDetailsFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            fragment.mTitle = title
            fragment.data = bean
            return fragment
        }

        fun getInstance(title: String): ReportDetailsFragment {
            val fragment = ReportDetailsFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            fragment.mTitle = title
            return fragment
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_report_details
    }

    override fun initView() {
        tv_avg_heart.typeface = typeFaceYaHei
        tv_max_heart.typeface = typeFaceYaHei
        tv_min_heart.typeface = typeFaceYaHei
        tv_slow_heart.typeface = typeFaceYaHei
        tv_time.typeface = typeFaceYaHei
        tv_fast_heart.typeface = typeFaceYaHei
        list.setFocusable(false)

        initData()

        App.instance.regeditAction(action)
    }

    var action: Action = object : Action {
        override fun callBack(code: Int, dataItem: Any): Boolean {
            if (code == Config.UPDATE_REPORT_DETAILS) {
                if (dataItem != null && dataItem is ReportInfo) {
                    data = dataItem
                    initData()
                }
            }
            return false
        }
    }

    private fun initData(){
        if(data == null){
            return
        }
        data?.let {
            tv_avg_heart.text= data?.avgHr
            tv_max_heart.text= data?.maxHr
            tv_min_heart.text= data?.minHr
            tv_slow_heart.text = getTimeMin(data?.brady)
            tv_time.text = getTime(data?.detectTime)
            tv_fast_heart.text = getTimeMin(data?.tachy)
        }
        itemList.clear()
        var even =  data?.eventRecord
        even?.let {
            itemList.addAll(even)
        }
        mAdapter = ReportDetailsDetailAdapter(itemList,activity!!)
        list.adapter = mAdapter
        list.setFocusable(false)
    }

    private fun getTime(time:String?):String{
        var date = Integer.valueOf(time)
        var hours  = (date/3600)
        var min  = (date % 3600 / 60 + 0.999).toInt()
        return hours.toString() +":"+ min.toString()
    }

    private fun getTimeMin(time:String?):String{
        var date = Integer.valueOf(time)
        var min  = (date/60 + 0.999).toInt()
        return min.toString()
    }


    override fun lazyLoad() {

    }

    override fun onDestroy() {
        App.instance.removeAction(action)
        super.onDestroy()
    }
}