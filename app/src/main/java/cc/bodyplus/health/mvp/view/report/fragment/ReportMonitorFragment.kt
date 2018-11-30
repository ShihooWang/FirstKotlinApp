package cc.bodyplus.health.mvp.view.report.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import cc.bodyplus.health.App
import cc.bodyplus.health.R
import cc.bodyplus.health.ble.utils.monitor.EcgOriginalData
import cc.bodyplus.health.di.component.ActivityComponent
import cc.bodyplus.health.di.component.DaggerActivityComponent
import cc.bodyplus.health.di.module.ActivityModule
import cc.bodyplus.health.mvp.contract.EcgContract
import cc.bodyplus.health.mvp.module.*
import cc.bodyplus.health.mvp.presenter.EcgListPresenter
import cc.bodyplus.health.mvp.view.BaseFragment
import cc.bodyplus.health.mvp.view.monitor.activity.EcgViewReportActivity
import cc.bodyplus.health.mvp.view.report.adapter.ResultAdapter
import cc.bodyplus.health.utils.Action
import cc.bodyplus.health.utils.Config
import cc.bodyplus.health.utils.SharedPrefHelperUtils
import cc.bodyplus.health.utils.db.HistoryDao
import cc.bodyplus.health.widget.dialog.ProgressDialog
import kotlinx.android.synthetic.main.fragment_report_monitor.*
import org.json.JSONArray

/**
 * Created by rui.gao on 2018-05-02.
 */
open class ReportMonitorFragment : BaseFragment(),EcgContract.View{


    private var mTitle: String? = null
    private var detectId: String = ""
    private var stampItem: String = ""
    private var itemList = ArrayList<ReportEcgListBean>()
    private var myAdapter : ResultAdapter?= null //by lazy { ResultAdapter(itemList ,activity!!) }
    private var data : ReportInfo2?= null

    private val progressDialog by lazy {
        ProgressDialog(activity)
    }

    private val mPresenter by lazy { EcgListPresenter() }

    init {
        mPresenter.attachView(this)
    }

    companion object {
        fun getInstance(title: String,bean : ReportInfo2,detectId: String): ReportMonitorFragment {
            val fragment = ReportMonitorFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            fragment.mTitle = title
            fragment.data = bean
            fragment.detectId = detectId
            return fragment
        }
        fun getInstance(title: String,detectId:String): ReportMonitorFragment {
            val fragment = ReportMonitorFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            fragment.mTitle = title
            fragment.detectId = detectId
            return fragment
        }

        fun setData(fragment: ReportMonitorFragment,bean : ReportInfo2){
            fragment.data = bean
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_report_monitor
    }

    override fun initView() {
        activityComponent.inject(this)

        App.instance.regeditAction(action)

        initData()
    }

    var action: Action = Action { code, dataItem ->
        if (code == Config.UPDATE_REPORT_DETAILS) {
            if (dataItem != null && dataItem is ReportInfo2) {
                data = dataItem
                initData()
            }
        }
        false
    }

    private fun initData(){
        if(data == null){
            return
        }
        itemList.clear()
        itemList.addAll(data!!.reportEcgList)
        myAdapter = ResultAdapter(itemList,activity!!)
        listView.adapter = myAdapter
        listView.setFocusable(false)
//        myAdapter!!.addData(itemList)

        listView.setOnItemClickListener { parent, view, position, id ->
            val stamp = itemList[position].timestamp.toString()
            // TODO 判断本地是否有数据
            val bpRecord = HistoryDao.getInstance(activity!!).getMonitorEcgRecord(stamp, SharedPrefHelperUtils.getInstance().userId)
            bpRecord?.let {
                showEcgActivity(bpRecord)
            }?: let {
                stampItem = stamp
                val map  = mapOf("detectId" to detectId,"timestamp" to stamp)
                mPresenter.requestEcgData(map)
                progressDialog.show()
            }
        }
    }

    override fun lazyLoad() {
    }

    override fun setEcgData(bean: EcgDataBean) {
        var recordModel = BPRecordModel()
        recordModel.stamp = stampItem.toLong()

        val jsonArray = JSONArray(bean.ecg)
        val arr = IntArray(jsonArray.length())
        for(i in 0 until jsonArray.length()){
            arr[i] = jsonArray.getInt(i)
        }
        recordModel.dealArr = arr
        recordModel.avgHR = bean.avgHr.toInt()
        recordModel.type = 1
        HistoryDao.getInstance(activity!!).addMonitorEcgRecord(SharedPrefHelperUtils.getInstance().userId,recordModel,1)
        showEcgActivity(recordModel)
    }

    override fun setEcgRecordList(list: EcgRecordList) {

    }

    private fun showEcgActivity(bpRecord: BPRecordModel) {
        EcgOriginalData.mInstance.setBPRecordModel(bpRecord)
        startActivity(Intent(activity, EcgViewReportActivity::class.java))
    }

    override fun onDestroy() {
        App.instance.removeAction(action)
        super.onDestroy()
    }

    protected val activityComponent: ActivityComponent by lazy {
        DaggerActivityComponent.builder()
                .appComponent(App.instance.appComponent)
                .activityModule(ActivityModule(activity as FragmentActivity))
                .build()
    }

    override fun showErrorMsg(errorMsg: String) {
        progressDialog.dismiss()
    }

    override fun showLoading() {

    }

    override fun dismissLoading() {
        progressDialog.dismiss()
    }


}