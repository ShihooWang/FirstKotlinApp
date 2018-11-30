package cc.bodyplus.health.mvp.view.login

import cc.bodyplus.health.R
import cc.bodyplus.health.ble.utils.BleSharedPrefHelperUtils
import cc.bodyplus.health.ble.utils.BleUtils
import cc.bodyplus.health.mvp.view.BaseActivity
import cc.bodyplus.health.utils.SharedPrefHelperUtils
import kotlinx.android.synthetic.main.activity_device_info.*

/**
 * Created by rui.gao on 2018-05-09.
 */
class DeviceInfoActivity : BaseActivity() {
    override fun initInject() {
    }

    override fun layoutId(): Int = R.layout.activity_device_info

    override fun initData() {
    }

    override fun initView() {
        title_back.setOnClickListener { onBackPressed() }
        BleSharedPrefHelperUtils.getInstance().coreInfo?.let {
            tv_version.text = "v"+BleUtils.generateVersion(it.hwVn)
            tv_gj.text = "v"+BleUtils.generateVersion(it.swVn)
            tv_sn.text = it.sn
        }?:let {
            tv_version.text = ""
            tv_gj.text = ""
            tv_sn.text = ""
        }
    }
}