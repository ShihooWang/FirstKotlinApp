package cc.bodyplus.health.ble.utils

/**
 * Created by shihoo.wang on 2018/4/20.
 * Email shihu.wang@bodyplus.cc
 */
object BleConst{

    const val MSG_REGEISTER = 10 // 创建通信桥梁
    const val MSG_SEARCH_DEVICE = 11 // 搜索设备
    const val MSG_DISCONNECT = 13 // 断开设备连接
    const val MSG_POWER_LEVEL = 14 // 电池电量
    const val MSG_CONNECT_STATE_FlAG = 15 // 连接和标注状态
    const val MSG_GARMENT_STATE = 16 // 连接衣服的信息
    const val MSG_ADJUST_MUSCLE = 17 // 设置肌肉标注值
    const val MSG_CORE_MODE = 18      // Core的位置信息
    const val MSG_DFU = 20           // DFU升级
    const val MSG_BLE_NAME = 23          // 更改设备名称
    const val MSG_UPDATE_BLE_START = 24          //
    const val MSG_UPDATE_BLE_RESULT = 25          //
    const val MSG_CLEAR_SERVICE = 30   // 清除服务中缓存数据
    const val MSG_AUTO_CONNECT_MAC = 33   // 自动重连控制
    const val RE_SCAN_DEVICE = 110 //搜索所有设备
    const val RE_DEVICE_DISCONNECT_STATE = 113 //设备断开
    const val RE_CORE_MODLE = 114 //设备的位置状态
    const val RE_POW_LEVEL = 117 //电量
    const val RE_CORE_CONNECT = 130 // 设备重连
    const val RE_MSG_DEVICE_INFO = 145
    const val RE_MSG_DATA_S06 = 146           // S06数据
    const val MSG_SWITCH_CHANNEL_S06 = 147    // 打开正常采集或者呼吸采集
    const val MSG_CLOSE_CHANNEL_S06 = 148    // 关闭数据通知
    const val MSG_AUTO_CONNECT_SN = 150    // 自动连接的搜索
    const val MSG_AUTO_CONNECT_DEVICE = 151    // 自动连接的搜索
    const val MSG_SWITCH_HEART_MONITOR_S06 = 152    // 打开心率监测
    const val MSG_SWITCH_SLEEP_MONITOR_S06 = 153    // 打开睡眠监测
    const val MSG_OFFLINE_DATA_INF = 38          // S06产品离线模式 读命令 查询当前要读的数据信息 返回时间和长度 8byte
    const val MSG_CLOSE_DATA_CHANNEL = 149 // 关闭数据通知

    const val GAIN = 380f
    const val DATA_LENGTH = 60
    const val WAVE_OUT_ECG_LENGTH = 250
    const val NDK_ORIGIAN_LENGTH = DATA_LENGTH * 500
    const val NDK_DATA_BACK_LENGTH = DATA_LENGTH * WAVE_OUT_ECG_LENGTH
    const val NDK_HEART_BACK_LENGTH = 2
    const val NDK_DESC_BACK_LENGTH = 20
    const val WAVE_OUT_LENGTH = 5
    const val CODE_CLOSE_DRAWERLAYOUT = 1
    const val CODE_BLE_OPEN_CLOSE = 2

    const val ORGINAL_ECG = 500
    const val ORGINAL_ACC = 150
    const val ORGINAL_BREATH = 50

}