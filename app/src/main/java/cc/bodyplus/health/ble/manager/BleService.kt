package cc.bodyplus.health.ble.manager

import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.*
import android.util.Log
import cc.bodyplus.health.App
import cc.bodyplus.health.ble.bean.DeviceInfo
import cc.bodyplus.health.ble.bean.MyBleDevice
import cc.bodyplus.health.ble.dfu.DfuHelperS06
import cc.bodyplus.health.ble.utils.BleConst
import cc.bodyplus.health.ble.utils.BleConst.RE_CORE_MODLE
import cc.bodyplus.health.ble.utils.BleConst.RE_DEVICE_DISCONNECT_STATE
import cc.bodyplus.health.ble.utils.BleConst.RE_MSG_DEVICE_INFO
import cc.bodyplus.health.ble.utils.BleConst.RE_SCAN_DEVICE
import cc.bodyplus.health.ble.utils.BleSharedPrefHelperUtils
import cc.bodyplus.health.ble.utils.BleUtils
import cc.bodyplus.health.ble.utils.DfuLocalConfig
import cc.bodyplus.health.ext.tryCatch
import cc.bodyplus.health.utils.SharedPrefHelperUtils
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.ArrayList
import java.util.concurrent.TimeUnit
import kotlin.experimental.or

/**
 * Created by shihoo.wang on 2018/4/20.
 * Email shihu.wang@bodyplus.cc
 */

class BleService : Service(){

    private val mHandler: IncomingHandler by lazy {
        IncomingHandler()
    }

    private val mMessenger: Messenger by lazy {
        Messenger(mHandler)
    }
    private val mBlueToothAdapter: BluetoothAdapter by lazy {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    private var mGattBodyPlus : GattBodyPlus? = null
    private var mClient: Messenger? = null

    private var isDeviceConnection: Boolean = false
    private var mLeDevices: MutableList<MyBleDevice> = ArrayList() // 扫描到所有设备（上衣和裤子）的集合
    private var mDeviceAddress: MutableList<String> = ArrayList() // 扫描到所有设备（上衣和裤子）的物理地址的集合 用来过滤掉重复的设备


    override fun onBind(intent: Intent?): IBinder {
        return mMessenger.binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return Service.START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(blueStateBroadcastReceiver)
    }

    override fun onCreate() {
        super.onCreate()
        registerReceiver(blueStateBroadcastReceiver, IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED))
    }

    private fun clearData() {
        isDeviceConnection = false
        mLeDevices = ArrayList()
        mDeviceAddress = ArrayList()
        mGattBodyPlus?.run {
            disconnect()
        }

    }

    inner  class IncomingHandler : Handler(){
        override fun handleMessage(msg: Message) {
            tryCatch({
                it.printStackTrace()
            }) {
                when (msg.what) {
                    BleConst.MSG_REGEISTER -> mClient = msg.replyTo
                    BleConst.MSG_SEARCH_DEVICE -> startScanBleDevice()
                    BleConst.MSG_DISCONNECT -> disConnect(msg)
                    BleConst.MSG_POWER_LEVEL -> fetchPowerLevel()
                    BleConst.MSG_CORE_MODE -> fetchCoreMode()
                    BleConst.MSG_UPDATE_BLE_START -> coreStartOta()
                    BleConst.MSG_BLE_NAME -> deviceReName(msg)
                    BleConst.MSG_CLEAR_SERVICE -> clearData()
                    BleConst.MSG_AUTO_CONNECT_SN -> autoScanDevice(msg.obj as String)
                    BleConst.MSG_AUTO_CONNECT_DEVICE -> reConnectBySearchDevice(msg)
                    BleConst.MSG_CLOSE_DATA_CHANNEL -> closeDataChannel ()

                }

            }
        }
    }


    private fun disConnect(msg: Message){
        mGattBodyPlus?.disconnect()
        mGattBodyPlus = null
    }

    private fun fetchCoreMode(){
        mGattBodyPlus?.fetchCoreMode()
    }

    private fun fetchPowerLevel(){
        mGattBodyPlus?.fetchPowerLevel()
    }

    private fun coreStartOta(){
        mGattBodyPlus?.startOta()
    }

    private fun deviceReName(msg: Message){
        val name = msg.obj as String
        mGattBodyPlus?.deviceReName(name)
    }

    private fun closeDataChannel() {
        mGattBodyPlus?.closeDataChannel()
    }


    private fun sendMessage(msg: Message) {
        try {
            mClient?.send(msg)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private var mGattCallBack = object : GattCallBack {

        override fun handleMessage(message: Message) {
            sendMessage(message)
        }

        override fun reDisconnect() {
            isDeviceConnection = false
            val msg = Message.obtain(null, RE_DEVICE_DISCONNECT_STATE)
            sendMessage(msg)
            mGattBodyPlus = null
        }

        override fun reDeviceInfo(deviceInfo: DeviceInfo) {
            isDeviceConnection = true
            val msg = Message.obtain(null, RE_MSG_DEVICE_INFO)
            msg.obj = deviceInfo
            sendMessage(msg)
        }

        override fun reCoreModle(stateValue: ByteArray) {
            if (isDeviceConnection) {
                val msg = Message.obtain(null, RE_CORE_MODLE)
                msg.obj = stateValue
                sendMessage(msg)
            }
        }


        override fun handleLogData(data: ByteArray) {
        }

    }

    private fun startScanBleDevice(){
        destroyBleStatusDisposable()
        mHandler.postDelayed({
            mBlueToothAdapter.stopLeScan(mLeScanCallback) // 3秒后结束扫描
            // 通知界面扫描结束 并传递数据
            if (mLeDevices.size > 0) {
                val msg = Message.obtain(null, RE_SCAN_DEVICE)
                msg.arg1 = 0
                msg.obj = mLeDevices
                sendMessage(msg)
            } else {
                // 通知没有扫描到
                val msg = Message.obtain(null, RE_SCAN_DEVICE)
                msg.arg1 = 2
                sendMessage(msg)
            }
        }, (3 * 1000).toLong())
        mLeDevices = ArrayList()
        if (mDeviceAddress.size > 0) {
            mDeviceAddress.clear()
        }
        Thread(Runnable {
            //                mBlueToothAdapter.startLeScan(serviceUuids,mLeScanCallback);
            mBlueToothAdapter.startLeScan(mLeScanCallback)
        }).start()
    }



    private val mLeScanCallback = BluetoothAdapter.LeScanCallback { device, rssi, scanRecord ->
        device?.let {
            if (mDeviceAddress.size > 0 && mDeviceAddress.contains(device.address)) {
                return@LeScanCallback
            }
            val recodeArray = BleUtils.parseFromBytes(scanRecord)
            //根据厂商ID获取对应的信息（0xffff 默认厂商ID  信息为SN码）
            val b = recodeArray.get(0xffff)
            b?.let {
                val deviceSn = BleUtils.byteToChar(it)
                if (deviceSn.length >= 10 && deviceSn.startsWith("M2")) { // S06产品
                    var sn = ""
                    var hw = "6"
                    var hasHwInfo = false
                    if (deviceSn.length == 10) {
                        sn = deviceSn
                    } else if (deviceSn.length == 12) {
                        sn = deviceSn.substring(0, 10)
                        val s = it[11].toInt()
                        val t = (s shl 8).toShort()
                        hw = (t or it[10].toShort()).toString()
                        hasHwInfo = true
                    }
                    val myBleDevice = MyBleDevice()
                    myBleDevice.macAddress = device.address
                    myBleDevice.deviceSn = sn
                    myBleDevice.rssi = rssi
                    myBleDevice.deviceHw = hw
                    myBleDevice.hasHwInfo = hasHwInfo
                    myBleDevice.deviceName = device.name
                    myBleDevice.bluetoothDevice = device
                    myBleDevice.isDfuStatus = BleUtils.isFilterDFUUUID(scanRecord)
                    mLeDevices.add(myBleDevice)
                    mDeviceAddress.add(device.address)
                }
            }
        }
    }


    /**
     *  *********************************** 实现自动重连的目标 *****************************************
     */

    private var mReconnectSn : String ?= null
    private var isReScaned : Boolean = false
    private var subscribe: Disposable? = null

    private fun autoScanDevice(sn: String){
        Log.w("wsh", "自动搜索 sn :" + sn)
        mReconnectSn = sn
        isReScaned = false
        startBleStatusDisposable()
        Thread(Runnable { mBlueToothAdapter.startLeScan(mReConnectLeScanCallback) }).start()

    }


    private fun reConnectBySearchDevice(msg: Message){
        destroyBleStatusDisposable()
        val device = msg.obj as MyBleDevice
        if (device.isDfuStatus) {
            val deviceInfo = DeviceInfo()
            deviceInfo.sn = device.deviceSn
            deviceInfo.hwVn = device.deviceHw

            if (device.hasHwInfo) {
                val updateDfu = DfuLocalConfig.checkUpdateDfu(Integer.parseInt(deviceInfo.hwVn), 0)
                deviceInfo.swVn = "0"
                Log.d("wsh", "重连 dfu状态 升级 App ***** ")
                DfuHelperS06((application as App).getCurrentActivity(), deviceInfo).setZipFile(updateDfu, device.macAddress!!, device.deviceName!!)
            } else {
                val hw = Integer.parseInt(deviceInfo.hwVn)
                val updateDfu = DfuLocalConfig.getNewSwFilePathByHw(hw)
                deviceInfo.swVn = "0"
                Log.d("wsh", "重连 dfu状态 升级 BootLoader ***** ")
                DfuHelperS06((application as App).getCurrentActivity(), deviceInfo).setZipFile(updateDfu, device.macAddress!!, device.deviceName!!)
            }
        } else {
            mReConnectLeScanCallback?.let {
                mBlueToothAdapter.stopLeScan(it)
            }
            val deviceInfo = DeviceInfo()
            deviceInfo.sn = device.deviceSn
            deviceInfo.mac = device.macAddress
            mGattBodyPlus = GattM2(this@BleService, device.bluetoothDevice!!, deviceInfo, mGattCallBack)
            mGattBodyPlus!!.connect()
        }
    }

    private fun reConnectByAutoDevice(device: MyBleDevice) {
        val deviceInfo = BleSharedPrefHelperUtils.getInstance().coreInfo ?: return
        if (device.isDfuStatus) {
            if (device.hasHwInfo) {
                val updateDfu = DfuLocalConfig.checkUpdateDfu(Integer.parseInt(deviceInfo.hwVn), 0)
                deviceInfo.swVn = "0"
                Log.d("wsh", "重连 dfu状态 升级 App ***** ")
                DfuHelperS06((application as App).getCurrentActivity(), deviceInfo).setZipFile(updateDfu, device.macAddress!!, device.deviceName!!)
            } else {
                val hw = Integer.parseInt(deviceInfo.hwVn)
                val updateDfu = DfuLocalConfig.getNewSwFilePathByHw(hw)
                deviceInfo.swVn = "0"
                Log.d("wsh", "重连 dfu状态 升级 BootLoader ***** ")
                DfuHelperS06((application as App).getCurrentActivity(), deviceInfo).setZipFile(updateDfu, device.macAddress!!, device.deviceName!!)
            }
        } else {
            deviceInfo.mac = device.macAddress
            mGattBodyPlus = GattM2(this@BleService, device.bluetoothDevice!!, deviceInfo, mGattCallBack)
            mGattBodyPlus!!.connect()
            mHandler.postDelayed({
                if (!isDeviceConnection) {
                    autoScanDevice(mReconnectSn!!)
                }
            }, (8 * 1000).toLong())
        }
    }

    private fun createDisposable(){
        subscribe = Observable.interval(60, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                //                    .observeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .doOnDispose { }.subscribe({ checkBleStatus() }) { throwable -> throwable.printStackTrace() }
    }

    private fun startBleStatusDisposable() {
        subscribe?.run {
            if (isDisposed){
                createDisposable()
            }
        } ?: let {
            createDisposable()
        }

    }

    private fun destroyBleStatusDisposable(){
        subscribe?.run {
            if (!isDisposed){
                dispose()
            }
        }
    }

    private fun checkBleStatus(){
        if (!isReScaned && !isDeviceConnection) {
            mBlueToothAdapter.stopLeScan(mReConnectLeScanCallback)
            SystemClock.sleep((2 * 1000).toLong())
            if (!isReScaned && !isDeviceConnection) {
                val startLeScan = mBlueToothAdapter.startLeScan(mReConnectLeScanCallback)
                Log.w("wsh", "startLeScan :" + startLeScan)
            }
        } else {
            if (mReConnectLeScanCallback != null) {
                mBlueToothAdapter.stopLeScan(mReConnectLeScanCallback)
            }
            destroyBleStatusDisposable()
        }
    }

    private fun reAutoScanDevice(device: MyBleDevice) {
        mReConnectLeScanCallback?.let {
            mBlueToothAdapter.stopLeScan(mReConnectLeScanCallback)
        }
        destroyBleStatusDisposable()
        reConnectByAutoDevice(device)
    }



    private val mReConnectLeScanCallback = BluetoothAdapter.LeScanCallback { device, rssi, scanRecord ->
        if (!isReScaned) {
            mReconnectSn?.let {
                val recodeArray = BleUtils.parseFromBytes(scanRecord)
                //根据厂商ID获取对应的信息（0xffff 默认厂商ID  信息为SN码）
                val b = recodeArray.get(0xffff)
                b?.let {
                    val deviceSn = BleUtils.byteToChar(it)
                    if (deviceSn.length >= 10 && deviceSn.startsWith("M2")) { // S06产品
                        var sn = ""
                        var hw = "6"
                        var hasHwInfo = false
                        if (deviceSn.length == 10) {
                            sn = deviceSn
                        } else if (deviceSn.length == 12) {
                            sn = deviceSn.substring(0, 10)
                            val s = it[11].toInt()
                            val t = (s shl 8).toShort()
                            hw = (t or it[10].toShort()).toString()
                            hasHwInfo = true
                        }

                        Log.w("wsh", "搜索到 ：sn :" + sn)
                        if (sn == mReconnectSn) {
                            isReScaned = true
                            val myBleDevice = MyBleDevice()
                            myBleDevice.macAddress = device.address
                            myBleDevice.deviceSn = sn
                            myBleDevice.rssi = rssi
                            myBleDevice.deviceHw = hw
                            myBleDevice.hasHwInfo = hasHwInfo
                            myBleDevice.deviceName = device.name
                            myBleDevice.bluetoothDevice = device
                            myBleDevice.isDfuStatus = BleUtils.isFilterDFUUUID(scanRecord)
                            reAutoScanDevice(myBleDevice)
                        }
                    }
                }
            }
        }
    }



    private var blueStateBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0)
            when (blueState) {
                BluetoothAdapter.STATE_ON ->
                    mReconnectSn?.let {
                        autoScanDevice(it)
                    }
//                BluetoothAdapter.STATE_OFF -> coreConnectInterface_Upper.reDisconnect()
            }
        }
    }
}