package cc.bodyplus.health.ble.bean

import java.io.Serializable

/**
 * Created by shihoo.wang on 2018/4/20.
 * Email shihu.wang@bodyplus.cc
 */
class OriginalFrameData : Serializable {
    /**
     * bit0	总步数	    N	    触发	    4	        4	    N
     * bit1	充电状态	    N	    触发	    1	        1	    Y
     * bit2	电池电量	    N	    0.1	    1	        -	    N
     * bit3	脱落状态	    N	    触发	    1	        1	    N
     * bit4	心跳  	    N	    1/30	1	        -	    Y
     * bit5	Core位置	    N	    触发	    1	        1	    Y
     * bit6	第二扩展ID	N	    触发	    1	        1	    N
     * bit7	步频	        N	    0.1	    2	        -	    N
     */
    var ecg: IntArray? = null    // 心电的原始数据
    var acc: IntArray? = null      // 加速度原始数据
    var breath: IntArray? = null    // 呼吸的原始数据
    var frameCount: Int = 0       // 帧计数
    var status: Int = 0      // 每秒的状态 正常/异常 用来循环记录正常一段波形的标示
    var totalStep: Int = 0       // 总步数
    var chargePowerStatus: Int = 0       // 充电状态
    var powerLevel: Int = 0       // 电池电量
    var leadOffStatus: Int = 0       // 脱落状态
    var heartBeat: Int = 0 // 心跳
    var coreModel: Byte = 0       // Core位置
    var stepFrequen: Int = 0       // 步频


}