package cc.bodyplus.health.ble.ota

import android.app.Activity
import no.nordicsemi.android.dfu.DfuBaseService

/**
 * Created by shihoo.wang on 2018/4/24.
 * Email shihu.wang@bodyplus.cc
 */
class OTAService : DfuBaseService() {

    override fun getNotificationTarget(): Class<out Activity> {
        return NotificationActivity::class.java
    }

    override fun isDebug(): Boolean {
        return true
    }
}