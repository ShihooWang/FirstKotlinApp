package cc.bodyplus.health.ble.manager

import android.os.Message
import cc.bodyplus.health.ble.bean.DeviceInfo

/**
 * Created by shihoo.wang on 2018/4/20.
 * Email shihu.wang@bodyplus.cc
 */
interface BleConnectionInterface{

    /**
     * 事件分发
     */
    fun bleDispatchMessage(msg : Message)

    /**
     * 电量读取的返回、电量改变的回调
     * @param data 电量值
     */
    fun blePowerLevel(data: Byte)


    fun reDeviceInfo(deviceInfo: DeviceInfo)

    /**
     * Core断开连接的回调
     */
    fun bleDeviceDisconnect()

    /**
     * Core位置变化的回调（主动触发）
     * byte[] value  说明：
     * value[0] == 00  充电座
     * value[0] == 01  上衣
     * value[0] == 10  裤子
     * value[0] == 11  独立
     */
    fun bleCoreModule(data: Byte)

//    fun frameData(originalFrameData: OriginalFrameData, status: Int)

//    fun simpleFrameData(simpleFrameData: SimpleFrameData)

    fun heartBreathData(heart : Int , breath : Int=0)

//    fun
}