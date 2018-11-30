package cc.bodyplus.health.mvp.view.report.fragment

import android.content.Intent
import android.os.Bundle
import cc.bodyplus.health.App
import cc.bodyplus.health.R
import cc.bodyplus.health.ble.utils.monitor.EcgOriginalData
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
import kotlinx.android.synthetic.main.fragment_result.*
import org.json.JSONArray

/**
 * Created by rui.gao on 2018-05-02.
 */
class ReportResultFragment : BaseFragment(), EcgContract.View{

    private var mTitle: String? = null
    private var detectId: String = ""
    private var stampItem: String = ""
    private var itemList = ArrayList<ReportEcgListBean>()
    private var myAdapter : ResultAdapter ?= null //by lazy { ResultAdapter(itemList ,activity!!) }
    private var data : ReportInfo2 ?= null
    private val progressDialog by lazy {
        ProgressDialog(activity)
    }

    private val mPresenter by lazy { EcgListPresenter() }

    init {
        mPresenter.attachView(this)
    }
    companion object {
        fun getInstance(title: String,bean : ReportInfo2,detectId: String): ReportResultFragment {
            val fragment = ReportResultFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            fragment.mTitle = title
            fragment.data = bean
            fragment.detectId = detectId
            return fragment
        }
        fun getInstance(title: String,detectId: String): ReportResultFragment {
            val fragment = ReportResultFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            fragment.mTitle = title
            fragment.detectId = detectId
            return fragment
        }

        fun setData(fragment: ReportResultFragment,bean : ReportInfo2){
            fragment.data = bean
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_result
    }

    override fun initView() {
        App.instance.regeditAction(action)
        initData()
    }

    var action: Action = object : Action {
        override fun callBack(code: Int, dataItem: Any): Boolean {
            if (code == Config.UPDATE_REPORT_DETAILS) {
                if (dataItem != null && dataItem is ReportInfo2) {
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

        tv_doctor.text = "诊断医生："+data?.diagnosisDoctor
        tv_doctor_zg.text = "主管医生："+data?.chiefDoctor
        text_diagnosis.text =  data?.diagnosisAnalysis
        text_chiefAnalysis.text = data?.chiefAnalysis
        if(data?.diagnosisAnalysis == "") {
            text_diagnosis.text = "暂未诊断"
        }
        if(data?.chiefAnalysis == "") {
            text_chiefAnalysis.text = "暂未诊断"
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

    override fun onDestroy() {
        App.instance.removeAction(action)
        super.onDestroy()
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

    override fun showErrorMsg(errorMsg: String) {
        progressDialog.dismiss()
    }

    override fun showLoading() {
    }

    override fun dismissLoading() {
        progressDialog.dismiss()
    }

    override fun lazyLoad() {

    }
}