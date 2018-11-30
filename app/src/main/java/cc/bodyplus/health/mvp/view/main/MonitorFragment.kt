package cc.bodyplus.health.mvp.view.main

import android.app.ActivityOptions
import android.app.Fragment
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.*
import cc.bodyplus.NdkJniUtils
import cc.bodyplus.health.R
import cc.bodyplus.health.ble.bean.DeviceInfo
import cc.bodyplus.health.ble.bean.MyBleDevice
import cc.bodyplus.health.ble.dfu.CoreUpdateHelper
import cc.bodyplus.health.ble.manager.BleConnectionInterface
import cc.bodyplus.health.ble.manager.BleConnectionManger
import cc.bodyplus.health.ble.utils.BleConst.RE_SCAN_DEVICE
import cc.bodyplus.health.ble.utils.BleSharedPrefHelperUtils
import cc.bodyplus.health.ble.utils.DfuLocalConfig
import cc.bodyplus.health.ble.utils.monitor.EcgOriginalData
import cc.bodyplus.health.ext.showToast
import cc.bodyplus.health.mvp.view.monitor.activity.EcgListActivity
import cc.bodyplus.health.mvp.view.equipment.EquipmentBondActivity
import cc.bodyplus.health.mvp.view.equipment.EquipmentBondActivity.Companion.BOND_CANCEL
import cc.bodyplus.health.mvp.view.equipment.EquipmentBondActivity.Companion.BOND_SUCCEED
import cc.bodyplus.health.mvp.view.equipment.EquipmentBondActivity.Companion.SEARCH_FAIL
import cc.bodyplus.health.mvp.view.monitor.activity.EcgListActivity2
import cc.bodyplus.health.mvp.view.monitor.activity.MonitorEcgActivity
import cc.bodyplus.health.utils.FastClickUtil
import cc.bodyplus.health.utils.SharedPrefHelperUtils
import cc.bodyplus.health.widget.dialog.InitMonitorDialog
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_monitor.*
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * Created by shihoo.wang on 2018/5/2.
 * Email shihu.wang@bodyplus.cc
 */

class MonitorFragment : Fragment(), BleConnectionInterface {


    private var searchAnimIg : ImageView ?= null
    private var fragmentMonitorViewHolder : FragmentMonitorViewHolder ?= null

    private val mHandler : Handler by lazy {
        Handler()
    }

    private val typeFaceYaHei by lazy { Typeface.createFromAsset(activity.assets, "fonts/DIN.ttf") }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_monitor,container,false)
    }


    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSearchStatus()
        activity?.let {
            mHandler.postDelayed(Runnable {
                isBleOpen(activity)
            },200)
        }

        ll_monitor_bottom.setOnClickListener({
            loadMoreEcgList()
        })

    }

    override fun onResume() {
        super.onResume()
        BleConnectionManger.mInstance.addConnectionListener(this,true)
    }

    private fun showDeviceList(result: Int, lists: ArrayList<MyBleDevice>) {
        when (result) {
            0  -> {
                val intent = Intent(activity, EquipmentBondActivity::class.java)
                val bundle = Bundle()
                bundle.putSerializable("deivesList",lists)
                intent.putExtras(bundle)
                startActivityForResult(intent,11)
                mHandler.postDelayed({ initIdleStatus() },100)
            }
            2  -> {
                BleConnectionManger.mInstance.searchDevice()
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 11 && resultCode == BOND_SUCCEED) {
            // 连接成功
            initMonitorStatus()
        }else if (requestCode == 11 && resultCode == BOND_CANCEL) {
            // 暂时不连接
            initIdleStatus()
        }else if (requestCode == 11 && resultCode == SEARCH_FAIL){
            // 搜索失败
            initIdleStatus()
        }
    }

    override fun bleDispatchMessage(msg: Message) {
        if (view_search.visibility == View.VISIBLE) {
            when (msg.what) {
                RE_SCAN_DEVICE -> {
                    val result = msg.arg1
                    msg.obj?.let {
                        val myBleDevices = it as ArrayList<MyBleDevice>
                        showDeviceList(result, myBleDevices)
                    } ?: let {
                        BleConnectionManger.mInstance.searchDevice()
                    }

                }
            }
        }
    }

    override fun blePowerLevel(data: Byte) {
        fragmentMonitorViewHolder?.let {
            val level = data.toInt()
            it.powerText.text = ("$level" +"%")
            if (level <= 6){
                it.powerImg.setImageResource(R.drawable.monitor_power_0)
            }else if (level <= 20){
                it.powerImg.setImageResource(R.drawable.monitor_power_1)
            }else if (level <= 50){
                it.powerImg.setImageResource(R.drawable.monitor_power_2)
            }else if (level <= 80){
                it.powerImg.setImageResource(R.drawable.monitor_power_3)
            }else if (level <= 100){
                it.powerImg.setImageResource(R.drawable.monitor_power_4)
            }
        }
    }

    override fun reDeviceInfo(deviceInfo: DeviceInfo) {
        isBleUpdate = false
        BleSharedPrefHelperUtils.getInstance().coreInfo = deviceInfo
        NdkJniUtils.ecgProcessInit()
        BleConnectionManger.mInstance.fetchPowerLevel()
        initMonitorStatus()
        if (!startDfu(deviceInfo)) {
//            initMonitorStatus()
        }
    }

    override fun bleDeviceDisconnect() {
        initSearchStatus()
    }

    override fun bleCoreModule(data: Byte) {
        fragmentMonitorViewHolder?.let {
            when (data) {
                0.toByte() // 充电座
                ->{
                    it.heartText.text = "--"
                    it.heartLevel.text = ""
                }
                    0x01.toByte() // 上衣
                ->{

                    }
                    0x10.toByte() // 裤子
                ->{
                    it.heartText.text = "--"
                    it.heartLevel.text = ""
                    }
                    0x11.toByte() // 独立
                ->{
                    it.heartText.text = "--"
                    it.heartLevel.text = ""
                }
            }
        }
    }

    override fun heartBreathData(heart: Int, breath: Int) {
        fragmentMonitorViewHolder?.let {
            if (heart >= 0){
                it.heartText.text = "$heart"
                if (heart > 110){
                    it.heartLevel.text = "心率稍快"
                }else if (heart < 50){
                    it.heartLevel.text = "心率稍慢"
                }else{
                    it.heartLevel.text = "心率正常"
                }
                it.startRecord.text = "记录心电"
            }else{
                it.heartText.text = "--"
                it.heartLevel.text = ""
            }
        }
    }

    private var isShowDialog: Boolean = false
    private var isBleUpdate: Boolean = false
    private fun startDfu(coreInfo: DeviceInfo?): Boolean {
        //        if (1 > 0) {
        //            return false;
        //        }
        if (coreInfo == null || isShowDialog) {
            return false
        }
        val updateDfu = DfuLocalConfig.checkUpdateDfu(Integer.parseInt(coreInfo.hwVn), Integer.parseInt(coreInfo.swVn))
        if (updateDfu != null) {
            CoreUpdateHelper(activity, object : CoreUpdateHelper.OnBleUpdateDialogConfirmClick {
                override fun onClick() {
                    activity.showToast("准备升级，请稍后！")
                    BleConnectionManger.mInstance.closeDataChannel()
                    BleConnectionManger.mInstance.sendUpdateBleStart()
                    isShowDialog = false
                    isBleUpdate = true
                }
            }).setFwUpdate(DfuLocalConfig.DFU_MEG)
            isShowDialog = true
            return true
        } else {
            return false
        }
    }

    private fun initSearchStatus(){
        view_idle.removeAllViews()
        view_idle.visibility = View.GONE
        view_monitor.removeAllViews()
        view_monitor.visibility = View.GONE
        view_search.removeAllViews()

        view_search.visibility = View.VISIBLE
        val view = View.inflate(activity, R.layout.frame_layout_search, null)
        val tvNewEquipment = view.findViewById<LinearLayout>(R.id.tv_new_equipment)
        val tvInfo = view.findViewById<TextView>(R.id.tv_info)
        val btConnect = view.findViewById<Button>(R.id.bt_connect)
        searchAnimIg = view.findViewById<ImageView>(R.id.search_anim)
        val deviceInfo = BleSharedPrefHelperUtils.getInstance().coreInfo
        deviceInfo?.let {
            tvNewEquipment.visibility = View.VISIBLE
            tvInfo.text = "SN:" + deviceInfo.sn
            searchAnimIg?.let {
                startSearchAnim(it)
            }
            BleConnectionManger.mInstance.autoConnectBleBySN(deviceInfo.sn!!)
            if (isBleUpdate) {
                btConnect.text = "升级中..."
                tvNewEquipment.setOnClickListener {
                }
            }else{
                btConnect.text = "连接中..."
                tvNewEquipment.setOnClickListener {
                    BleSharedPrefHelperUtils.getInstance().coreInfo = null
                    initSearchStatus()
                }
            }
        }?:let {
            tvNewEquipment.visibility = View.GONE
            tvInfo.text = "请穿上服装并将设备安装在服装\n上，同时打开手机蓝牙\n(部分手机需要打开定位)"
            btConnect.text = "搜索中..."
            BleConnectionManger.mInstance.searchDevice()
            searchAnimIg?.let {
                startSearchAnim(it)
            }
        }
        view_search.addView(view)
    }

    private fun initMonitorStatus(){
        stopSearchAnim()
        view_search.removeAllViews()
        view_search.visibility = View.GONE
        view_idle.removeAllViews()
        view_idle.visibility = View.GONE
        view_monitor.removeAllViews()

        view_monitor.visibility = View.VISIBLE
        val view = View.inflate(activity, R.layout.frame_layout_monitor, null)
        val startRecord = view.findViewById<Button>(R.id.bt_monitor_start)
        startRecord.text = "初始化中..."
        startRecord.setOnClickListener({
            startRecordEcg()
        })
        val powerImg = view.findViewById<ImageView>(R.id.iv_power_level)
        val powerText = view.findViewById<TextView>(R.id.tv_power_level)
        val heartText = view.findViewById<TextView>(R.id.tv_heart_rate)
        val heartLevel = view.findViewById<TextView>(R.id.tv_heart_level)
        heartText.typeface = typeFaceYaHei
        heartText.text = "--"
        fragmentMonitorViewHolder = FragmentMonitorViewHolder(powerImg,powerText,heartText,heartLevel,startRecord)

        view_monitor.addView(view)
    }

    private fun initIdleStatus(){
        stopSearchAnim()
        view_monitor.removeAllViews()
        view_monitor.visibility = View.GONE
        view_search.removeAllViews()
        view_search.visibility = View.GONE
        view_idle.removeAllViews()

        view_idle.visibility = View.VISIBLE
        val view = View.inflate(activity, R.layout.frame_layout_idle, null)
        val btConnect = view.findViewById<Button>(R.id.idle_bt_connect)
        btConnect.setOnClickListener {
            initSearchStatus()
        }
        view_idle.addView(view)

    }

    private fun loadMoreEcgList(){
        val id = SharedPrefHelperUtils.getInstance().userId
        if (TextUtils.isEmpty(id) || id.toInt()<0){
            activity?.let {
                activity.showToast("请先登录！")
            }
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startActivity(Intent(activity, EcgListActivity2::class.java), ActivityOptions.makeSceneTransitionAnimation(activity).toBundle())
        }else{
            startActivity(Intent(activity, EcgListActivity2::class.java))
        }
    }

    private fun startRecordEcg(){
        if (FastClickUtil.isFastClick()) {
            return
        }
        val id = SharedPrefHelperUtils.getInstance().userId
        if (TextUtils.isEmpty(id) || id.toInt()<0){
            activity.showToast("请先登录！")
            return
        }
        val userId = SharedPrefHelperUtils.getInstance().userId
        if (userId.isEmpty()){
            activity.showToast("请先登录！")
            return
        }
        fragmentMonitorViewHolder?.let {
            if (it.startRecord.text.startsWith("初始化中")){
                activity.showToast("初始化中，请稍后！")
                return
            }
            val text = it.heartText.text
            if(TextUtils.isEmpty(text) || text=="--"){
                activity.showToast("初始化中，请稍后！")
                return
            }
        }
        var count = 5
        val dialog = InitMonitorDialog(activity)
        dialog.setTitleTxt("为保证数据有效性\n请保持静止状态")
        dialog.setConfirmTxt("$count")
        dialog.contentTv.typeface = typeFaceYaHei
        dialog.show()
        dialog.setCancelable(true)
        EcgOriginalData.mInstance.startSaveData()
        val disposable = Observable.interval(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnDispose { }
                .subscribe({
                    if (count == 0){
                        dialog.dismiss()
                        startActivity(Intent(activity, MonitorEcgActivity::class.java))
                    }else {
                        count--
                        dialog.setConfirmTxt("$count")
                    }
                }) { throwable -> throwable.printStackTrace() }
        dialog.setOnCancelListener {
            EcgOriginalData.mInstance.stopSaveData()
        }
        dialog.setOnDismissListener {
            disposable.dispose()
        }
    }

    private fun isBleOpen(context: Context): Boolean {
        val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val blueToothAdapter = bluetoothManager.adapter ?: return false

        if (!blueToothAdapter.isEnabled) {
            blueToothAdapter.enable()
            return false
        }
        return true
    }

    private fun startSearchAnim(searchAnim: ImageView) {
        searchAnimIg?.clearAnimation()
        val rotate = AnimationUtils.loadAnimation(activity, R.anim.equipment_search_anim_rotate)
        searchAnim.animation = rotate
        searchAnim.startAnimation(rotate)
    }

    private fun stopSearchAnim(){
        searchAnimIg?.clearAnimation()
    }

    class FragmentMonitorViewHolder(
            val powerImg: ImageView,
            val powerText: TextView,
            val heartText: TextView,
            val heartLevel: TextView,
            val startRecord: Button)
}