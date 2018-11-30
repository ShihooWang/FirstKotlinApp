package cc.bodyplus.health.mvp.view.monitor.activity

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.SeekBar
import cc.bodyplus.health.R
import cc.bodyplus.health.ble.utils.BleConst.GAIN
import cc.bodyplus.health.ble.utils.BleConst.NDK_DATA_BACK_LENGTH
import cc.bodyplus.health.ble.utils.BleConst.WAVE_OUT_ECG_LENGTH
import cc.bodyplus.health.ble.utils.BleConst.WAVE_OUT_LENGTH
import cc.bodyplus.health.ble.utils.monitor.EcgOriginalData
import cc.bodyplus.health.ext.showToast
import cc.bodyplus.health.mvp.view.BaseActivity
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_ecg_report.*
import java.util.concurrent.TimeUnit

/**
 * Created by shihoo.wang on 2018/5/10.
 * Email shihu.wang@bodyplus.cc
 */
class EcgViewReportActivity : BaseActivity() {

    private var timerEcgDisposable: Disposable? = null

    private var pausePlay: Boolean = false
    private var dataArray: FloatArray? = null
    private var currentIndex: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        isNeedLandSpace = true
        super.onCreate(savedInstanceState)
    }


    override fun initInject() {

    }

    override fun layoutId(): Int {
        return R.layout.activity_ecg_report
    }

    override fun initData() {
        generateData()
        createEcgDisposable()
        ecg_wave.needDrawSeconds(true)
        ecg_wave.setTextMargin(60)
        ecg_wave.moveBaseLineDown(1)
        ecg_progress_bar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    currentIndex = progress
                    seekBarChangeData(currentIndex)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                pauseEcg()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                pausePlay = true
                currentIndex = seekBar.progress
                seekBarChangeData(currentIndex)
            }
        })
    }

    override fun initView() {
        iv_close.setOnClickListener({onBackPressed()})
        iv_play.setOnClickListener({
            if (iv_play.tag == "play") {
                pauseEcg()
            } else if (iv_play.tag == "pause") {
                playEcg()
            }
        })
        ecg_tv_pdf.setOnClickListener({startShare()})
    }

    override fun onDestroy() {
        super.onDestroy()
        destroyEcgDisposable()
    }

    private fun generateData() {
        val bpRecordModel = EcgOriginalData.mInstance.getBPRecordModel()
        bpRecordModel?.let {
            dataArray = FloatArray(NDK_DATA_BACK_LENGTH)
            for (i in 0 until NDK_DATA_BACK_LENGTH) {
                bpRecordModel.dealArr?.let {
                    dataArray!![i] = bpRecordModel.dealArr[i] / GAIN
                }
            }
            ecg_progress_bar.max = NDK_DATA_BACK_LENGTH / WAVE_OUT_LENGTH
            if (bpRecordModel.avgHR > 0) {
                ecg_wave.setLeftText("心率：" + bpRecordModel.avgHR + "bpm")
            }else{
                ecg_wave.setLeftText("平均心率：- -")
            }
        }

    }

    private fun createEcgDisposable(){
        timerEcgDisposable = Observable.interval(1000L * WAVE_OUT_LENGTH / WAVE_OUT_ECG_LENGTH, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnDispose { }.subscribe({ handleEcgTime() }) { throwable -> throwable.printStackTrace() }
    }

    private fun destroyEcgDisposable() {
        timerEcgDisposable?.run {
            if (!isDisposed){
                dispose()
            }
        }
    }

    private fun handleEcgTime() {
        if (pausePlay) {
            return
        }
        if (dataArray != null) {
            if (currentIndex == NDK_DATA_BACK_LENGTH / WAVE_OUT_LENGTH) {
                resetData()
            } else {
                val currentData = FloatArray(WAVE_OUT_LENGTH)
                System.arraycopy(dataArray, currentIndex * WAVE_OUT_LENGTH, currentData, 0, WAVE_OUT_LENGTH)
                //                currentIndex += WAVE_OUT_LENGTH;
                currentIndex += 1
                ecg_wave.setCurrentHeartData(currentData)
                ecg_progress_bar.progress = currentIndex
            }
        }
    }


    private fun seekBarChangeData(index: Int) {
        val ecgViewDataLength = ecg_wave.dataLength
        // 取index之前所有索引值 判断是否够一屏幕  ecgViewDataLength/10
        val data = FloatArray(ecgViewDataLength)
        dataArray?.let {
            if (index * WAVE_OUT_LENGTH >= ecgViewDataLength) {
                System.arraycopy(dataArray, index * WAVE_OUT_LENGTH - ecgViewDataLength, data, 0, ecgViewDataLength)
            } else {
                System.arraycopy(dataArray, 0, data, ecgViewDataLength - index * WAVE_OUT_LENGTH, index * WAVE_OUT_LENGTH)
            }
            ecg_wave.setChangeData(data)
        }
    }

    private fun resetData() {
        iv_play.tag = "pause"
        iv_play.setBackgroundResource(R.drawable.iv_ecg_start)
        pausePlay = true
    }

    private fun pauseEcg() {
        iv_play.tag = "pause"
        iv_play.setBackgroundResource(R.drawable.iv_ecg_start)
        pausePlay = true
    }


    private fun playEcg() {
        if (currentIndex == NDK_DATA_BACK_LENGTH / WAVE_OUT_LENGTH) {
            currentIndex = 0
            ecg_wave.reSet()
        }
        iv_play.tag = "play"
        iv_play.setBackgroundResource(R.drawable.iv_ecg_pause)
        pausePlay = false
    }

    private fun startShare() {
        pauseEcg()
        val bpRecordModel = EcgOriginalData.mInstance.getBPRecordModel()
        bpRecordModel?.let {
            val intent = Intent(this@EcgViewReportActivity, EcgSharePngActivity::class.java)
            intent.putExtra("type", 2)
            val b = Bundle()
            b.putFloatArray("ecgRecordArray", dataArray)
            b.putLong("stamp", it.stamp)
            b.putInt("avgHr", it.avgHR)
            intent.putExtras(b)
            startActivity(intent)
        } ?:let {
            showToast("打开失败！")
        }
    }
}