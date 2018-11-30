package cc.bodyplus.health.ble.manager

import android.bluetooth.*
import android.content.Context
import android.os.Build
import android.os.Message
import android.os.SystemClock
import android.support.annotation.RequiresApi
import android.util.Log
import cc.bodyplus.health.ble.bean.DeviceInfo
import cc.bodyplus.health.ble.parse.BleCmdConfig
import cc.bodyplus.health.ble.parse.M2CmdConfig
import cc.bodyplus.health.ble.parse.M2MegicType.*
import cc.bodyplus.health.ble.utils.*
import cc.bodyplus.health.ble.utils.BleConst.RE_MSG_DATA_S06
import cc.bodyplus.health.ble.utils.BleConst.RE_POW_LEVEL
import cc.bodyplus.health.ext.tryCatch
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.Executors
import kotlin.experimental.and
import kotlin.experimental.or

/**
 * Created by shihoo.wang on 2018/4/20.
 * Email shihu.wang@bodyplus.cc
 *
 * 医疗版M2产品 BLE 连接实例
 */
class GattM2 (private val mContext: Context,
              private val mBluetoothDevice: BluetoothDevice,
              private var mDeviceInfo: DeviceInfo,
              private val gattCallBack: GattCallBack): GattBodyPlus(){

    private var mBluetoothGatt : BluetoothGatt ? = null
    private var cmdService: BluetoothGattService? = null
    private var dataService:BluetoothGattService? = null
    private var cmdWriteCharacter: BluetoothGattCharacteristic? = null
    private var cmdRespondCharacter:BluetoothGattCharacteristic? = null
    private var logReadCharacter:BluetoothGattCharacteristic? = null
    private var dataWriteCharacter: BluetoothGattCharacteristic? = null
    private var dataRespondCharacter:BluetoothGattCharacteristic? = null

    private val sWriteQueue : ConcurrentLinkedQueue<BleWriteData> by lazy {
        ConcurrentLinkedQueue<BleWriteData>()
    }

    private var sIsWriting = false
    private var latSendData: BleWriteData? = null
    private var reSendData: BleWriteData? = null
    private var timeOutNum = 0
    private var timeOutTime = 0
    private var timeOutThread_Start = false
    private var timeOutThread: TimeOutThread? = null
    private val errorCount = 0//数据发送失败次数
    private val ERROR_FLAG_SUCCESS: Byte = 0
    private var isDeviceConnection: Boolean = false//设备连接是否成功
    private val executorService = Executors.newSingleThreadExecutor()
    private var mConnectionPriorityOperationInProgress = false


    override fun closeThread(){
        timeOutThread_Start = false
    }

    override fun connect() {
        mBluetoothGatt = mBluetoothDevice.connectGatt(mContext, false, mBluetoothUpperCallback)
    }

    override fun disconnect() {
        tryCatch ({
            it.printStackTrace()
        }){
            if (mBluetoothGatt != null) {
                if (isDeviceConnection) {
                    mBluetoothGatt?.disconnect()
                } else {
                    val isrefresh = refreshDeviceCache()
                    mBluetoothGatt?.close()
                }
            }
        }
    }

    override fun startOta() {
        cmdWriteCmd(M2CmdConfig.BLE_CMD_DFU_JUMP_LENGTH, byteArrayOf(1))
    }

    override fun closeDataChannel() {
        enableNotification(false, mBluetoothGatt, dataRespondCharacter)
        nextWrite()
    }

    override fun fetchPowerLevel() {
        readPowerLevel()
    }


    private fun disConnectDevice() {
        mBluetoothGatt?.close()
        gattCallBack.reDisconnect()
        sWriteQueue.clear()
        sIsWriting = false
        closeThread()

        mBluetoothGatt = null
        cmdService = null
        dataService = null

        cmdWriteCharacter = null
        cmdRespondCharacter = null
        logReadCharacter = null

        dataWriteCharacter = null
        dataRespondCharacter = null

    }

    private fun sendMessage(message: Message) {
        gattCallBack.handleMessage(message)
    }


    private fun reDeviceInfo(info: DeviceInfo) {

        setDeviceTime()
        isDeviceConnection = true
        gattCallBack.reDeviceInfo(info)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Thread(Runnable {
                SystemClock.sleep(5000)
                requestConnectionPriority(BluetoothGatt.CONNECTION_PRIORITY_HIGH)
                mConnectionPriorityOperationInProgress = true
                SystemClock.sleep(500)
                enableDataProfile()
            }).start()
        }else{
            enableDataProfile()
        }

    }






    private val mBluetoothUpperCallback = object : BluetoothGattCallback() {

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            //            List<BluetoothDevice> connectedDevices = gatt.getConnectedDevices();
            val device = gatt.device
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                sWriteQueue.clear()
                sIsWriting = false
                mBluetoothGatt?.discoverServices()
            } else {
                if (isDeviceConnection) {
                    isDeviceConnection = false
                    disConnectDevice()
                }
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                cmdService = mBluetoothGatt?.getService(UUIDUtils.CMD_SERVICE)
                cmdRespondCharacter = cmdService?.getCharacteristic(UUIDUtils.CMD_READ_CHARACTERISTIC)
                cmdWriteCharacter = cmdService?.getCharacteristic(UUIDUtils.CMD_WRITE_CHARACTERISTIC)
                logReadCharacter = cmdService?.getCharacteristic(UUIDUtils.CMD_LOG_READ_CHARACTERISTIC)

                dataService = mBluetoothGatt?.getService(UUIDUtils.DATA_SERVICE)
                dataRespondCharacter = dataService?.getCharacteristic(UUIDUtils.DATA_READ_CHARACTERISTIC)
                dataWriteCharacter = dataService?.getCharacteristic(UUIDUtils.DATA_WRITE_CHARACTERISTIC)

                //开启事件订阅
                enableNotification(true, mBluetoothGatt, cmdRespondCharacter)
                enableNotification(true, mBluetoothGatt, logReadCharacter)
//                enableDataProfile()
                nextWrite()
                if (mDeviceInfo.hwVn == null || mDeviceInfo.hwVn?.length!! < 1) {
                    readHwVersion()
                } else {
                    reDeviceInfo(mDeviceInfo)
                }
            }
        }

        override fun onCharacteristicRead(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {

        }

        override fun onCharacteristicWrite(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {}

        override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
            val data = characteristic.value

            val uuid = characteristic.uuid
            if (uuid == UUIDUtils.DATA_READ_CHARACTERISTIC) {
                Log.i("wsh", "收到数据 value ---  " + BleUtils.dumpBytes(data))
                handleDataAvailable(data)
            } else if (uuid == UUIDUtils.CMD_LOG_READ_CHARACTERISTIC) { // 设备日志上传
                //                gattCallBack.handleLogData(value);
            } else if (uuid == UUIDUtils.OFFLINE_READ_CHARACTERISTIC) { // 离线数据上传
            } else {
                if (TLUtil.validateCRC8(data)) { // 通知返回码 包括命令码返回 STM码 以及数据通道
                    handleCharacteristicData(data)
                }
            }
        }

        override fun onDescriptorRead(gatt: BluetoothGatt, descriptor: BluetoothGattDescriptor, status: Int) {}

        override fun onDescriptorWrite(gatt: BluetoothGatt, descriptor: BluetoothGattDescriptor, status: Int) {
            sIsWriting = false
            nextWrite()
        }


        /**
         * Build.Build.VERSION_CODES.O 版本SDK回调
         */
        fun onConnectionUpdated(gatt: BluetoothGatt, interval: Int, latency: Int, timeout: Int,
                                status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
            }
            if (mConnectionPriorityOperationInProgress) {
                mConnectionPriorityOperationInProgress = false
                nextWrite()
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    fun requestConnectionPriority(priority: Int) {
        if (mBluetoothGatt != null) {
            val requestConnectionPriority = mBluetoothGatt?.requestConnectionPriority(priority)
        }
    }


    private fun handleCharacteristicData(data: ByteArray) {
        if (BleUtils.isCMDReadResponse(data)) {
            handleCMDReadResponse(data)
        } else if (BleUtils.isCMDWriteResponse(data)) {
            handleCMDWriteResponse(data)
        } else {
            succeedReceiveCmd()
        }
    }

    private fun handleBattery(data: ByteArray) {
        val msg = Message.obtain(null, RE_POW_LEVEL)
        msg.obj = data
        sendMessage(msg)
    }


    private fun handleCMDReadResponse(data: ByteArray) {
        if (BleUtils.isCurrentCommandData(data, BleCmdConfig.BLE_CMD_HW_VN)) { // 获取到 硬件版本号
            checkCmdReturn(BleCmdConfig.BLE_CMD_HW_VN)
            if (!BleUtils.isCMDReadError(data, BleCmdConfig.getPayloadLengthByCommand(BleCmdConfig.BLE_CMD_HW_VN))) {
                val value = ByteArray(BleCmdConfig.getPayloadLengthByCommand(BleCmdConfig.BLE_CMD_HW_VN).toInt())
                System.arraycopy(data, 4, value, 0, value.size)

                val s = value[1].toInt()
                val t = (s shl 8).toShort()
                val hw = (t or value[0].toShort()).toShort()
                mDeviceInfo.hwVn = hw.toString()
                readSwVersion()
            } else {
                errorReceiveCmd_Upper(BleCmdConfig.BLE_CMD_HW_VN)
            }
        } else if (BleUtils.isCurrentCommandData(data, BleCmdConfig.BLE_CMD_SW_VN)) { //  获取到 固件版本号
            checkCmdReturn(BleCmdConfig.BLE_CMD_SW_VN)
            if (!BleUtils.isCMDReadError(data, BleCmdConfig.getPayloadLengthByCommand(BleCmdConfig.BLE_CMD_SW_VN))) {
                val value = ByteArray(BleCmdConfig.getPayloadLengthByCommand(BleCmdConfig.BLE_CMD_SW_VN).toInt())
                System.arraycopy(data, 4, value, 0, value.size)
                val sw = (value[1].toInt() shl 8 or (value[0] and 0xff.toByte()).toInt()).toShort()
                mDeviceInfo.swVn = sw.toString()
                reDeviceInfo(mDeviceInfo)
            } else {
                errorReceiveCmd_Upper(BleCmdConfig.BLE_CMD_SW_VN)
            }
        } else if (BleUtils.isCurrentCommandData(data, M2CmdConfig.BLE_CMD_DFU_JUMP_LENGTH)) { // 升级跳转
            checkCmdReturn(M2CmdConfig.BLE_CMD_DFU_JUMP_LENGTH)
            if (!BleUtils.isCMDReadError(data, M2CmdConfig.getPayloadLengthByCommand(M2CmdConfig.BLE_CMD_DFU_JUMP_LENGTH))) {
                val value = ByteArray(M2CmdConfig.getPayloadLengthByCommand(M2CmdConfig.BLE_CMD_DFU_JUMP_LENGTH).toInt())
                System.arraycopy(data, 4, value, 0, value.size)
            } else {
                errorReceiveCmd_Upper(M2CmdConfig.BLE_CMD_DFU_JUMP_LENGTH)
            }
        }  else if (BleUtils.isCurrentCommandData(data, M2CmdConfig.BLECMD_CORE_TYPE)) { //  Core工作状态
            checkCmdReturn(M2CmdConfig.BLECMD_CORE_TYPE)
            val value = ByteArray(1)
            System.arraycopy(data, 4, value, 0, value.size)
//            val msg = Message.obtain(null, MSG_OFFLINE_WORK_STATUS)
//            msg.obj = value[0]
//            sendMessage(msg)
        } else if (BleUtils.isCurrentCommandData(data, M2CmdConfig.BLE_CMD_SLEEP_THRESHOLD)) { //  睡眠阈值1
            checkCmdReturn(M2CmdConfig.BLE_CMD_SLEEP_THRESHOLD)
            val value = ByteArray(4)
            System.arraycopy(data, 4, value, 0, value.size)
            val aFloat = BleUtils.getFloat(value)
            //            Log.w("wsh"," sleep 1 : "+ aFloat);
        } else if (BleUtils.isCurrentCommandData(data, M2CmdConfig.BLE_CMD_SLEEP_THRESHOLD_N)) { //  睡眠阈值2
            checkCmdReturn(M2CmdConfig.BLE_CMD_SLEEP_THRESHOLD_N)
            val value = ByteArray(4)
            System.arraycopy(data, 4, value, 0, value.size)
            val aFloat = BleUtils.getFloat(value)
            //            Log.w("wsh"," sleep 2 : "+ aFloat);
        } else if (BleUtils.isCurrentCommandData(data, BleCmdConfig.BLE_CMD_POW_LEV)) { // 设置时间
            checkCmdReturn(BleCmdConfig.BLE_CMD_POW_LEV)
            if (!BleUtils.isCMDReadError(data, BleCmdConfig.getPayloadLengthByCommand(BleCmdConfig.BLE_CMD_POW_LEV))) {
                val value = ByteArray(BleCmdConfig.getPayloadLengthByCommand(BleCmdConfig.BLE_CMD_POW_LEV).toInt())
                System.arraycopy(data, 4, value, 0, value.size)
                val msg = Message.obtain(null, RE_POW_LEVEL)
                msg.obj = value
                sendMessage(msg)
            } else {
                errorReceiveCmd_Upper(BleCmdConfig.BLE_CMD_POW_LEV)
            }

        } else {
            succeedReceiveCmd()
        }
    }


    private fun handleCMDWriteResponse(data: ByteArray) {
        if (BleUtils.isCurrentCommandData(data, M2CmdConfig.BLE_CMD_DFU_JUMP_LENGTH)) { // 升级跳转
            checkCmdReturn(M2CmdConfig.BLE_CMD_DFU_JUMP_LENGTH)
        } else if (BleUtils.isCurrentCommandData(data, BleCmdConfig.BLE_CMD_TIME)) { // 设置时间
            checkCmdReturn(BleCmdConfig.BLE_CMD_TIME)
        } else if (BleUtils.isCurrentCommandData(data, M2CmdConfig.BLE_CMD_SLEEP_THRESHOLD)) {
            checkCmdReturn(M2CmdConfig.BLE_CMD_SLEEP_THRESHOLD)
        } else if (BleUtils.isCurrentCommandData(data, M2CmdConfig.BLE_CMD_SLEEP_THRESHOLD_N)) {
            checkCmdReturn(M2CmdConfig.BLE_CMD_SLEEP_THRESHOLD_N)
        } else {
            succeedReceiveCmd()
        }
    }

    private fun enableNotification(enable: Boolean, gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?) {
        if (gatt == null || characteristic == null)
            return
        gatt.setCharacteristicNotification(characteristic, enable)
        val clientConfig = characteristic.getDescriptor(UUIDUtils.CCC)
        if (enable) {
            clientConfig.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
        } else {
            clientConfig.value = BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE
        }
        val bData = BleWriteData()
        bData.write_type = BleWriteData.DESCRIP_WRITE
        bData.`object` = clientConfig
        sWriteQueue.add(bData)
    }

    private fun checkCmdReturn(receiveCmdId: Short) {
        val isSuccee = isCurrentCmdBack(latSendData?.write_data!!, receiveCmdId)
        if (isSuccee) { //  收到的返回是当前发送的指令
            sIsWriting = false
            timeOutTime = 0
            nextWrite()
        }
    }


    /**
     * 判断是否为当前命令的返回 UPPER
     */
    private fun isCurrentCmdBack(sendCmdId: ByteArray, receiveCmdId: Short): Boolean {
        val cmdByte = DataUtils.shortToByteArray(receiveCmdId)
        if (sendCmdId[2] == cmdByte[1] && sendCmdId[3] == cmdByte[0]) { // STM 的返回
            return true
        } else if (sendCmdId[1] == cmdByte[1] && sendCmdId[2] == cmdByte[0]) { // CMD的返回
            return true
        }
        return false
    }

    /**
     * CMD指令的重发错误次数 UPPER
     */
    private fun errorReceiveCmd_Upper(cmdID: Short) {}

    private fun succeedReceiveCmd() {
        sIsWriting = false
        timeOutTime = 0
        nextWrite()
    }

    private inner class TimeOutThread : Thread() {
        override fun run() {
            while (timeOutThread_Start) {
                if (sIsWriting) {
                    if (timeOutTime == 6) {
                        if (timeOutNum == 3) {
                            sIsWriting = false
                            sWriteQueue.clear()
                            timeOutNum = 0
                            timeOutTime = 0
                            sendTimeOutMessage()
                            nextWrite()
                            continue
                        }
                        if (reSendData != null && latSendData != null &&
                                reSendData?.write_type === latSendData?.write_type &&
                                Arrays.toString(reSendData?.write_data) == Arrays.toString(latSendData?.write_data)) {
                            timeOutNum++
                        } else {
                            timeOutNum = 1
                        }
                        latSendData?.let {
                            doWrite(it)
                            reSendData = latSendData
                        }
                        timeOutTime = 0
                    } else {
                        var num = 0
                        while (sIsWriting) {
                            SystemClock.sleep(10)
                            num++
                            if (num == 50) {
                                break
                            }
                        }
                        timeOutTime++
                    }
                } else {
                    var num = 0
                    while (!sIsWriting) {
                        SystemClock.sleep(10)
                        num++
                        if (num == 1000) {
                            break
                        }
                    }
                }
            }
        }
    }


    @Synchronized private fun write(bData: BleWriteData) {
        if (sIsWriting) {
            sWriteQueue.add(bData)
        } else {
            if (sWriteQueue.isEmpty()) {
                sIsWriting = true
                doWrite(bData)
            } else {
                sIsWriting = true
                doWrite(sWriteQueue.poll())
                sWriteQueue.add(bData)
            }
        }
    }

    @Synchronized private fun nextWrite() {
        if (!sWriteQueue.isEmpty() && !sIsWriting) {
            sIsWriting = true
            doWrite(sWriteQueue.poll())
        }
    }

    private fun doWrite(bDatas: BleWriteData) {
        if (mBluetoothGatt == null) {
            return
        }
        executorService.execute {
            if (bDatas.write_type === BleWriteData.CMD) {
                cmdWriteCharacter?.let {
                    it.writeType = BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE
                    it.value = bDatas.write_data
                    mBluetoothGatt?.writeCharacteristic(it)
                    latSendData = bDatas
                    timeOutThread?: let {
                        initThread_Upper()
                    }
                }

            } else if (bDatas.write_type === BleWriteData.DESCRIP_WRITE) { //  BluetoothGattDescriptor
                mBluetoothGatt?.writeDescriptor(bDatas.`object` as BluetoothGattDescriptor)
            } else {
                sIsWriting = false
                nextWrite()
            }
        }
    }

    private fun initThread_Upper() {
        timeOutThread_Start = true
        timeOutThread?:let {
            timeOutThread = TimeOutThread()
            timeOutThread?.start()
        }
    }


    private fun sendTimeOutMessage() {

    }


    /**
     * 读取硬件版本
     */
    private fun readHwVersion() {
        cmdReadCmd(BleCmdConfig.BLE_CMD_HW_VN, BleCmdConfig.getPayloadLengthByCommand(BleCmdConfig.BLE_CMD_HW_VN))
    }

    /**
     * 读取固件版本
     */
    private fun readSwVersion() {
        cmdReadCmd(BleCmdConfig.BLE_CMD_SW_VN, BleCmdConfig.getPayloadLengthByCommand(BleCmdConfig.BLE_CMD_SW_VN))
    }

    /**
     * 读取电量
     */
    private fun readPowerLevel() {
        cmdReadCmd(BleCmdConfig.BLE_CMD_POW_LEV, BleCmdConfig.getPayloadLengthByCommand(BleCmdConfig.BLE_CMD_POW_LEV))
    }

    private fun setDeviceTime() {
        cmdWriteCmd(BleCmdConfig.BLE_CMD_TIME, BleUtils.get_BleCmd_time())
    }

    private fun enableDataProfile() {
        enableNotification(true, mBluetoothGatt, dataRespondCharacter)
        nextWrite()
    }

    private fun disableDataProfile() {
        enableNotification(false, mBluetoothGatt, dataRespondCharacter)
        nextWrite()
    }

    /**
     * 关闭数据上传
     */
    private fun closedCmdData() {
        cmdWriteCmd(BleCmdConfig.BLE_CMD_UPLOAD_DATA, byteArrayOf(0x00))
    }

    /**
     * 打开数据上传
     */
    private fun openCmdData() {
        cmdWriteCmd(BleCmdConfig.BLE_CMD_UPLOAD_DATA, byteArrayOf(0x01))
    }

    fun refreshDeviceCache(): Boolean {
        if (mBluetoothGatt != null) {
            try {
                val localBluetoothGatt = mBluetoothGatt
                val localMethod = localBluetoothGatt?.javaClass!!.getMethod(
                        "refresh", *arrayOfNulls(0))
                if (localMethod != null) {
                    return (localMethod.invoke(
                            localBluetoothGatt, *arrayOfNulls(0)) as Boolean)
                }
            } catch (localException: Exception) {
            }

        }
        return false
    }

    /**
     * 命令通道
     * 写命令（有参数）
     */
    private fun cmdWriteCmd(commandId: Short, value: ByteArray) {
        val cmdByte = DataUtils.shortToByteArray(commandId)
        val bleWriteData = BleWriteData()
        val data = ByteArray(5 + value.size)
        data[0] = 0xA0.toByte()
        data[1] = cmdByte[1]
        data[2] = cmdByte[0]
        data[3] = value.size.toByte()
        System.arraycopy(value, 0, data, 4, value.size)
        data[data.size - 1] = TLUtil.calcDataCrcInBytes(data)
        bleWriteData.write_type = BleWriteData.CMD
        bleWriteData.write_data = data
        write(bleWriteData)
    }


    /**
     * 命令通道
     * 读命令（有参数 = 长度）
     */
    private fun cmdReadCmd(commandId: Short, valueLength: Byte) {
        val bleWriteData = BleWriteData()
        val cmdByte = DataUtils.shortToByteArray(commandId)
        val data = ByteArray(6)
        data[0] = 0xB0.toByte()
        data[1] = cmdByte[1]
        data[2] = cmdByte[0]
        data[3] = 0x01.toByte()
        data[4] = valueLength
        data[5] = TLUtil.calcDataCrcInBytes(data)
        bleWriteData.write_type = BleWriteData.CMD
        bleWriteData.write_data = data
        write(bleWriteData)
    }


    private fun sendFrameResponse(d: Byte, d2: Byte, d3: Byte, d4: Byte) {
        val data = ByteArray(4)
        data[0] = d
        data[1] = d2
        data[2] = d3
        data[3] = d4
        if (dataWriteCharacter != null) {
            dataWriteCharacter?.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE)
            dataWriteCharacter?.setValue(data)
            mBluetoothGatt?.writeCharacteristic(dataWriteCharacter)
        }
    }


    private fun sendS06Data(data: ByteArray) {
        val msg = Message.obtain(null, RE_MSG_DATA_S06)
        msg.obj = data
        sendMessage(msg)
    }


    private fun handleDataAvailable(data: ByteArray) {
        if (mMedicalDataLength > 0) {
            handleMedicalData(data)
        } else if (data[0] == MAGIC_FIRST_BYTE) {
            when (data[1]) {
                MAGIC_SIMPLE_NO_RE_BYTE  // 简化帧无应答
                -> sendS06Data(data)
                MAGIC_SIMPLE_RES_BYTE  // 简化帧有应答
                -> {
                    sendFrameResponse(data[0], data[1], data[2], ERROR_FLAG_SUCCESS)
                    sendS06Data(data)
                }
                MAGIC_SIMPLE_EXTEND_NO_RES_BYTE  // 简化帧(含扩展)无应答
                -> sendS06Data(data)
                MAGIC_SIMPLE_EXTEND_AND_RES_BYTE  // 简化帧(含扩展)有应答
                -> {
                    sendFrameResponse(data[0], data[1], data[2], ERROR_FLAG_SUCCESS)
                    sendS06Data(data)
                }
            /**
             * ----------------------------- 区别 ------------------------
             */
                MAGIC_EXTEND_NO_RES_BYTE  // 扩展帧 无应答
                -> sendS06Data(data)
                MAGIC_EXTEND_AND_RES_BYTE // 扩展帧 需应答
                -> {
                    sendFrameResponse(data[0], data[1], data[2], ERROR_FLAG_SUCCESS)
                    sendS06Data(data)
                }
                MAGIC_STANDARD_NO_RES_BYTE // 标准帧 无应答
                -> initMedicalData(data)
                MAGIC_STANDARD_AND_RES_BYTE // 标准帧 需应答
                -> {
                    sendFrameResponse(data[0], data[1], data[2], ERROR_FLAG_SUCCESS)
                    initMedicalData(data)
                }
                MAGIC_STANDARD_EXTEND_NO_RES_BYTE // 标准帧(含扩展) 无应答
                -> initMedicalData(data)
                MAGIC_STANDARD_EXTEND_AND_RES_BYTE // 标准帧(含扩展) 需应答
                -> {
                    sendFrameResponse(data[0], data[1], data[2], ERROR_FLAG_SUCCESS)
                    initMedicalData(data)
                }
            }
        }
    }

    private var mMedicalDataLength = -1
    private var mMedicalCurLength = 0
    private var mMedicalData: ByteArray? = null


    private fun handleMedicalData(data: ByteArray) {
        if (data[0] == MAGIC_FIRST_BYTE) {
            if (data[1] == MAGIC_EXTEND_NO_RES_BYTE && data.size <= 18) {  // 扩展帧 无应答
                sendS06Data(data)
                resetMedicalData()
                return
            } else if (data[1] == MAGIC_EXTEND_AND_RES_BYTE && data.size <= 18) {  // 扩展帧 需应答
                sendFrameResponse(data[0], data[1], data[2], ERROR_FLAG_SUCCESS)
                resetMedicalData()
                return
            } else if (data[1] == MAGIC_STANDARD_NO_RES_BYTE
                    && (data[3] == CORE_TYPE_MEDC_ECG/* || data[3] == CORE_TYPE_MEDC_SLEP*/) && data[2] or (data[3].toInt() shl 8).toByte() < 1000) {  // 标准帧 无应答
                initMedicalData(data)
                return
            } else if (data[1] == MAGIC_STANDARD_AND_RES_BYTE
                    && (data[3] == CORE_TYPE_MEDC_ECG /*|| data[3] == CORE_TYPE_MEDC_SLEP*/) && data[2] or (data[3].toInt() shl 8).toByte() < 1000) {  // 标准帧 需应答
                sendFrameResponse(data[0], data[1], data[2], ERROR_FLAG_SUCCESS)
                initMedicalData(data)
                return
            } else if (data[1] == MAGIC_STANDARD_EXTEND_NO_RES_BYTE
                    && (data[3] == CORE_TYPE_MEDC_ECG /*|| data[3] == CORE_TYPE_MEDC_SLEP*/) && data[2] or (data[3].toInt() shl 8).toByte() < 1000) {  // 标准帧(含扩展) 无应答
                initMedicalData(data)
                return
            } else if (data[1] == MAGIC_STANDARD_EXTEND_AND_RES_BYTE && (data[3] == CORE_TYPE_MEDC_ECG /*|| data[3] == CORE_TYPE_MEDC_SLEP*/) && data[2] or (data[3].toInt() shl 8).toByte() < 1000) {  // 标准帧(含扩展) 需应答
                sendFrameResponse(data[0], data[1], data[2], ERROR_FLAG_SUCCESS)
                initMedicalData(data)
                return
            }
        }
        addMedicalData(data)
    }

    private fun addMedicalData(data: ByteArray) {
        if (mMedicalDataLength > 0) {
            val len = data.size
            try {
                System.arraycopy(data, 0, mMedicalData!!, mMedicalCurLength, len)
                mMedicalCurLength += len
                mMedicalDataLength -= len
            } catch (e: ArrayIndexOutOfBoundsException) {
                resetMedicalData()
            }

            if (mMedicalDataLength == 0) {
                val crcPass = TLUtil.validateCRC8(mMedicalData)
                if (crcPass) {
                    Log.d("wsh", "CRC校验后 ------ 帧计数" + mMedicalData!![2] + " 接收到数据，长度 ： " + mMedicalData!!.size)
                    sendS06Data(mMedicalData!!)
                } else {
                    Log.d("wsh", "CRC校验后 ------ 失败")
                    resetMedicalData()
                }
            } else if (mMedicalDataLength < 0) {
                resetMedicalData()
            }
        } else {
            resetMedicalData()
        }
    }

    private fun initMedicalData(data: ByteArray) {
        mMedicalData = null
        /**
         * 帧头	    类型ID	长度	数据(含扩展)	CRC
         * 3byte	1byte	2byte	Xbyte	1byte
         */
//        mMedicalDataLength = ((data[4] and 0xFF.toByte()) or ((data[5].toInt()) shl 8).toByte()).toInt() + 7 // 加上crc的长度 加上帧头的长度 加上类型ID的长度 加上长度字节的长度
        val b = (data[5].toInt()) shl 8
        val a = data[4].toInt() and 0xff

        mMedicalDataLength = (a + b)  + 7 // 加上crc的长度 加上帧头的长度 加上类型ID的长度 加上长度字节的长度
        Log.d("wsh", "收到头 ----- 帧计数" + data[2] + " 接收到数据，长度 ： " + mMedicalDataLength)
        if (mMedicalDataLength > 0) {
            mMedicalData = ByteArray(mMedicalDataLength)
            mMedicalCurLength = 0
            val len = data.size
            try {
                System.arraycopy(data, 0, mMedicalData!!, mMedicalCurLength, len)
                mMedicalCurLength += len
                mMedicalDataLength -= len
            } catch (e: ArrayIndexOutOfBoundsException) {
                resetMedicalData()
            }

        }
    }

    private fun resetMedicalData() {
        //        Log.d("wsh","数据重置" +" --- ");
        mMedicalDataLength = -1
        mMedicalCurLength = 0
        mMedicalData = null
    }

}
