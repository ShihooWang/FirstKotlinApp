package cc.bodyplus.health.mvp.view.report.adapter

import android.support.v4.app.FragmentActivity
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import cc.bodyplus.health.R
import cc.bodyplus.health.mvp.module.EventList

/**
 * Created by rui.gao on 2018-05-02.
 */
class ReportDetailsDetailAdapter (var mList: List<EventList>, var mContext:FragmentActivity): BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var holder: ViewHolder
        var v: View
        if (convertView == null) {
            holder = ViewHolder()
            v = View.inflate(mContext, R.layout.item_report_details, null)
            holder.tv_title = v.findViewById(R.id.tv_title)
            holder.tv_num = v.findViewById(R.id.tv_num)
            v.tag = holder
        } else {
            v = convertView
            holder = v.tag as ViewHolder
        }
        holder.tv_title.text = mList[position].name
        holder.tv_num.text = mList[position].value +"次"
        return v
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
        lateinit var tv_num: TextView
    }
}