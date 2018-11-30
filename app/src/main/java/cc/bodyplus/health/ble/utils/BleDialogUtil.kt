package cc.bodyplus.health.ble.utils

import android.app.Activity
import android.app.AlertDialog
import android.widget.TextView
import cc.bodyplus.health.R

/**
 * Created by shihoo.wang on 2018/4/24.
 * Email shihu.wang@bodyplus.cc
 */
class BleDialogUtil {

    fun showDialog(activity: Activity, title: String, content: String, listener: BleDialogClickListener, cancelable: Boolean) {
        val dialog = AlertDialog.Builder(activity, R.style.bleDfuDialog).create()
        dialog.show()
        val window = dialog.window
        window.setContentView(R.layout.layout_dialog_ble)
        //设置dialog的弹窗类型
        val inputEdit = window.findViewById<TextView>(R.id.edit_input) as TextView
        val titleTv = window.findViewById<TextView>(R.id.input_title) as TextView
        val confirmBtn = window.findViewById<TextView>(R.id.dialog_confirm_btn) as TextView
        titleTv.text = title
        inputEdit.text = content
        confirmBtn.setOnClickListener {
            listener.onConfirmBtnClick()
            dialog.dismiss()
        }
        dialog.setCancelable(cancelable)
    }

    interface BleDialogClickListener {
        fun onConfirmBtnClick()
    }
}