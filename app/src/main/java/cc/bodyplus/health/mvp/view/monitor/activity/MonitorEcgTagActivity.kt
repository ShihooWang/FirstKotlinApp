package cc.bodyplus.health.mvp.view.monitor.activity

import android.content.Intent
import android.os.Build
import android.transition.Slide
import cc.bodyplus.health.R
import cc.bodyplus.health.ble.utils.monitor.EcgOriginalData
import cc.bodyplus.health.ext.showToast
import cc.bodyplus.health.mvp.view.BaseActivity
import cc.bodyplus.health.mvp.view.monitor.adapter.TagAdapter
import cc.bodyplus.health.utils.AutoUploadEcgRecord
import cc.bodyplus.health.utils.EcgStatusUtils
import cc.bodyplus.health.utils.SharedPrefHelperUtils
import cc.bodyplus.health.utils.StateUtils
import cc.bodyplus.health.utils.db.HistoryDao
import cc.bodyplus.health.widget.dialog.CancleDialog
import cc.bodyplus.health.widget.dialog.ProgressDialog
import cc.bodyplus.health.widget.flowtag.FlowTagLayout
import kotlinx.android.synthetic.main.activity_monitor_ecg_tag.*
import kotlin.collections.ArrayList

/**
 * Created by shihoo.wang on 2018/5/9.
 * Email shihu.wang@bodyplus.cc
 */

class MonitorEcgTagActivity : BaseActivity() {

    private val progressDialog by lazy {
        ProgressDialog(this)
    }

    private val mTagAdapter : TagAdapter<String> by lazy {
        TagAdapter<String>(this)
    }
    private val TAG_DEFAUT = intArrayOf(0,0,0,0,0,0,0,0,0)


    override fun initInject() {
        activityComponent.inject(this)
    }

    override fun layoutId(): Int {
        return R.layout.activity_monitor_ecg_tag
    }

    override fun initView() {
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
            window.enterTransition = Slide().setDuration(300)
            window.exitTransition = Slide().setDuration(300)
        }
    }

    override fun initData() {
        initTagsView()
        initMonitorResult()
        bt_show_ecg.setOnClickListener { startActivity(Intent(this, EcgViewReportActivity::class.java)) }
        bt_commit.setOnClickListener { commitData() }
        iv_closed.setOnClickListener { showCancelDialog() }
    }

    override fun onBackPressed() {
        showCancelDialog()
    }

    private fun initMonitorResult(){
        val bPRecordModel = EcgOriginalData.mInstance.getBPRecordModel()
        bPRecordModel?.let {
            val result = EcgStatusUtils.generateEcgStatus(it.desc)
            if (result == EcgStatusUtils.NO_ERROR){
                monitor_tv_result.text = "未见异常"
            }else{
                monitor_tv_result.text = "疑似：$result"
            }

        }?:let {
            monitor_tv_result.text = "未见异常"
        }
    }

    private fun initTagsView(){
        tag_flow_layout.setTagCheckedMode(FlowTagLayout.FLOW_TAG_CHECKED_MULTI)
        tag_flow_layout.adapter = mTagAdapter
        tag_flow_layout.setOnTagSelectListener { _, selectedList ->
            EcgOriginalData.mInstance.getBPRecordModel()?.tag = generateTagIntArr(selectedList)
        }
        val dataSource = ArrayList<String>()
        dataSource.add(StateUtils.STATE_0)
        dataSource.add(StateUtils.STATE_1)
        dataSource.add(StateUtils.STATE_2)
        dataSource.add(StateUtils.STATE_3)
        dataSource.add(StateUtils.STATE_4)
        dataSource.add(StateUtils.STATE_5)
        dataSource.add(StateUtils.STATE_6)
        dataSource.add(StateUtils.STATE_7)
        dataSource.add(StateUtils.STATE_8)
        mTagAdapter.onlyAddAll(dataSource)
    }

    /**
     * 提交数据
     */
    private fun commitData(){
        val userId = SharedPrefHelperUtils.getInstance().userId
        val bpRecordModel = EcgOriginalData.mInstance.getBPRecordModel()
        bpRecordModel?.let {
            progressDialog.show()
            progressDialog.setMessage("正在提交...")
            it.edit = monitor_edit.text.toString().trim()
            HistoryDao.getInstance(this).addMonitorEcgRecord(userId,it,0)
            val callBack = object : AutoUploadEcgRecord.CallBack{
                override fun succeed() {
                    showToast("上传成功！")
                    progressDialog.dismiss()
                    finish()
                }

                override fun fail(msg: String) {
                    showToast("上传失败，等待自动上传！")
                    progressDialog.dismiss()
                    finish()
                }

            }
            AutoUploadEcgRecord(userId,this).uploadEcgData(bpRecordModel,callBack,true)
        }


    }

    private fun generateTagIntArr(selectedList: MutableList<Int>?): ArrayList<Int> {
        val result = TAG_DEFAUT
        selectedList?.let {
            for (i in selectedList){
                result[i]=1
            }
        }
        val list = ArrayList<Int>()
        list.addAll(result.asList())
        return list
    }


    private fun showCancelDialog(){
        val dialog = CancleDialog(this)
        dialog.setTitleTxt("是否放弃保存？")
        dialog.setConfirmTxt("确认")
        dialog.setCancelTxt("取消")
        dialog.setCancelable(true)
        dialog.setDialogClickListener(object : CancleDialog.OnClickListener{
            override fun onCancelBtnClick() {
                dialog.dismiss()
            }

            override fun onConfirmBtnClick() {
                dialog.dismiss()
                finish()
            }

        })
        dialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        progressDialog?.run {
            if (isShowing){
                dismiss()
            }
        }
    }

}