package cc.bodyplus.health.mvp.view.report.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import cc.bodyplus.health.R
import cc.bodyplus.health.mvp.module.HistoryBean

/**
 * Created by rui.gao on 2018-05-03.
 */
class RecordItemAdapter(var mContext: Context,var dataList: List<HistoryBean>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = View.inflate(mContext, R.layout.item_view_report_record, null)
        return RecordItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val rIHolder = holder as RecordItemViewHolder
        val bean = dataList[position]

        rIHolder.tvTime!!.text = "监测时长："+ getDateString(bean.detectTime)
        rIHolder.tvType!!.text = bean.detectDate + ""

        if(bean.patientView == "0" ){
            rIHolder.image_new!!.visibility = View.VISIBLE
            rIHolder.tvStatus!!.visibility = View.GONE
        }else if(bean.patientView  == "1" && bean.isCheck == "1" ){
            rIHolder.image_new!!.setImageResource(R.drawable.ic_img_red_show)
            rIHolder.image_new!!.visibility = View.VISIBLE
            rIHolder.tvStatus!!.visibility = View.GONE
        }else if(bean.patientView  == "2"){
            rIHolder.image_new!!.visibility = View.GONE
            rIHolder.tvStatus!!.visibility = View.GONE
        }else{
            rIHolder.image_new!!.visibility = View.GONE
            rIHolder.tvStatus!!.visibility = View.GONE
        }

        rIHolder.mItemView.setOnClickListener(View.OnClickListener {
            mOnRecordItemClickListener.onRecordItemClick(position,rIHolder.mItemView)
        })

    }


    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    override fun getItemCount(): Int {
        return dataList.size
    }


    inner class RecordItemViewHolder(var mItemView: View) : RecyclerView.ViewHolder(mItemView) {
        var tvType: TextView? = null
        var image_new: ImageView? = null
        var tvTime: TextView? = null
        var tvStatus: TextView? = null

        init {
            tvType = mItemView.findViewById(R.id.tv_type)
            tvTime = mItemView.findViewById(R.id.tv_time)
            tvStatus = mItemView.findViewById(R.id.tv_status)
            image_new = mItemView.findViewById(R.id.image_new)

        }
    }

    lateinit var mOnRecordItemClickListener: OnRecordItemClickListener

    interface OnRecordItemClickListener {
        fun onRecordItemClick(recordItemPosition: Int,view: View)
    }

    fun setOnRecordItemClickListener(mOnRecordItemClickListener: OnRecordItemClickListener) {
        this.mOnRecordItemClickListener = mOnRecordItemClickListener
    }

    private fun getDateString(time:String?= "0"):String{
        var timeInt = Integer.parseInt(time)
        return if(timeInt > 3600){
            (timeInt/3600).toString() + "小时" + (timeInt % 3600 / 60 + 0.999).toInt()+"分"
        }else{
            (timeInt % 3600 / 60 + 0.999).toInt().toString()+"分"
        }
    }
}