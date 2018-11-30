package cc.bodyplus.health.ble.manager

import android.os.Message
import cc.bodyplus.health.ble.bean.DeviceInfo

/**
 * Created by shihoo.wang on 2018/4/20.
 * Email shihu.wang@bodyplus.cc
 */

interface GattCallBack{

    fun handleMessage(message: Message)
    fun reDeviceInfo(deviceInfo: DeviceInfo)
    fun reCoreModle(stateValue: ByteArray)
    fun reDisconnect()
    fun handleLogData(data: ByteArray)

}