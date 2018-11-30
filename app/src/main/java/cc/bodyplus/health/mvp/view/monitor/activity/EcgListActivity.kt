package cc.bodyplus.health.mvp.view.monitor.activity

import android.content.Intent
import android.os.Build
import android.support.annotation.RequiresApi
import android.transition.Slide
import cc.bodyplus.health.R
import cc.bodyplus.health.ble.utils.monitor.EcgOriginalData
import cc.bodyplus.health.mvp.module.BPRecordModel
import cc.bodyplus.health.mvp.view.BaseActivity
import cc.bodyplus.health.mvp.view.report.adapter.MonitorAdapter
import cc.bodyplus.health.utils.SharedPrefHelperUtils
import cc.bodyplus.health.utils.db.HistoryDao
import kotlinx.android.synthetic.main.activity_ecg_list.*
import java.util.*

/**
 * Created by shihoo.wang on 2018/5/7.
 * Email shihu.wang@bodyplus.cc
 */

class EcgListActivity : BaseActivity() {

    override fun initInject() {
    }

    override fun layoutId(): Int {
        return R.layout.activity_ecg_list
    }

    override fun initData() {
        val stamp = System.currentTimeMillis()
        val date = cc.bodyplus.health.utils.DateUtils.getDay(stamp.toString())
        val oneDayEcgRecord = HistoryDao.getInstance(this).getOneDayEcgRecord(date, SharedPrefHelperUtils.getInstance().userId)

        oneDayEcgRecord?.let {
            Collections.sort(it) {
                o1, o2 -> if (o1 is BPRecordModel && o2 is BPRecordModel) {
                (o2.stamp - o1.stamp).toInt()
            } else -1
            }
            val adapter = MonitorAdapter(it ,this)
            list_View.adapter = adapter
            list_View.setOnItemClickListener { _, _, position, _ ->
                EcgOriginalData.mInstance.setBPRecordModel(it[position])
                startActivity(Intent(this, EcgViewReportActivity::class.java))
            }
        }


    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun initView() {
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
            window.enterTransition = Slide().setDuration(300)
            window.exitTransition = Slide().setDuration(300)
        }
        ig_closed_list.setOnClickListener({
            onBackPressed()
        })
    }

}