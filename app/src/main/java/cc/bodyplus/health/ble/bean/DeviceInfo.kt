package cc.bodyplus.health.ble.bean

import java.io.Serializable

/**
 * Created by shihoo.wang on 2018/4/20.
 * Email shihu.wang@bodyplus.cc
 */
class DeviceInfo : Serializable {

    var sn: String? = null

    var mac: String? = null
    var bleName: String? = null
    var hwVn: String? = null
    var swVn: String? = null

    @Deprecated("")
    var bleVn: String? = null
}