package cc.bodyplus.health.mvp.view.report.adapter

import android.support.v4.app.FragmentActivity
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import cc.bodyplus.health.R
import cc.bodyplus.health.mvp.module.BPRecordModel
import cc.bodyplus.health.utils.DateUtils
import cc.bodyplus.health.utils.EcgStatusUtils
import cc.bodyplus.health.utils.StateUtils
import cc.bodyplus.health.widget.flowtag.FlowTagLayout
import cc.bodyplus.health.widget.flowtag.FlowTagLayout.FLOW_TAG_CHECKED_NONE

/**
 * Created by rui.gao on 2018-05-03.
 */
class MonitorAdapter (var mList: ArrayList<BPRecordModel>, var mContext: FragmentActivity): BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var holder: ViewHolder
        var v: View
        if (convertView == null) {
            holder = ViewHolder()
            v = View.inflate(mContext, R.layout.item_report_monitor, null)
            holder.tv_title = v.findViewById(R.id.tv_title)
            holder.tv_time = v.findViewById(R.id.tv_time)
            holder.tv_ms = v.findViewById(R.id.tv_ms)
            holder.tag_flow_layout = v.findViewById(R.id.tag_flow_layout)
            v.tag = holder
        } else {
            v = convertView
            holder = v.tag as ViewHolder
        }
        val recordModel = mList[position]
        val title = EcgStatusUtils.generateEcgStatus(recordModel.desc)
        holder.tv_title.text = title
        val adapter = ReportMonitorTagAdapter<String>(mContext)
        holder.tag_flow_layout.setTagCheckedMode(FLOW_TAG_CHECKED_NONE)
        holder.tag_flow_layout.adapter = adapter
        val datas = StateUtils.generateState(recordModel.tag)
        adapter.onlyAddAll(datas)
        var desc = "暂无描述"
        recordModel.edit?.let {
            if (it.isNotEmpty()){
                desc = it
            }
        }
        holder.tv_ms.text = desc
        holder.tv_time.text = DateUtils.getStringDateHourAndMin(recordModel.stamp)
        return v
    }

    fun setData(dataList: ArrayList<BPRecordModel>){
        mList = dataList
        notifyDataSetChanged()
    }

    override fun getItem(position: Int): Any? {
        return mList.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return mList.size
    }

    class ViewHolder {
        lateinit var tv_title: TextView
        lateinit var tv_time: TextView
        lateinit var tv_ms: TextView
        lateinit var tag_flow_layout : FlowTagLayout
    }
}