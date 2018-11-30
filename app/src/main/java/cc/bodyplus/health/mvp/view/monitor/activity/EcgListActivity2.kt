package cc.bodyplus.health.mvp.view.monitor.activity

import android.content.Intent
import android.os.Build
import android.support.annotation.RequiresApi
import android.transition.Slide
import cc.bodyplus.health.R
import cc.bodyplus.health.ble.utils.monitor.EcgOriginalData
import cc.bodyplus.health.mvp.contract.EcgContract
import cc.bodyplus.health.mvp.module.BPRecordModel
import cc.bodyplus.health.mvp.module.EcgDataBean
import cc.bodyplus.health.mvp.module.EcgRecordList
import cc.bodyplus.health.mvp.module.ReportInfo
import cc.bodyplus.health.mvp.presenter.EcgListPresenter
import cc.bodyplus.health.mvp.view.BaseActivity
import cc.bodyplus.health.mvp.view.report.adapter.MonitorAdapter
import cc.bodyplus.health.utils.SharedPrefHelperUtils
import cc.bodyplus.health.utils.db.HistoryDao
import cc.bodyplus.health.widget.dialog.ProgressDialog
import com.scwang.smartrefresh.header.MaterialHeader
import kotlinx.android.synthetic.main.activity_ecg_list2.*
import org.json.JSONArray
import java.util.*

/**
 * Created by shihoo.wang on 2018/5/7.
 * Email shihu.wang@bodyplus.cc
 */

class EcgListActivity2 : BaseActivity() , EcgContract.View{

    private var detectId: String = ""
    private var mMaterialHeader: MaterialHeader? = null
    private var itemList = ArrayList<BPRecordModel>()
    private val myAdapter by lazy { MonitorAdapter(itemList ,this!!) }
    private val progressDialog by lazy {
        ProgressDialog(this)
    }
    private var bpRecordModel : BPRecordModel ?= null

    private val mPresenter by lazy { EcgListPresenter() }

    private var nextPage = "1"


    override fun initInject() {
        activityComponent.inject(this)
    }

    override fun layoutId(): Int {
        return R.layout.activity_ecg_list2
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun initView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.enterTransition = Slide().setDuration(300)
            window.exitTransition = Slide().setDuration(300)
        }
        ig_closed_list.setOnClickListener({
            onBackPressed()
        })

        mRefreshLayout.setEnableHeaderTranslationContent(true)
        mRefreshLayout.setOnRefreshListener {
            mRefreshLayout.finishRefresh(1000)
        }
        mMaterialHeader = mRefreshLayout.refreshHeader as MaterialHeader?
        //打开下拉刷新区域块背景:
        mMaterialHeader?.setShowBezierWave(true)
        //设置下拉刷新主题颜色
        mRefreshLayout.setPrimaryColorsId(R.color.color_text_6, R.color.color_title_bg)
        mRefreshLayout.setOnLoadmoreListener({
            requestData()
        })


        listView.emptyView = view_empty
        listView.setOnItemClickListener { parent, view, position, id ->
            val stamp = itemList[position].stamp.toString()
            // TODO 判断本地是否有数据
            val bpRecord = HistoryDao.getInstance(this).getMonitorEcgRecord(stamp,SharedPrefHelperUtils.getInstance().userId)
            bpRecord?.let {
                showEcgActivity(bpRecord)
            }?: let {
                val map  = mapOf("detectId" to detectId,"timestamp" to stamp)
                bpRecordModel = itemList[position]
                mPresenter.requestEcgData(map)
                progressDialog.show()
            }

        }
        listView.adapter = myAdapter
    }

    override fun initData() {
        mPresenter.attachView(this)
        postDelayed(initRunnable,500)
    }

    var initRunnable : Runnable = Runnable {
        requestData()
    }

    override fun onDestroy() {
        super.onDestroy()
        removeCallbacks(initRunnable)
    }


    private fun requestData(){
        val map = mapOf("page" to nextPage)
        mPresenter.requestEcgRecordList(map)
    }

    override fun setEcgRecordList(list: EcgRecordList) {
        mRefreshLayout.finishLoadmore(1000)

        val modelList = ArrayList<BPRecordModel>()
        for (data in list.dataList){
            val bean = BPRecordModel()

            val tagStr = data.tags
            val jsonArray = JSONArray(tagStr)
            val tagList :ArrayList<Int> = ArrayList<Int>()
            if(jsonArray.length() > 0){
                for(i in 0 until jsonArray.length()){
                    tagList.add(jsonArray.getInt(i))
                }
            }
            bean.tag = tagList

            val descStr = data.diagnosisType
            val descStrArray = JSONArray(descStr)
            val descList :ArrayList<Int> = ArrayList<Int>()
            if(descStrArray.length() > 0){
                for(i in 0 until descStrArray.length()){
                    descList.add(descStrArray.getInt(i))
                }
            }
            bean.desc = descList
            bean.edit = data.record
            bean.stamp = data.timestamp.toLong()

            modelList.add(bean)
        }
        itemList.addAll(modelList)
        myAdapter.notifyDataSetChanged()
        if (list.nextPage.toInt() < 0){
            nextPage = "1"
            mRefreshLayout.isEnableLoadmore = false
        }else {
            mRefreshLayout.isEnableLoadmore = true
            nextPage = list.nextPage
        }
    }

    override fun showErrorMsg(errorMsg: String) {
        progressDialog.dismiss()
        mRefreshLayout.finishRefresh()
    }

    override fun showLoading() {

    }

    override fun dismissLoading() {

    }

    override fun setEcgData(bean: EcgDataBean) {
        progressDialog.dismiss()
        bpRecordModel?.let {
            val jsonArray = JSONArray(bean.ecg)
            val arr = IntArray(jsonArray.length())
            for(i in 0 until jsonArray.length()){
                arr[i] = jsonArray.getInt(i)
            }
            it.dealArr = arr
            it.avgHR = bean.avgHr.toInt()
            HistoryDao.getInstance(this).addMonitorEcgRecord(SharedPrefHelperUtils.getInstance().userId,it,1)
            showEcgActivity(it)
        }
    }

    private fun showEcgActivity(bpRecord: BPRecordModel) {
        EcgOriginalData.mInstance.setBPRecordModel(bpRecord)
        startActivity(Intent(this, EcgViewReportActivity::class.java))
    }






}