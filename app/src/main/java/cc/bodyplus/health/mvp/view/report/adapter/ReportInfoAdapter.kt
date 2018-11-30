package cc.bodyplus.health.mvp.view.report.adapter

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cc.bodyplus.health.R
import cc.bodyplus.health.mvp.module.ReportRecord
import cc.bodyplus.health.mvp.view.report.view.RecordViewHolder

/**
 * Created by rui.gao on 2018-05-03.
 */
class ReportInfoAdapter(var context: Context,var mList : ArrayList<ReportRecord>) : RecyclerView.Adapter<RecordViewHolder>() {
    private var inflater: LayoutInflater? = null

    init {
        inflater = LayoutInflater.from(context)
    }

    fun setReportRecordList(list0: ArrayList<ReportRecord>) {
        mList.clear()
        mList.addAll(list0)
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder {
        val view = inflater!!.inflate(R.layout.view_report_info_item, parent, false)
        return RecordViewHolder(view!!)
    }

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        val reportRecord = mList[position]
        var month = reportRecord.date//2017-7-11
        val split = month!!.split("-".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
        month = split[0] + "年" + split[1] + "月"
        holder.setText(R.id.tv_time_item,month)
        val dataList = reportRecord.dataList
        val recordItemAdapter = RecordItemAdapter( context,dataList)
        var list = holder.getView<RecyclerView>(R.id.listView)
        list.layoutManager = LinearLayoutManager(context)
        list.adapter = recordItemAdapter
        recordItemAdapter.setOnRecordItemClickListener(object : RecordItemAdapter.OnRecordItemClickListener {
            override fun onRecordItemClick(recordItemPosition: Int, view: View) {
                mOnReportInfoClickListener.onReportInfoClick(position, recordItemPosition,view)
            }
        })

    }

    override fun getItemCount(): Int {
        return mList.size
    }

    lateinit var mOnReportInfoClickListener: OnReportInfoClickListener

    interface OnReportInfoClickListener {
        fun onReportInfoClick(reportInfoPosition: Int, recordItemPosition: Int,view :View)
    }

    fun setOnReportInfoClickListener(mOnReportInfoClickListener: OnReportInfoClickListener) {
        this.mOnReportInfoClickListener = mOnReportInfoClickListener
    }

}