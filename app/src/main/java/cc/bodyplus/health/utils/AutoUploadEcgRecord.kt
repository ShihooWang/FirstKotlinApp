package cc.bodyplus.health.utils

import android.content.Context
import cc.bodyplus.health.App
import cc.bodyplus.health.ext.tryCatch
import cc.bodyplus.health.mvp.module.BPRecordModel
import cc.bodyplus.health.mvp.module.UploadResultBean
import cc.bodyplus.health.net.util.NetLoginConfig
import cc.bodyplus.health.utils.db.HistoryDao
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*

/**
 * Created by shihoo.wang on 2018/5/17.
 * Email shihu.wang@bodyplus.cc
 */
class AutoUploadEcgRecord(private val userId : String,private val mContext : Context){


    fun uploadDbRecord(){
        val unLoadEcgRecord = HistoryDao.getInstance(mContext).getUnLoadEcgRecord(userId)
        unLoadEcgRecord?.let {
            uploadRecordList(it)
        }
    }


    private fun uploadRecordList(list : ArrayList<BPRecordModel>){
        tryCatch({
            it.printStackTrace()
        }) {
            if (!list.isEmpty()) {
                val bp = list.removeAt(0)
                val callback = object : CallBack {
                    override fun succeed() {
                        uploadRecordList(list)
                    }

                    override fun fail(msg: String) {

                    }
                }
                uploadEcgData(bp,callback,false)

            }
        }
    }


    fun uploadEcgData(record : BPRecordModel,callback : CallBack,isMainThread : Boolean){
        var ecgName = "未见异常"
        val result = EcgStatusUtils.generateEcgStatus(record.desc)
        if (result != EcgStatusUtils.NO_ERROR){
            ecgName = result
        }
        val params = mapOf<String,String>(
                "ecg" to Arrays.toString(record.dealArr)
                ,"tags" to record.tag.toString()
                ,"timestamp" to ""+record.stamp
                ,"record" to record.edit
                ,"diagnosisType" to record.desc.toString()
                ,"avgHr" to ""+record.avgHR
                ,"ecgName" to ecgName)

        App.instance.appComponent.getDataApi().doUploadEcgData(NetLoginConfig.UPLOAD_ECG, params)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(if (isMainThread)AndroidSchedulers.mainThread() else Schedulers.io())
                .subscribe({bean: UploadResultBean? ->
                        if (bean?.timestamp?.length!! > 1){
                            HistoryDao.getInstance(mContext).modifyMonitorEcgRecordHasUpload(""+record.stamp,userId,record)
                        }else{
                            HistoryDao.getInstance(mContext).deleteEcgRecord(""+record.stamp,userId)
                        }
                    callback.succeed()
                   },{ throwable: Throwable ->
                    callback.fail(throwable.message.toString())
                })
    }


    interface CallBack {
        fun succeed()
        fun fail(msg : String)
    }
}