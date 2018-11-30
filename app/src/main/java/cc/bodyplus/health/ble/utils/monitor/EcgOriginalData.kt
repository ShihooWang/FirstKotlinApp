package cc.bodyplus.health.ble.utils.monitor

import android.annotation.SuppressLint
import android.util.Log
import cc.bodyplus.NdkJniUtils
import cc.bodyplus.health.ble.bean.OriginalFrameData
import cc.bodyplus.health.ble.manager.BleConnectionInterface
import cc.bodyplus.health.ble.utils.BleConst
import cc.bodyplus.health.ble.utils.BleConst.DATA_LENGTH
import cc.bodyplus.health.ble.utils.BleConst.GAIN
import cc.bodyplus.health.ble.utils.BleConst.NDK_HEART_BACK_LENGTH
import cc.bodyplus.health.ble.utils.BleConst.WAVE_OUT_ECG_LENGTH
import cc.bodyplus.health.ble.utils.BleConst.WAVE_OUT_LENGTH
import cc.bodyplus.health.mvp.module.BPECGOriginal
import cc.bodyplus.health.mvp.module.BPRecordModel
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * Created by shihoo.wang on 2018/5/7.
 * Email shihu.wang@bodyplus.cc
 */

class EcgOriginalData {

    companion object {
        @SuppressLint("StaticFieldLeak")
        var mInstance = EcgOriginalData()
    }

    private var dataQueue: Queue<FloatArray> = ConcurrentLinkedQueue<FloatArray>()
    private var ecgBuff: LimitEcgQueue<BPECGOriginal> = LimitEcgQueue(DATA_LENGTH)
    private var lastFrameCount = -1
    private var ecgCacheStatus = 0
    private var isStartSave : Boolean = false
    private var  bPRecordModel : BPRecordModel?=null

    fun handleFrameData(originalFrameData: OriginalFrameData, status: Int, terface: BleConnectionInterface? ){

        if (status == 1) { // CRC失败或者数据接收转换错误
            ecgBuff.append(RecordUtils.generateErrorBPECGOriginal())
            NdkJniUtils.ecgAlgorithmInit()
        } else {
            // 判断帧计数
            val currentFrameCount: Int
            if (originalFrameData.frameCount == 0) {
                currentFrameCount = 256
            } else {
                currentFrameCount = originalFrameData.frameCount
            }
            if (lastFrameCount < 0) {
                // 第一帧数据
            } else if (lastFrameCount == currentFrameCount - 1) {
                val bpecgOriginal = RecordUtils.generateFrameData(originalFrameData)
                if (isStartSave) {
                    ecgBuff.append(bpecgOriginal)
                }
                handleEcgOriginalData(bpecgOriginal,terface)
            } else if (lastFrameCount == currentFrameCount) {
                // 相同帧计数
            } else {
                if (isStartSave) {
                    for (i in 0 until currentFrameCount - 1 - lastFrameCount) {
                        ecgBuff.append(RecordUtils.generateErrorBPECGOriginal())
                    }
                }
                NdkJniUtils.ecgAlgorithmInit()
            }
            lastFrameCount = originalFrameData.frameCount
        }

    }


    private fun handleEcgOriginalData(ecgOriginal: BPECGOriginal, terface: BleConnectionInterface?) {
        val out = IntArray(WAVE_OUT_ECG_LENGTH)
        val heart = IntArray(NDK_HEART_BACK_LENGTH)
        val status = IntArray(NDK_HEART_BACK_LENGTH)
        NdkJniUtils.ecgProcess(ecgOriginal.values, out, heart, status)
//        Log.i("wsh", "ECG数据 ---  " + Arrays.toString(ecgOriginal.values))

        Log .d("wsh" , "monition 心率 ： " + heart[0])
        ecgOriginal.status = status[0]
        terface?.heartBreathData(heart[0])
        val result = FloatArray(WAVE_OUT_ECG_LENGTH)
        for (i in out.indices) {
            result[i] = out[i] / GAIN
        }
        if (isStartSave) {
            handleEcgConversionData(result)
        }
    }

    private fun handleEcgConversionData(data: FloatArray) {
        ecgCacheStatus++
        if (ecgCacheStatus <= 5) {
            return
        }
        for (i in 0 until WAVE_OUT_ECG_LENGTH / WAVE_OUT_LENGTH) {
            val da = FloatArray(WAVE_OUT_LENGTH)
            System.arraycopy(data, WAVE_OUT_LENGTH * i, da, 0, WAVE_OUT_LENGTH)
            dataQueue.add(da)
        }
    }

    fun getEcgConversionData()  = dataQueue.poll()

    fun startSaveData(){
        isStartSave = true
        dataQueue.clear()
        ecgCacheStatus = 0
        ecgBuff.clear()
    }

    fun stopSaveData(){
        isStartSave = false
        handleEcgBuff(ecgBuff)
    }

    private fun handleEcgBuff(ecgBuff: LimitEcgQueue<BPECGOriginal>) {
        val bpecgOriginals = RecordUtils.generateEcgOraiginals(ecgBuff)
        bPRecordModel = BPRecordModel()
//        bPRecordModel?.ecgOriginals = bpecgOriginals
        bPRecordModel?.stamp = System.currentTimeMillis() - bpecgOriginals.size *1000
        val originalData = IntArray(BleConst.NDK_ORIGIAN_LENGTH)

        var totalProcess = 0
        for (bpecgOriginal in bpecgOriginals) {
            System.arraycopy(bpecgOriginal.values, 0, originalData, totalProcess, bpecgOriginal.values.size)
            totalProcess += bpecgOriginal.values.size
        }
        val array = IntArray(BleConst.NDK_DATA_BACK_LENGTH)
        val heart = IntArray(NDK_HEART_BACK_LENGTH)
        val arr = IntArray(BleConst.NDK_DESC_BACK_LENGTH)
        NdkJniUtils.ecgOffLineProcess(originalData, array, heart, arr)
        bPRecordModel?.desc = RecordUtils.generateArrayList(arr)
        bPRecordModel?.avgHR = heart[0]
        bPRecordModel?.dealArr = array
//        val ecgStatus = EcgStatusUtils.generateEcgStatus(record.desc)
//        showEcgRecordDialog(record.stamp, array, heart[0], ecgStatus)
    }

    fun getBPRecordModel() = bPRecordModel

    fun setBPRecordModel(recordModel : BPRecordModel){
        bPRecordModel = recordModel
    }
}