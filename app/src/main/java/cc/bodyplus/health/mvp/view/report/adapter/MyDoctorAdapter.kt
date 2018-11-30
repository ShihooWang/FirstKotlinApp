package cc.bodyplus.health.mvp.view.report.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import cc.bodyplus.health.R
import cc.bodyplus.health.mvp.module.DoctorBean
import de.hdodenhof.circleimageview.CircleImageView

/**
 * Created by rui.gao on 2018-05-09.
 */
class MyDoctorAdapter (var mList: ArrayList<DoctorBean>, var mContext: Context): BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var holder: ViewHolder
        var v: View
        if (convertView == null) {
            holder = ViewHolder()
            v = View.inflate(mContext, R.layout.item_my_doctor, null)
            holder.civ_avatar = v.findViewById(R.id.civ_avatar)
            holder.tv_Department = v.findViewById(R.id.tv_Department)
            holder.tv_name = v.findViewById(R.id.tv_name)
            holder.tv_Department_manage = v.findViewById(R.id.tv_Department_manage)
            holder.tv_address = v.findViewById(R.id.tv_address)
            v.tag = holder
        } else {
            v = convertView
            holder = v.tag as ViewHolder
        }
        holder.tv_name.text = mList[position].name
        holder.tv_Department.text = mList[position].dept
        holder.tv_Department_manage.text =  mList[position].duties
        holder.tv_address.text = mList[position].hospital
        return v
    }

    fun addData(dataList: ArrayList<DoctorBean>){
        mList.clear()
        mList.addAll(dataList)
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
        lateinit var civ_avatar: CircleImageView
        lateinit var tv_name: TextView
        lateinit var tv_Department: TextView
        lateinit var tv_Department_manage: TextView
        lateinit var tv_address: TextView
    }
}