package cc.bodyplus.health.mvp.view.monitor.activity

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Message
import android.view.View
import cc.bodyplus.health.R
import cc.bodyplus.health.ext.showToast
import cc.bodyplus.health.mvp.view.BaseActivity
import cc.bodyplus.health.utils.Config
import cc.bodyplus.health.utils.ShareInfo
import kotlinx.android.synthetic.main.activity_ecg_share.*
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

/**
 * Created by shihoo.wang on 2018/5/10.
 * Email shihu.wang@bodyplus.cc
 */
class EcgSharePngActivity : BaseActivity() {


    private val png = ".png"
    private var fileName: String? = null
    private var stamp: Long = 0
    private var avgHr: Int = 0
    private var dataArray: FloatArray? = null

    override fun initInject() {
    }

    override fun layoutId(): Int {
        return R.layout.activity_ecg_share
    }

    override fun initView() {
        png_ic_back.setOnClickListener { onBackPressed() }
        text_share.setOnClickListener { sharePng() }
    }


    override fun initData() {
        val extras = intent.extras
        extras?.let {
            dataArray = extras.getFloatArray("ecgRecordArray")
            stamp = extras.getLong("stamp")
            avgHr = extras.getInt("avgHr")
            checkPng()
        }
    }


    override fun onHandle(msg: Message) {
        if (msg.what == 1) {
            postDelayed(Runnable { checkPng() }, 500)
        }
    }

    private fun checkPng() {
        try {
            fileName = Config.DATA_ECG_PNG + "/" + stamp + png
            val dir = Config.DATA_ECG_PNG
            val dirFile = File(dir)
            if (!dirFile.exists()) {
                dirFile.mkdirs()
                showNoEcgPng(dataArray)
            } else {
                val f = File(fileName)
                if (!f.exists()) {
                    showNoEcgPng(dataArray)
                } else {
                    showHasEcgPng()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            finish()
        }

    }

    private fun showHasEcgPng() {
        val bitmap = BitmapFactory.decodeFile(fileName)
        ecg_share_ig.visibility = View.GONE
        photo_view.visibility = View.VISIBLE
        photo_view.setImageBitmap(bitmap)
    }

    private fun showNoEcgPng(dataArray: FloatArray?) {
        ecg_share_ig.setBackgroundColor(Color.WHITE)
        ecg_share_ig.setStamp(stamp)
        ecg_share_ig.setHr(avgHr)
        ecg_share_ig.setData(dataArray)
        mHandler.postDelayed({
            val bitmap = convertViewToBitmap(ecg_share_ig)
            savePic(bitmap)
        }, 500)
    }

    private fun savePic(bitmap: Bitmap?) {
        Thread(Runnable {
            if (bitmap != null) {
                var os: FileOutputStream? = null
                try {
                    os = FileOutputStream(File(fileName))
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, os)
                    os.flush()
                    os.close()
                    mHandler.sendEmptyMessage(1)
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }).start()
    }

    private fun convertViewToBitmap(view: View): Bitmap {
        view.buildDrawingCache()
        return view.drawingCache
    }

    private fun sharePng(){
        val f = File(fileName)
        if (!f.exists()) {
            showToast("文件不存在！")
        } else {
            ShareInfo.showSharePic("", "", fileName)
        }
    }
}