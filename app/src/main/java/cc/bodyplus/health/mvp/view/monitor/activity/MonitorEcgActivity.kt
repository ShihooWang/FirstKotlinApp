package cc.bodyplus.health.mvp.view.monitor.activity

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Message
import android.view.View
import android.view.WindowManager
import cc.bodyplus.health.R
import cc.bodyplus.health.ble.bean.DeviceInfo
import cc.bodyplus.health.ble.manager.BleConnectionInterface
import cc.bodyplus.health.ble.manager.BleConnectionManger
import cc.bodyplus.health.ble.utils.BleConst.WAVE_OUT_ECG_LENGTH
import cc.bodyplus.health.ble.utils.BleConst.WAVE_OUT_LENGTH
import cc.bodyplus.health.ble.utils.monitor.EcgOriginalData
import cc.bodyplus.health.mvp.view.BaseActivity
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_monitor_ecg.*
import java.util.concurrent.TimeUnit

/**
 * Created by shihoo.wang on 2018/5/8.
 * Email shihu.wang@bodyplus.cc
 */
class MonitorEcgActivity : BaseActivity(), BleConnectionInterface {
    override fun bleDispatchMessage(msg: Message) {
    }

    override fun blePowerLevel(data: Byte) {
    }

    override fun reDeviceInfo(deviceInfo: DeviceInfo) {
    }

    override fun bleDeviceDisconnect() {
    }

    override fun bleCoreModule(data: Byte) {
    }

    override fun heartBreathData(heart: Int, breath: Int) {
        if (heart >= 0){
            monitor_ecg_heart.text = "当前心率 ：$heart"

        }else{
            monitor_ecg_heart.text = "当前心率 ：--"
        }
    }

    private var cutDownDisposable : Disposable ?= null
    private var timerEcgDisposable: Disposable? = null
    override fun initInject() {

    }

    override fun layoutId(): Int {
        return R.layout.activity_monitor_ecg
    }

    override fun initData() {
    }

    override fun initView() {
        ecg_wave.moveBaseLineDown(1)
        ecg_wave.reSet()

        roundProgressBar.startCountDownTime({ }, 100.0, 100.0)
        createCuntDownTime()
        createEcgDisposable()
        monitor_ic_back.setOnClickListener { onBackPressed() }
        BleConnectionManger.mInstance.addConnectionListener(this,true)
    }


    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        adjustScreen()
    }

    override fun onDestroy() {
        destroyEcgDisposable()
        destroyCutDownDisposable()
        BleConnectionManger.mInstance.removeConnectionListener(this)
//        EcgOriginalData.mInstance.stopSaveData()
        super.onDestroy()
    }
    private fun createCuntDownTime(){
        var cutDownTime = 0
        cutDownDisposable = Observable.interval(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnDispose { }.subscribe({
            cutDownTime ++
            if (cutDownTime == 60) {

                endEcgRecord()
                cutDownTime = 0
            }
        }) { throwable -> throwable.printStackTrace() }
    }

    private fun createEcgDisposable() {
        timerEcgDisposable = Observable.interval((1000L * WAVE_OUT_LENGTH )/ WAVE_OUT_ECG_LENGTH, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnDispose { }.subscribe({ handleEcgTime() }) { }
    }

    private fun handleEcgTime() {
        var data = EcgOriginalData.mInstance.getEcgConversionData()
        data?.let {
            ecg_wave.setCurrentHeartData(data)
        }?:let {
            ecg_wave.setCurrentHeartData(FloatArray(WAVE_OUT_LENGTH))
        }
    }

    private fun endEcgRecord() {
        destroyCutDownDisposable()
        ecg_wave.reSet()
        destroyEcgDisposable()
        EcgOriginalData.mInstance.stopSaveData()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startActivity(Intent(this, MonitorEcgTagActivity::class.java), ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        }else {
            startActivity(Intent(this, MonitorEcgTagActivity::class.java))
        }
        finish()
    }

    private fun destroyEcgDisposable() {
        timerEcgDisposable?.run {
            if (!isDisposed){
                dispose()
            }
        }
    }

    private fun destroyCutDownDisposable(){
        cutDownDisposable?.run {
            if (!isDisposed){
                dispose()
            }
        }
    }

    private fun adjustScreen() {
        val wm = applicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val height = wm.defaultDisplay.height
        if (height < 2000) {
            view_null.visibility = View.GONE
        }
        val lp = rl_ecg_view.layoutParams
        lp.height = ecg_wave.viewHeight.toInt()
        rl_ecg_view.layoutParams = lp
    }

}