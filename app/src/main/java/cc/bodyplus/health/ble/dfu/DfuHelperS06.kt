package cc.bodyplus.health.ble.dfu

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.SystemClock
import android.util.Log
import cc.bodyplus.health.R
import cc.bodyplus.health.ble.bean.DeviceInfo
import cc.bodyplus.health.ble.manager.BleConnectionManger
import cc.bodyplus.health.ble.ota.OTAService
import cc.bodyplus.health.ble.utils.*
import cc.bodyplus.health.ext.showToast
import cc.bodyplus.health.utils.SharedPrefHelperUtils
import kotlinx.android.synthetic.main.view_dialog.*
import no.nordicsemi.android.dfu.DfuProgressListenerAdapter
import no.nordicsemi.android.dfu.DfuServiceInitiator
import no.nordicsemi.android.dfu.DfuServiceListenerHelper
import org.json.JSONObject
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader
import kotlin.experimental.or

/**
 * Created by shihoo.wang on 2018/4/24.
 * Email shihu.wang@bodyplus.cc
 */
class DfuHelperS06 (private val mContext: Activity, private val mDeviceInfo: DeviceInfo){

    private var mBluetoothAdapter: BluetoothAdapter? = null
    private var isFound = false
    private var mZipPath: String? = null
    private var mSwVersion: Int = 0


//    fun setZipFile(fileName: String): Int {
//        try {
//            FileUtils.deleteFile(BleConstant.UPDATE_STM32_PATH)
//            UnzipFromAssets.unZip(mContext, fileName, BleConstant.UPDATE_STM32_PATH, true)
//            val jsonPath = getJsonFileFromSD(BleConstant.UPDATE_STM32_PATH + "/bodydfu.json")
//            val stm32Json = JSONObject(jsonPath).getJSONObject("stm32dfu")
//
//            val firmwareName = stm32Json.getString("firmware_name")
//            val firmwareVersion = stm32Json.getString("firmware_version")
//            mZipPath = BleConstant.UPDATE_STM32_PATH + "/" + firmwareName
//            mSwVersion = Integer.parseInt(firmwareVersion)
//            if (mSwVersion > Integer.parseInt(mDeviceInfo.swVn)) {
//                startUpdate()
//                return 0
//            } else {
//                return 1
//            }
//        } catch (e: Exception) {
//            return 2
//        }
//
//    }

//
//    private fun startUpdate() {
//        BleConnectionManger.mInstance.sendUpdateBleStart()
//        Thread(Runnable {
//            SystemClock.sleep((5 * 1000).toLong())
//            searchDFUDevice()
//        }).start()
//
//    }


//    private fun searchDFUDevice() {
//        val bluetoothManager = mContext.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
//        if (bluetoothManager != null) {
//            mBluetoothAdapter = bluetoothManager.adapter
//            if (!mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
//                // 提示不支持蓝牙设备
//                return
//            } else {
//                isFound = false
//                mBluetoothAdapter?.startLeScan(mLeScanCallback)
//                Log.d("ble", "searchDFUDevice")
//            }
//        }
//    }

//    private val mLeScanCallback = object : BluetoothAdapter.LeScanCallback {
//        override fun onLeScan(device: BluetoothDevice, rssi: Int, scanRecord: ByteArray) {
//            val recodeArray = BleUtils.parseFromBytes(scanRecord)
//            //根据厂商ID获取对应的信息（0xffff 默认厂商ID  信息为SN码）
//            val b = recodeArray.get(0xffff)
//
//            b?.let {
//                if (!isFound){
//                    val deviceSn = BleUtils.byteToChar(it)
//                    var sn = ""
//                    var hw = "6"
//                    var hasHwInfo = false
//                    if (deviceSn.length == 10) {
//                        sn = deviceSn
//                    } else if (deviceSn.length == 12) {
//                        sn = deviceSn.substring(0, 10)
//                        val s = it[11].toInt()
//                        val t = (s shl 8).toShort()
//                        hw = (t or it[10].toShort()).toString()
//                        hasHwInfo = true
//                    }
//
//                    if (sn == mDeviceInfo.sn) {
//                        if (BleUtils.isFilterDFUUUID(scanRecord)) {
//                            isFound = true
//                            mBluetoothAdapter?.stopLeScan(this)
//                            if (hasHwInfo) {
//                                startUp(device.address, device.name)
//                            } else {
//                                var updateDfu = DfuLocalConfig.HW_102_BOOTLOADER
//                                if (mDeviceInfo.hwVn.equals(""+DfuLocalConfig.HW_103)) {
//                                    updateDfu = DfuLocalConfig.HW_103_BOOTLOADER
//                                }
//                                setZipFile(updateDfu, device.address, device.name)
//                            }
//
//                        }
//                    }
//                }
//            }
//        }
//    }


    fun setZipFile(fileName: String, address: String, name: String): Int {
        try {
            FileUtils.deleteFile(BleConstant.UPDATE_STM32_PATH)
            UnzipFromAssets.unZip(mContext, fileName, BleConstant.UPDATE_STM32_PATH, true)
            val jsonPath = getJsonFileFromSD(BleConstant.UPDATE_STM32_PATH + "/bodydfu.json")
            val stm32Json = JSONObject(jsonPath).getJSONObject("stm32dfu")

            val firmwareName = stm32Json.getString("firmware_name")
            val firmwareVersion = stm32Json.getString("firmware_version")
            mZipPath = BleConstant.UPDATE_STM32_PATH + "/" + firmwareName
            mSwVersion = Integer.parseInt(firmwareVersion)
            if (mSwVersion > Integer.parseInt(mDeviceInfo.swVn)) {
                startUp(address, name)
                return 0
            } else {
                return 1
            }
        } catch (e: Exception) {
            return 2
        }

    }

    private fun startUp(address: String, name: String) {
        Thread(Runnable {
            val starter = DfuServiceInitiator(address)
                    .setDeviceName(name)
                    .setPacketsReceiptNotificationsEnabled(Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
                    .setKeepBond(true)
                    .setUnsafeExperimentalButtonlessServiceInSecureDfuEnabled(true)
                    .setForeground(Build.VERSION.SDK_INT < Build.VERSION_CODES.O)

            starter.setZip(mZipPath)
            starter.start(mContext, OTAService::class.java)
        }).start()
        if (progressDialog2 == null) {
            progressDialog2 = BleProgress2()
            progressDialog2!!.showDialog(mContext)
        }
        progressDialog2!!.setProgress(0)
        progressDialog2!!.setMessage(mContext.getString(R.string.equipment_core_update))
        DfuServiceListenerHelper.registerProgressListener(mContext, mDfuProgressListener)
    }


    private fun getJsonFileFromSD(path: String): String {
        var result = ""
        try {
            val fis = FileInputStream(path)
            val bfr = BufferedReader(InputStreamReader(fis))
            var line : String
            do {
                line = bfr.readLine()
                if (line == null){
                    break
                }else{
                    result += line
                }

            }while (true)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return result
    }

    private fun unRegist() {
        mDfuProgressListener.let {
            DfuServiceListenerHelper.unregisterProgressListener(mContext, it)
        }
    }

    private var progressDialog2: BleProgress2? = null

    private val mDfuProgressListener = object : DfuProgressListenerAdapter() {
        override fun onDfuCompleted(deviceAddress: String) {
            progressDialog2?.run {disMiss()}
            unRegist()
            mContext.showToast(mContext.getResources().getString(R.string.equipment_update_succeed))
            mDeviceInfo.swVn = mSwVersion.toString()
            BleSharedPrefHelperUtils.getInstance().coreInfo = mDeviceInfo
            connectDevice()
            Log.w("wsh dfu", " onDfuCompleted !!! ")
        }

        override fun onDfuAborted(deviceAddress: String) {
            progressDialog2?.run { disMiss()}
            unRegist()
            connectDevice()
            mContext.showToast(mContext.getResources().getString(R.string.equipment_update_fail))
            Log.w("wsh dfu", " onDfuAborted! deviceAddress: " + deviceAddress)
        }

        override fun onProgressChanged(deviceAddress: String, percent: Int, speed: Float, avgSpeed: Float, currentPart: Int, partsTotal: Int) {
            progressDialog2?.run { setProgress(percent)}
            Log.w("wsh dfu", " onProgressChanged : " + percent)
        }

        override fun onError(deviceAddress: String, error: Int, errorType: Int, message: String) {
            unRegist()
            progressDialog2?.run { disMiss()}
            connectDevice()
            mContext.showToast(mContext.getResources().getString(R.string.equipment_update_fail) + message)
            Log.w("wsh dfu", "onError! error: $error  errorType: $errorType  message :$message")
        }
    }

    private fun connectDevice() {
        Thread(Runnable {
            SystemClock.sleep((10 * 1000).toLong())
            mDeviceInfo.sn?.let {
                BleConnectionManger.mInstance.autoConnectBleBySN(it)
            }
        }).start()
    }
}