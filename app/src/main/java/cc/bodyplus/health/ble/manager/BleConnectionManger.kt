package cc.bodyplus.health.ble.manager

import android.annotation.SuppressLint
import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.os.Messenger
import cc.bodyplus.health.ble.bean.DeviceInfo
import cc.bodyplus.health.ble.bean.MyBleDevice
import cc.bodyplus.health.ble.bean.OriginalFrameData
import cc.bodyplus.health.ble.bean.SimpleFrameData
import cc.bodyplus.health.ble.parse.S06DataParser
import cc.bodyplus.health.ble.utils.BleConst
import cc.bodyplus.health.ble.utils.BleConst.MSG_AUTO_CONNECT_DEVICE
import cc.bodyplus.health.ble.utils.BleConst.MSG_AUTO_CONNECT_SN
import cc.bodyplus.health.ble.utils.BleConst.MSG_CLEAR_SERVICE
import cc.bodyplus.health.ble.utils.BleConst.MSG_CORE_MODE
import cc.bodyplus.health.ble.utils.BleConst.MSG_DISCONNECT
import cc.bodyplus.health.ble.utils.BleConst.MSG_POWER_LEVEL
import cc.bodyplus.health.ble.utils.BleConst.MSG_REGEISTER
import cc.bodyplus.health.ble.utils.BleConst.MSG_SEARCH_DEVICE
import cc.bodyplus.health.ble.utils.BleConst.MSG_UPDATE_BLE_START
import cc.bodyplus.health.ble.utils.BleConst.RE_DEVICE_DISCONNECT_STATE
import cc.bodyplus.health.ble.utils.BleConst.MSG_CLOSE_DATA_CHANNEL
import cc.bodyplus.health.ble.utils.monitor.EcgOriginalData
import cc.bodyplus.health.ext.tryCatch
import java.util.HashMap
import java.util.concurrent.Executors

/**
 * Created by shihoo.wang on 2018/4/20.
 * Email shihu.wang@bodyplus.cc
 */
class BleConnectionManger : ServiceConnection {

    companion object {
        @SuppressLint("StaticFieldLeak")
        var mInstance = BleConnectionManger()
    }
    private val mMessenger: Messenger by lazy {
        Messenger(IncomingHandler())
    }

    private var mService: Messenger? = null
    private var registedView: BleConnectionInterface? = null
    private val connectionMap = HashMap<String, BleConnectionInterface>()
    private var application: Application? = null
    private val executorService = Executors.newSingleThreadExecutor()


    fun init(application: Application) {
        this.application = application
        startBondBleService()
    }

    private fun startBondBleService() {
        val service = Intent(application, BleService::class.java)
        application?.startService(service)
        application?.bindService(service, this, Context.BIND_AUTO_CREATE)
    }


    override fun onServiceConnected(name: ComponentName, service: IBinder) {
        mService = Messenger(service)
        val msg = Message.obtain(null, MSG_REGEISTER)
        msg?.run {
            replyTo = mMessenger
            sendMessage(msg)
        }
    }

    override fun onServiceDisconnected(name: ComponentName) {
        if (stopService) {
            stopService = false
            application?.stopService(Intent(application, BleService::class.java))
        } else {
            startBondBleService()
        }
    }


    private var stopService: Boolean = false


    @Deprecated("")
    private fun clearService() {
        val message = Message.obtain(null, MSG_CLEAR_SERVICE)
        sendMessage(message)
    }


    @Synchronized
    fun addConnectionListener(bleConnectionInterface: BleConnectionInterface, isRegisterBodyData: Boolean) {
        try {
            val clazzName = bleConnectionInterface.javaClass.name
            //  覆盖相同key值的value值
            connectionMap.put(clazzName, bleConnectionInterface)
            if (isRegisterBodyData) {
                registedView = bleConnectionInterface
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    @Synchronized
    fun removeConnectionListener(bleConnectionInterface: BleConnectionInterface) {
        try {
            val clazzName = bleConnectionInterface.javaClass.name
            if (connectionMap.containsKey(clazzName)) {
                connectionMap.remove(clazzName)
            }
            val viewName = registedView?.let {
                it.javaClass.name
            }
            if (viewName.equals(clazzName)) {
                registedView = null
            }
        } catch (e: Exception) {
        }

    }


    private fun sendMessage(msg: Message?) {
        mService?.run {
            executorService.execute{
                tryCatch({
                    it.printStackTrace()
                }) {
                    send(msg)
                }
            }
        }
    }

    fun clearAll() {
        disconnect()
        connectionMap.clear()
        registedView = null
        clearService()
    }


    fun searchDevice() {
        val message = Message.obtain(null, MSG_SEARCH_DEVICE)
        sendMessage(message)
    }


    fun disconnect() {
        val message = Message.obtain(null, MSG_DISCONNECT)
        sendMessage(message)
    }

    fun fetchPowerLevel() {
        val message = Message.obtain(null, MSG_POWER_LEVEL)
        sendMessage(message)
    }

    fun fetchCoreMode() {
        val message = Message.obtain(null, MSG_CORE_MODE)
        sendMessage(message)
    }


    /**
     * 关闭硬件采集 idel状态
     */
    fun closeDataChannel() {
        val message = Message.obtain(null, MSG_CLOSE_DATA_CHANNEL)
        sendMessage(message)
    }

    fun  sendUpdateBleStart(){
        val message = Message.obtain(null, MSG_UPDATE_BLE_START)
        sendMessage(message);
    }

    fun autoConnectBleBySN(sn: String) {
        val message = Message.obtain(null, MSG_AUTO_CONNECT_SN)
        message.obj = sn
        sendMessage(message)
    }

    fun autoConnectBleByDevice(myBleDevice: MyBleDevice) {
        val message = Message.obtain(null, MSG_AUTO_CONNECT_DEVICE)
        message.obj = myBleDevice
        sendMessage(message)
    }


    inner  class IncomingHandler : Handler(){
        override fun handleMessage(msg: Message) {
            tryCatch({
                it.printStackTrace()
            }) {
                if (connectionMap.size > 0) {
                    when (msg.what) {
                        RE_DEVICE_DISCONNECT_STATE -> {
                            msgDeviceDisconnect()
                            return@tryCatch
                        }
                        BleConst.RE_CORE_CONNECT -> {
                            msgReConnectDevice(msg)
                            return@tryCatch
                        }
                        BleConst.RE_POW_LEVEL -> {
                            msgPowerLevel(msg)
                            return@tryCatch
                        }
                        BleConst.RE_CORE_MODLE -> {
                            msgCoreModule(msg)
                            return@tryCatch
                        }
                        BleConst.RE_MSG_DEVICE_INFO -> {
                            msgReConnectDevice(msg)
                            return@tryCatch
                        }
//                        BleConst.RE_MSG_DATA_S06 -> {
//                            S06DataParser.parserData(msg.obj as ByteArray, parserCallBack)
//                            return
//                        }
                    }
                    val iter = connectionMap.entries.iterator()
                    while (iter.hasNext()) {
                        val entry = iter.next() as MutableMap.MutableEntry<*, *>
                        val connectionInterface = entry.value as BleConnectionInterface
                        connectionInterface.bleDispatchMessage(msg)
                    }
                }
                if (msg.what == BleConst.RE_MSG_DATA_S06){
                    S06DataParser.parserData(msg.obj as ByteArray, parserCallBack)
                }
            }
        }
    }

    private fun msgCoreModule(msg: Message) {
        val iter = connectionMap.entries.iterator()
        while (iter.hasNext()) {
            val entry = iter.next() as MutableMap.MutableEntry<*, *>
            val connectionInterface = entry.value as BleConnectionInterface
            connectionInterface.bleCoreModule((msg.obj as ByteArray)[0] )
        }
    }

    private fun msgPowerLevel(msg: Message) {
        val iter = connectionMap.entries.iterator()
        while (iter.hasNext()) {
            val entry = iter.next() as MutableMap.MutableEntry<*, *>
            val connectionInterface = entry.value as BleConnectionInterface
            connectionInterface.blePowerLevel((msg.obj as ByteArray)[0] )
        }
    }

    private fun msgReConnectDevice(msg: Message) {
        val iter = connectionMap.entries.iterator()
        while (iter.hasNext()) {
            val entry = iter.next() as MutableMap.MutableEntry<*, *>
            val connectionInterface = entry.value as BleConnectionInterface
            connectionInterface.reDeviceInfo(msg.obj as DeviceInfo)
        }
    }

    private fun msgDeviceDisconnect() {
        val iter = connectionMap.entries.iterator()
        while (iter.hasNext()) {
            val entry = iter.next() as MutableMap.MutableEntry<*, *>
            val connectionInterface = entry.value as BleConnectionInterface
            connectionInterface.bleDeviceDisconnect()
        }
    }

    private val parserCallBack = object : S06DataParser.S06DataParserCallBack {

        override fun frameData(originalFrameData: OriginalFrameData, status: Int) {
            EcgOriginalData.mInstance.handleFrameData(originalFrameData,status,registedView)
            if (originalFrameData.coreModel != 0x00.toByte()){
                registedView?.bleCoreModule(originalFrameData.coreModel)
            }
            if (originalFrameData.powerLevel != 0){
                registedView?.blePowerLevel(originalFrameData.powerLevel.toByte())
            }

        }

        override fun simpleFrameData(simpleFrameData: SimpleFrameData) {
            if (simpleFrameData.coreModel != 0x00.toByte()){
                registedView?.bleCoreModule(simpleFrameData.coreModel)
            }
            if (simpleFrameData.powerLevel != 0){
                registedView?.blePowerLevel(simpleFrameData.powerLevel.toByte())
            }
        }

    }


}