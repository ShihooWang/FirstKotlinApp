package cc.bodyplus.health.mvp.view.equipment

import android.app.ProgressDialog
import android.os.Message
import android.view.KeyEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.AdapterView
import android.widget.ImageView
import cc.bodyplus.health.R
import cc.bodyplus.health.ble.bean.DeviceInfo
import cc.bodyplus.health.ble.bean.MyBleDevice
import cc.bodyplus.health.ble.manager.BleConnectionInterface
import cc.bodyplus.health.ble.manager.BleConnectionManger
import cc.bodyplus.health.ble.utils.BleConst.RE_SCAN_DEVICE
import cc.bodyplus.health.ble.utils.BleSharedPrefHelperUtils
import cc.bodyplus.health.mvp.view.BaseActivity
import cc.bodyplus.health.mvp.view.main.BleDevicesListsAdapter
import cc.bodyplus.health.utils.SharedPrefHelperUtils
import kotlinx.android.synthetic.main.activity_equipment_bond.*
import kotlinx.android.synthetic.main.equipment_search_result.*
import java.util.*

/**
 * Created by shihoo.wang on 2018/4/20.
 * Email shihu.wang@bodyplus.cc
 */
class EquipmentBondActivity : BaseActivity(), BleConnectionInterface {

    companion object {
        val BOND_SUCCEED : Int = 1
        val BOND_CANCEL: Int = 2
        val SEARCH_FAIL: Int = 3
    }

    override fun initInject() {
    }

    override fun layoutId(): Int {
        return R.layout.activity_equipment_bond
    }

    override fun initData() {
    }

    private val adapter: BleDevicesListsAdapter by lazy {
        BleDevicesListsAdapter(this)
    }
    private val progressDialog: ProgressDialog by lazy {
        ProgressDialog(this,R.style.dialog)
    }

    private var isBinding: Boolean = false
    private var MyBleDeviceLists : ArrayList<MyBleDevice> ?= null
    private var mSn = ""

    override fun initView() {
        BleConnectionManger.mInstance.addConnectionListener(this, false)
        search_result_list.adapter = adapter //  要求加上 信号强度
        search_result_list.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val myBleDevice = MyBleDeviceLists?.get(position)
            bondingDevice(myBleDevice!!)
        }
        val myBleDeviceLists = intent.getSerializableExtra("deivesList") as ArrayList<MyBleDevice>
        showDevices(myBleDeviceLists)
        bond_ignore.setOnClickListener {
            setResult(BOND_CANCEL)
            finish()
        }
    }

    private fun bondingDevice(myBleDevice: MyBleDevice) {
        BleSharedPrefHelperUtils.getInstance().coreInfo = null
        BleConnectionManger.mInstance.autoConnectBleByDevice(myBleDevice)
        mSn = myBleDevice.deviceSn!!
        isBinding = true
        progressDialog.setMessage("连接中...")
        progressDialog.show()
        progressDialog.setCancelable(false)
        mHandler.postDelayed(checkedR, 15 * 1000)
    }

    internal var checkedR: Runnable = Runnable {
        progressDialog?.run {
            if (isShowing){
                dismiss()
            }
        }
    }


    private fun showDevices(myBleDeviceLists : ArrayList<MyBleDevice>){
        MyBleDeviceLists = myBleDeviceLists
        stopRefreshDevicesAnimation(iv_flush)
        Collections.sort(myBleDeviceLists) {
            o1, o2 -> if (o1 is MyBleDevice && o2 is MyBleDevice) {
                o2.rssi - o1.rssi
            } else -1
        }
        adapter.setData(myBleDeviceLists)
        // 5秒之后再次搜索刷新列表
        mHandler.postDelayed(reSearchRunnable, 5000)
    }

    private val reSearchRunnable = Runnable {
        if (!isBinding) {
            startRefreshDevicesAnimation(iv_flush)
            BleConnectionManger.mInstance.searchDevice()
        }
    }

    private fun startRefreshDevicesAnimation(imageView: ImageView) {
        imageView.visibility = View.VISIBLE
        imageView.setImageResource(R.drawable.equipment_research)
        val animation = RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        animation.duration = 1000
        val lin = LinearInterpolator()
        animation.interpolator = lin
        animation.repeatCount = Animation.INFINITE
        imageView.startAnimation(animation)
    }

    private fun stopRefreshDevicesAnimation(imageView: ImageView) {
        imageView.visibility = View.INVISIBLE
        imageView.clearAnimation()
    }

    private fun showDeviceList(result: Int, lists: ArrayList<MyBleDevice>) {
        when (result) {
            0  -> {
                showDevices(lists)
            }
            2  -> {
                // 没有搜索到  finis
                setResult(BOND_CANCEL)
                finish()
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        setResult(BOND_CANCEL)
        return super.onKeyDown(keyCode, event)
    }

    override fun onDestroy() {
        super.onDestroy()
        removeCallbacks(reSearchRunnable)
        removeCallbacks(checkedR)
    }

    override fun bleDispatchMessage(msg: Message) {
        when (msg.what) {
            RE_SCAN_DEVICE -> {
                val result = msg.arg1
                msg.obj?.let {
                    val myBleDevices = it as ArrayList<MyBleDevice>
                    showDeviceList(result, myBleDevices)
                } ?: let {
                    setResult(BOND_CANCEL)
                    finish()
                }
            }
        }
    }

    override fun blePowerLevel(data: Byte) {

    }

    override fun reDeviceInfo(deviceInfo: DeviceInfo) {
        progressDialog?.run {
            if (isShowing){
                dismiss()
            }
        }
        setResult(BOND_SUCCEED)
        finish()
    }

    override fun bleDeviceDisconnect() {
    }

    override fun bleCoreModule(data: Byte) {
    }

    override fun heartBreathData(heart: Int, breath: Int) {

    }
}