package cc.bodyplus.health.ble.dfu

import android.app.Activity
import cc.bodyplus.health.R
import cc.bodyplus.health.ble.utils.BleDialogUtil
import cc.bodyplus.health.ext.tryCatch

/**
 * Created by shihoo.wang on 2018/4/24.
 * Email shihu.wang@bodyplus.cc
 *
 *  固件更新时显示更新信息的帮助类
 *
 */
class CoreUpdateHelper(private val activity : Activity, private val onBleUpdateDialogConfirmClick: OnBleUpdateDialogConfirmClick ){


    fun setFwUpdate(message : String){
        tryCatch({
            it.printStackTrace()
        }) {
            val bleDialogUtil = BleDialogUtil()
            val cancelable = false
            val title = activity.getResources().getString(R.string.equipment_ble_update_fw)
            bleDialogUtil.showDialog(activity, title, message, object : BleDialogUtil.BleDialogClickListener {
                override fun onConfirmBtnClick() {
                    onBleUpdateDialogConfirmClick.onClick()
                }
            }, cancelable)
        }
    }

    interface OnBleUpdateDialogConfirmClick{
        fun onClick()
    }
}