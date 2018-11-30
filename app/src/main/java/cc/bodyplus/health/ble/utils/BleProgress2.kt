package cc.bodyplus.health.ble.utils

import android.app.Activity
import android.app.AlertDialog
import android.widget.TextView
import cc.bodyplus.health.R

/**
 * Created by shihoo.wang on 2018/4/24.
 * Email shihu.wang@bodyplus.cc
 */
class BleProgress2 {
    private var tvMessage: TextView? = null
    private var tvProgress:TextView? = null
    private var bleProgressBar: BleProgressBar? = null
    private var dialog: AlertDialog? = null

    fun showDialog(activity: Activity) {
        dialog = AlertDialog.Builder(activity, R.style.bleDfuDialog).create()
        dialog!!.show()
        val window = dialog!!.window
        window!!.setContentView(R.layout.layout_dialog_ble_progress2)
        //设置dialog的弹窗类型
        tvMessage = window.findViewById<TextView>(R.id.tv_message) as TextView
        tvProgress = window.findViewById<TextView>(R.id.tv_progress) as TextView
        bleProgressBar = window.findViewById<BleProgressBar>(R.id.progress_bar) as BleProgressBar
        dialog!!.setCancelable(false)

        bleProgressBar!!.setMaxCount(100)
    }

    fun setMessage(message: String) {
        if (tvMessage != null) {
            tvMessage!!.text = message
        }
    }

    fun disMiss() {
        try {
            if (dialog != null && dialog!!.isShowing) {
                bleProgressBar = null
                dialog!!.dismiss()
            }
        }catch ( e: Exception){
            e.printStackTrace()
        }
    }

    fun setProgress(progress: Int) {
        tvProgress!!.text = progress.toString() + "%"
        bleProgressBar!!.setCurrentCount(progress)
    }
}