package cc.bodyplus.health.utils.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import cc.bodyplus.health.mvp.module.BPRecordModel
import cc.bodyplus.health.mvp.module.ReportInfo
import cc.bodyplus.health.mvp.module.ReportRecord
import com.alibaba.fastjson.JSON
import com.google.gson.Gson
import java.util.ArrayList

/**
 * Created by rui.gao on 2018-04-24.
 */
class HistoryDao {

    companion object {
        lateinit var mContext: Context
        internal lateinit var dbHelper: HistoryDataBaseHelper

        fun getInstance(context: Context): HistoryDao{
            val historyDao = HistoryDao()
            mContext = context
            dbHelper = HistoryDataBaseHelper.getDataHelperInstance(mContext)
            return historyDao
        }
    }


    /**
     * 增加一条ecg记录
     */
    fun addMonitorEcgRecord(userId: String, bpRecordModel: BPRecordModel,hasUpload : Int){
        val contentResolver = mContext.contentResolver

        val gs = Gson()
        val model = gs.toJson(bpRecordModel)
        val stamp = bpRecordModel.stamp.toString()
        val date = cc.bodyplus.health.utils.DateUtils.getDay(stamp)

        contentResolver.delete(History.MonitorRecord.CONTENT_URI, History.MonitorRecord.STAMP + " = ? ",
                arrayOf(stamp))
        val values = ContentValues()
        values.put(History.MonitorRecord.TYPE, bpRecordModel.type)
        values.put(History.MonitorRecord.HAS_UPLOAD, hasUpload)
        values.put(History.MonitorRecord.STAMP, stamp)
        values.put(History.MonitorRecord.DATE, date)
        values.put(History.MonitorRecord.USER_ID, userId)
        values.put(History.MonitorRecord.RECORD_MODEL, model)
        contentResolver.insert(History.MonitorRecord.CONTENT_URI, values)
    }

    /**
     * 获取某一条ecg记录
     */
    fun getMonitorEcgRecord(stamp: String, userId : String) : BPRecordModel?{
        var bean : BPRecordModel ?= null
        var cursor: Cursor? = null
        try {
            val selectQuery = "SELECT * FROM " + History.MonitorRecord.MONITOR_TABLE_NAME + " WHERE " +
                    History.MonitorRecord.STAMP + "=? AND " +
                    History.MonitorRecord.USER_ID + "=?"
            val db = dbHelper.writableDatabase
            cursor = db.rawQuery(selectQuery, arrayOf(stamp,userId))
            cursor?.run {
                if (moveToFirst()) {
                    do {
                        val model = getString(getColumnIndex(History.MonitorRecord.RECORD_MODEL))
                        bean = JSON.parseObject(model,BPRecordModel::class.java)
                    } while (cursor.moveToNext())
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            cursor?.let {
                it.close()
            }
        } finally {
            cursor?.let {
                it.close()
            }
        }
        return bean
    }

    /**
     * 修改某一条记录为已上传
     */
    fun modifyMonitorEcgRecordHasUpload(stamp: String, userId: String, ecgRecord: BPRecordModel) : Boolean{
        val gs = Gson()
        val model = gs.toJson(ecgRecord)
        val date = cc.bodyplus.health.utils.DateUtils.getDay(stamp)

        val contentResolver = mContext.contentResolver
        contentResolver.delete(History.MonitorRecord.CONTENT_URI, History.MonitorRecord.STAMP + " = ? ",
                arrayOf(stamp))
        val values = ContentValues()
        values.put(History.MonitorRecord.STAMP, stamp)
        values.put(History.MonitorRecord.HAS_UPLOAD, 1)
        values.put(History.MonitorRecord.DATE, date)
        values.put(History.MonitorRecord.USER_ID, userId)
        values.put(History.MonitorRecord.RECORD_MODEL, model)
        contentResolver.insert(History.MonitorRecord.CONTENT_URI, values)

        val rt = contentResolver.update(History.MonitorRecord.CONTENT_URI, values,
                History.MonitorRecord.STAMP + "= ? AND "+History.MonitorRecord.USER_ID + "= ?",
                arrayOf(stamp,userId))
        return rt > 0
    }



    /**
     * 获取某一天ecg记录
     */
    fun getOneDayEcgRecord(data: String, userId : String) : ArrayList<BPRecordModel>?{
        var bean :  ArrayList<BPRecordModel> ?= null
        var cursor: Cursor? = null
        try {
            val selectQuery = "SELECT * FROM " + History.MonitorRecord.MONITOR_TABLE_NAME + " WHERE " +
                    History.MonitorRecord.DATE + "=? AND " +
                    History.MonitorRecord.USER_ID + "=?"
            val db = dbHelper.writableDatabase
            cursor = db.rawQuery(selectQuery, arrayOf(data,userId))
            cursor?.run {
                bean = ArrayList()
                if (moveToFirst()) {
                    do {
                        val model = getString(getColumnIndex(History.MonitorRecord.RECORD_MODEL))
                        bean?.add(JSON.parseObject(model,BPRecordModel::class.java))
                    } while (cursor.moveToNext())
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            cursor?.let {
                it.close()
            }
        } finally {
            cursor?.let {
                it.close()
            }
        }
        return bean
    }


    /**
     * 获取未上传的ecg记录
     */
    fun getUnLoadEcgRecord(userId : String) : ArrayList<BPRecordModel>?{
        var bean :  ArrayList<BPRecordModel> ?= null
        var cursor: Cursor? = null
        try {
            val selectQuery = "SELECT * FROM " + History.MonitorRecord.MONITOR_TABLE_NAME + " WHERE " +
                    History.MonitorRecord.HAS_UPLOAD + "=? AND " +
                    History.MonitorRecord.USER_ID + "=?"
            val db = dbHelper.writableDatabase
            cursor = db.rawQuery(selectQuery, arrayOf("0",userId))
            cursor?.run {
                bean = ArrayList()
                if (moveToFirst()) {
                    do {
                        val model = getString(getColumnIndex(History.MonitorRecord.RECORD_MODEL))
                        bean?.add(JSON.parseObject(model,BPRecordModel::class.java))
                    } while (cursor.moveToNext())
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            cursor?.let {
                it.close()
            }
        } finally {
            cursor?.let {
                it.close()
            }
        }
        return bean
    }

    /**
     * 删除某一条ECG记录(未上传已失效的)
     */
    fun deleteEcgRecord(stamp : String ,userId : String) {
        try {
            val contentResolver = mContext.contentResolver
            contentResolver.delete(History.MonitorRecord.CONTENT_URI,
                    History.MonitorRecord.STAMP + " = ? AND" + History.MonitorRecord.USER_ID + " = ?",
                    arrayOf(stamp,userId))
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    //...................................//

    fun addMonthReportRecord(userId: String, listData : ArrayList<ReportRecord>){
        val db = dbHelper.writableDatabase
        try {
            db.beginTransaction()
            for (reportRecord in listData){

                val gs = Gson()
                val model = gs.toJson(reportRecord)
                val month = reportRecord.date
                db.delete(History.ReportListRecord.REPORT_TABLE_NAME, History.ReportListRecord.MONTH + " = ? ", arrayOf(month))

                val values = ContentValues()
                values.put(History.ReportListRecord.MONTH, month)
                values.put(History.ReportListRecord.USER_ID, userId)
                values.put(History.ReportListRecord.REPORT_RECORD_MODEL, model)
                db.insert(History.ReportListRecord.REPORT_TABLE_NAME, "", values)
            }
        }catch (e:Exception){
            e.printStackTrace()
        }finally {
            db.setTransactionSuccessful()
            db.endTransaction()
            db.close()
        }
    }

    fun queryReportList(userId : String) : ArrayList<ReportRecord>?{
        var bean :  ArrayList<ReportRecord> = ArrayList<ReportRecord>()
        var cursor: Cursor? = null
        try {
            val selectQuery = "SELECT * FROM " + History.ReportListRecord.REPORT_TABLE_NAME + " WHERE " +
                    History.ReportListRecord.USER_ID + "=?"
            val db = dbHelper.writableDatabase
            cursor = db.rawQuery(selectQuery, arrayOf(userId))
            if (cursor!!.moveToFirst()) {
                do {
                    val model = cursor.getString(cursor.getColumnIndex(History.ReportListRecord.REPORT_RECORD_MODEL))
                    var data = JSON.parseObject(model,ReportRecord::class.java)
//                    data.date = cursor.getString(cursor.getColumnIndex(History.ReportListRecord.MONTH))
                    bean?.add(data)

                } while (cursor.moveToNext())
            }

//            cursor?.run {
//                bean = ArrayList()
//                if (moveToFirst()) {
//                    do {
//                        val model = getString(getColumnIndex(History.ReportListRecord.REPORT_RECORD_MODEL))
//                        var data = JSON.parseObject(model,ReportRecord::class.java)
//                        data.date = getString(cursor.getColumnIndex(History.ReportListRecord.MONTH))
//
//                        bean?.add(data)
//                    } while (cursor.moveToNext())
//                }
//            }
        } catch (e: Exception) {
            e.printStackTrace()
            cursor?.let {
                it.close()
            }
        } finally {
            cursor?.let {
                it.close()
            }
        }
        return bean
    }

    /**
     * 增加一条详情记录
     */
    fun addReportDetails(userId: String,detectId:String, reportInfo: ReportInfo){
        val contentResolver = mContext.contentResolver
        val gs = Gson()
        val model = gs.toJson(reportInfo)

        contentResolver.delete(History.HistoryReport.CONTENT_URI, History.HistoryReport.USER_ID + " = ?  and "+ History.HistoryReport.DETECTID + " = ?",
                arrayOf(userId,detectId))

        val values = ContentValues()
        values.put(History.HistoryReport.DETECTID, detectId)
        values.put(History.HistoryReport.USER_ID, userId)
        values.put(History.HistoryReport.REPORT_DETAILS_MODEL, model)
        contentResolver.insert(History.HistoryReport.CONTENT_URI, values)
    }

    fun queryReportDetails(userId : String,detectId:String) : ReportInfo?{
        var bean :  ReportInfo?= null
        var cursor: Cursor? = null
        try {
            val selectQuery = "SELECT * FROM " + History.HistoryReport.HISTORY_TABLE_NAME + " WHERE " +
                    History.HistoryReport.USER_ID + "=? AND "+ History.HistoryReport.DETECTID +" =? "
            val db = dbHelper.writableDatabase
            cursor = db.rawQuery(selectQuery, arrayOf(userId,detectId))
            cursor?.run {
                if (moveToFirst()) {
                    do {
                        val model = getString(getColumnIndex(History.HistoryReport.REPORT_DETAILS_MODEL))
                        bean = JSON.parseObject(model,ReportInfo::class.java)
                    } while (cursor.moveToNext())
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            cursor?.let {
                it.close()
            }
        } finally {
            cursor?.let {
                it.close()
            }
        }
        return bean
    }

//    /**
//     * 本地数据库增加历史记录
//     * @param historyBean
//     */
//    fun addHistoryBean(historyBean: HistoryBean?) {
//        if (historyBean == null) {
//            return
//        }
//        val contentResolver = mContext.contentResolver
//        val dateStamp = historyBean.detectTime
//        contentResolver.delete(History.HistoryReport.CONTENT_URI, History.HistoryReport.START_DATE + " = ? ",
//                arrayOf(dateStamp))
//        val values = ContentValues()
//        values.put(History.HistoryReport.TOTAL_TIME, historyBean.totalTime)
//        values.put(History.HistoryReport.HAS_SEE, historyBean.hasSee)
//        values.put(History.HistoryReport.DATE, historyBean.date)
//        values.put(History.HistoryReport.START_DATE, historyBean.detectTime)
//        values.put(History.HistoryReport.USER_ID, historyBean.userID)
//
//        values.put(History.HistoryReport.AVG_HEART, historyBean.stamp)
//        values.put(History.HistoryReport.MAX_HEART, historyBean.sleepLevel)
//        values.put(History.HistoryReport.MIN_HEART , historyBean.detectFile)
//        values.put(History.HistoryReport.SLOW_HEART , historyBean.detectType)
//        values.put(History.HistoryReport.QUICK_HEART  , historyBean.detectType)
//        values.put(History.HistoryReport.ATRIAL_FIBRILLATION  , historyBean.detectType)
//        values.put(History.HistoryReport.STOP_BLOOMER  , historyBean.detectType)
//        values.put(History.HistoryReport.HEART_FAST_BEAT  , historyBean.detectType)
//        values.put(History.HistoryReport.HEART_FAST_BEAT_TACHYCARDIA  , historyBean.detectType)
//        values.put(History.HistoryReport.TWO_COUPLETLAW  , historyBean.detectType)
//        values.put(History.HistoryReport.DIAGNOSTIC_ANALYSIS  , historyBean.detectType)
//        values.put(History.HistoryReport.DIAGNOSTIC_DOCTOR  , historyBean.detectType)
//        values.put(History.HistoryReport.DIAGNOSTIC_RESULT  , historyBean.detectType)
//        values.put(History.HistoryReport.DIAGNOSTIC_CHARGE_DOCTOR   , historyBean.detectType)
//        values.put(History.HistoryReport.DIAGNOSTIC_CARDIOGRAM     , historyBean.detectType)
//
//        contentResolver.insert(History.HistoryReport.CONTENT_URI, values)
//    }
//
//    fun queryAllMothInHistory(): ArrayList<String> {
//        val months = ArrayList<String>()
//        var cursor: Cursor? = null
//        try {
//            val selectQuery = "SELECT * FROM " + History.HistoryReport.HISTORY_TABLE_NAME
//            val db = dbHelper.writableDatabase
//            cursor = db.rawQuery(selectQuery, null)
//            if (cursor!!.moveToFirst()) {
//                do {
//                    val month = cursor.getString(cursor.getColumnIndex(History.HistoryReport.DATE))
//                    if (!months.contains(month)) {
//                        months.add(month)
//                    }
//                } while (cursor.moveToNext())
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//            if (cursor != null) {
//                cursor.close()
//            }
//        } finally {
//            if (cursor != null) {
//                cursor.close()
//            }
//        }
//        return months
//    }
//
//    fun queryHistoryReport(monthFirstDay: String): ArrayList<HistoryBean> {
//        val list = ArrayList<HistoryBean>()
//        var cursor: Cursor? = null
//        try {
//            val selectQuery = "SELECT * FROM " + History.HistoryReport.HISTORY_TABLE_NAME + " WHERE " + History.HistoryReport.DATE + "=?"
//            val db = dbHelper.writableDatabase
//            cursor = db.rawQuery(selectQuery, arrayOf(monthFirstDay))
//
//            if (cursor!!.moveToFirst()) {
//                do {
//                    val bean = HistoryBean()
//
//                    bean.totalTime = cursor.getString(cursor.getColumnIndex(History.HistoryReport.TOTAL_TIME))
//                    bean.date = cursor.getString(cursor.getColumnIndex(History.HistoryReport.DATE))
//                    bean.detectTime = cursor.getString(cursor.getColumnIndex(History.HistoryReport.START_DATE))
//                    bean.recordStatus = cursor.getString(cursor.getColumnIndex(History.HistoryReport.SLOW_HEART))
//                    bean.userID = cursor.getString(cursor.getColumnIndex(History.HistoryReport.USER_ID))
//                    bean.stamp = cursor.getString(cursor.getColumnIndex(History.HistoryReport.START_DATE))
//                    bean.hasSee = cursor.getString(cursor.getColumnIndex(History.HistoryReport.HAS_SEE))
//                    bean.detectType = cursor.getString(cursor.getColumnIndex(History.HistoryReport.AVG_HEART))
//                    bean.sleepLevel = cursor.getString(cursor.getColumnIndex(History.HistoryReport.HEART_FAST_BEAT))
//                    bean.detectFile = cursor.getString(cursor.getColumnIndex(History.HistoryReport.HEART_FAST_BEAT_TACHYCARDIA))
//                    list.add(bean)
//                } while (cursor.moveToNext())
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//            if (cursor != null) {
//                cursor.close()
//            }
//        } finally {
//            if (cursor != null) {
//                cursor.close()
//            }
//        }
//        return list
//    }
//
//    fun queryHistoryReportNOUpload(): ArrayList<HistoryBean> {
//        val list = ArrayList<HistoryBean>()
//        var cursor: Cursor? = null
//        try {
//            val selectQuery = "SELECT * FROM " + History.HistoryReport.HISTORY_TABLE_NAME + " WHERE " + History.HistoryReport.HAS_SEE + "=?"
//            val db = dbHelper.writableDatabase
//            cursor = db.rawQuery(selectQuery, arrayOf("0"))
//
//            if (cursor!!.moveToFirst()) {
//                do {
//                    val bean = HistoryBean()
//                    bean.totalTime = cursor.getString(cursor.getColumnIndex(History.HistoryReport.TOTAL_TIME))
//                    bean.date = cursor.getString(cursor.getColumnIndex(History.HistoryReport.DATE))
//                    bean.detectTime = cursor.getString(cursor.getColumnIndex(History.HistoryReport.START_DATE))
//                    bean.recordStatus = cursor.getString(cursor.getColumnIndex(History.HistoryReport.HISTORY_TABLE_NAME))
//                    bean.userID = cursor.getString(cursor.getColumnIndex(History.HistoryReport.USER_ID))
//                    bean.stamp = cursor.getString(cursor.getColumnIndex(History.HistoryReport.HEART_FAST_BEAT_TACHYCARDIA))
//                    bean.hasSee = cursor.getString(cursor.getColumnIndex(History.HistoryReport.TOTAL_TIME))
//                    bean.detectType = cursor.getString(cursor.getColumnIndex(History.HistoryReport.HAS_SEE))
//                    bean.sleepLevel = cursor.getString(cursor.getColumnIndex(History.HistoryReport.SLOW_HEART))
//                    list.add(bean)
//                } while (cursor.moveToNext())
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//            if (cursor != null) {
//                cursor.close()
//            }
//        } finally {
//            if (cursor != null) {
//                cursor.close()
//            }
//        }
//        return list
//    }
//
//    /**
//     * 查询历史记录
//     * @return
//     */
//    fun queryHistoryReport(): ArrayList<HistoryBean> {
//        val list = ArrayList<HistoryBean>()
//        var cursor: Cursor? = null
//        try {
//            val selectQuery = "SELECT * FROM " + History.HistoryReport.HISTORY_TABLE_NAME
//            val db = dbHelper.writableDatabase
//            cursor = db.rawQuery(selectQuery, null)
//
//            if (cursor!!.moveToFirst()) {
//                do {
//                    val bean = HistoryBean()
//                    bean.totalTime = cursor.getString(cursor.getColumnIndex(History.HistoryReport.TOTAL_TIME))
//                    bean.date = cursor.getString(cursor.getColumnIndex(History.HistoryReport.DATE))
//                    bean.detectTime = cursor.getString(cursor.getColumnIndex(History.HistoryReport.START_DATE))
//                    bean.recordStatus = cursor.getString(cursor.getColumnIndex(History.HistoryReport.START_DATE))
//                    bean.userID = cursor.getString(cursor.getColumnIndex(History.HistoryReport.USER_ID))
//                    bean.stamp = cursor.getString(cursor.getColumnIndex(History.HistoryReport.STOP_BLOOMER))
//                    bean.hasSee = cursor.getString(cursor.getColumnIndex(History.HistoryReport.HAS_SEE))
//                    list.add(bean)
//                } while (cursor.moveToNext())
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//            if (cursor != null) {
//                cursor.close()
//            }
//        } finally {
//            if (cursor != null) {
//                cursor.close()
//            }
//        }
//        return list
//    }
//
//    fun modifySportReportHasUpload(historyBean: HistoryBean?, status: Int): Boolean {
//        if (historyBean == null) {
//            return false
//        }
//        val contentResolver = mContext.contentResolver
//        val values = ContentValues()
//        values.put(History.HistoryReport.TOTAL_TIME, historyBean!!.totalTime)
//        values.put(History.HistoryReport.HAS_SEE, status.toString())
//        values.put(History.HistoryReport.DATE, historyBean!!.date)
//        values.put(History.HistoryReport.START_DATE, historyBean!!.detectTime)
//        values.put(History.HistoryReport.STOP_BLOOMER, historyBean!!.recordStatus)
//        values.put(History.HistoryReport.USER_ID, historyBean!!.userID)
//        values.put(History.HistoryReport.START_DATE, historyBean!!.stamp)
//        values.put(History.HistoryReport.SLOW_HEART, historyBean!!.sleepLevel)
//        values.put(History.HistoryReport.CONTENT_ITEM_TYPE, historyBean!!.detectFile)
//        values.put(History.HistoryReport.AVG_HEART, historyBean!!.detectType)
//
//        val rt = contentResolver.update(History.HistoryReport.CONTENT_URI, values, History.HistoryReport.START_DATE + "= ? ", arrayOf(historyBean!!.detectTime))
//        return rt > 0
//    }
//
//
//    /**
//     * 从网络上拉取到本地
//     * @param mReportRecordList
//     */
//    fun saveReportToDB(mReportRecordList: ArrayList<ReportRecord>) {
//        val db: SQLiteDatabase
//        try {
//            db = dbHelper.writableDatabase
//            db.beginTransaction()
//            for (reportRecord in mReportRecordList) {
//                for (item in reportRecord.dataList) {
//                    val values = ContentValues()
//                    values.put(History.HistoryReport.HAS_SEE, 1.toString())
//                    values.put(History.HistoryReport.TOTAL_TIME, item.totalTime)
//                    values.put(History.HistoryReport.DATE, reportRecord.date)
//                    values.put(History.HistoryReport.START_DATE, item.detectTime)
//                    values.put(History.HistoryReport.SLOW_HEART, item.recordStatus)
//                    values.put(History.HistoryReport.USER_ID, item.userID)
//                    values.put(History.HistoryReport.START_DATE, item.stamp)
//
//                    db.delete(History.HistoryReport.HISTORY_TABLE_NAME, History.HistoryReport.START_DATE + " = ? ", arrayOf(item.detectTime))
//
//                    val insert = db.insert(History.HistoryReport.HISTORY_TABLE_NAME, null, values)
//                    if (insert < 0) {
//                        break
//                    }
//                }
//            }
//            db.endTransaction()
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//
//    }
//
//    fun saveHistoryBeanToDB(historyBeans: ArrayList<HistoryBean>) {
//        try {
//            val contentResolver = mContext.contentResolver
//            for (item in historyBeans) {
//                val values = ContentValues()
//                values.put(History.HistoryReport.HAS_SEE, 1.toString())
//                values.put(History.HistoryReport.TOTAL_TIME, item.totalTime)
//                values.put(History.HistoryReport.DATE, item.date)
//                values.put(History.HistoryReport.START_DATE, item.detectTime)
//                values.put(History.HistoryReport.CONTENT_TYPE, item.recordStatus)
//                values.put(History.HistoryReport.USER_ID, item.userID)
//                values.put(History.HistoryReport.SLOW_HEART, item.stamp)
//                values.put(History.HistoryReport.TWO_COUPLETLAW, item.sleepLevel)
//                values.put(History.HistoryReport.AVG_HEART, item.detectFile)
//                values.put(History.HistoryReport.HEART_FAST_BEAT_TACHYCARDIA, item.detectType)
//
//                contentResolver.delete(History.HistoryReport.CONTENT_URI, History.HistoryReport.START_DATE + " = ? ", arrayOf(item.detectTime))
//
//                val insert = contentResolver.insert(History.HistoryReport.CONTENT_URI, values) ?: break
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//
//    }
//
//    fun queryReportBean(): ArrayList<HistoryBean> {
//        val list = ArrayList<HistoryBean>()
//        var cursor: Cursor? = null
//        try {
//            val selectQuery = "SELECT * FROM " + History.HistoryReport.HISTORY_TABLE_NAME
//            val db = dbHelper.writableDatabase
//            cursor = db.rawQuery(selectQuery, null)
//
//            if (cursor!!.moveToFirst()) {
//                do {
//                    val bean = HistoryBean()
//                    bean.totalTime = cursor.getString(cursor.getColumnIndex(History.HistoryReport.TOTAL_TIME))
//                    bean.date = cursor.getString(cursor.getColumnIndex(History.HistoryReport.DATE))
//                    bean.detectTime = cursor.getString(cursor.getColumnIndex(History.HistoryReport.START_DATE))
//                    bean.recordStatus = cursor.getString(cursor.getColumnIndex(History.HistoryReport.START_DATE))
//                    bean.userID = cursor.getString(cursor.getColumnIndex(History.HistoryReport.USER_ID))
//                    bean.stamp = cursor.getString(cursor.getColumnIndex(History.HistoryReport.HISTORY_TABLE_NAME))
//                    bean.hasSee = cursor.getString(cursor.getColumnIndex(History.HistoryReport.HAS_SEE))
//                    bean.sleepLevel = cursor.getString(cursor.getColumnIndex(History.HistoryReport.START_DATE))
//                    bean.detectFile = cursor.getString(cursor.getColumnIndex(History.HistoryReport.SLOW_HEART))
//                    bean.detectType = cursor.getString(cursor.getColumnIndex(History.HistoryReport.CONTENT_TYPE))
//                    list.add(bean)
//                } while (cursor.moveToNext())
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//            if (cursor != null) {
//                cursor.close()
//            }
//        } finally {
//            if (cursor != null) {
//                cursor.close()
//            }
//        }
//        return list
//    }

//    fun queryReport(): ReportInfo {
//        val reportInfo = ReportInfo()
//        val list = ArrayList<HistoryBean>()
//        var cursor: Cursor? = null
//        try {
//            val selectQuery = "SELECT * FROM " + History.HistoryReport.HISTORY_TABLE_NAME
//            val db = dbHelper.writableDatabase
//            cursor = db.rawQuery(selectQuery, null)
//
//            if (cursor!!.moveToFirst()) {
//                do {
//                    val bean = HistoryBean()
//                    bean.totalTime = cursor.getString(cursor.getColumnIndex(History.HistoryReport.TOTAL_TIME))
//                    bean.date = cursor.getString(cursor.getColumnIndex(History.HistoryReport.DATE))
//                    bean.detectTime = cursor.getString(cursor.getColumnIndex(History.HistoryReport.START_DATE))
//                    bean.recordStatus = cursor.getString(cursor.getColumnIndex(History.HistoryReport.ECG_STATUS))
//                    bean.userID = cursor.getString(cursor.getColumnIndex(History.HistoryReport.USER_ID))
//                    bean.stamp = cursor.getString(cursor.getColumnIndex(History.HistoryReport.STAMP))
//                    bean.hasUpload = cursor.getString(cursor.getColumnIndex(History.HistoryReport.HAS_UPLOAD))
//                    bean.sleepLevel = cursor.getString(cursor.getColumnIndex(History.HistoryReport.SLEEP_RATE))
//                    bean.detectFile = cursor.getString(cursor.getColumnIndex(History.HistoryReport.DETECT_FILE))
//                    bean.detectType = cursor.getString(cursor.getColumnIndex(History.HistoryReport.HISTORY_TYPE))
//                    list.add(bean)
//                } while (cursor.moveToNext())
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//            if (cursor != null) {
//                cursor.close()
//            }
//        } finally {
//            if (cursor != null) {
//                cursor.close()
//            }
//        }
//        if (list != null) {
//            reportInfo.data = ArrayList<Any>()
//            var date: String? = null
//            var record: ReportRecord? = null
//
//            for (historyBean in list) {
//                if (date != null && date == historyBean.date) {
//                    record!!.dataList.add(historyBean)
//                } else {
//                    date = historyBean.date
//                    record = ReportRecord()
//                    record!!.dataList = ArrayList<Any>()
//                    record!!.date = date
//                    record!!.dataList.add(historyBean)
//                    reportInfo.data.add(record)
//                }
//            }
//
//            //            Collections.reverse(reportInfo.data);
//            //            for (ReportRecord reportRecord : reportInfo.data){
//            //                Collections.reverse(reportRecord.dataList);
//            //            }
//        }
//        return reportInfo
//    }
//
//    /**
//     * ************************************************  MonitorRecord 存储  *******************************************
//     * @param ecgJson
//     */
//
//    fun saveAutoEcgRecordToDB(bean: HistoryRecordBean, ecgJson: String) {
//        saveEcgRecordToDB(History.EcgRecord.TYPE_AUTO, bean, ecgJson)
//    }
//
//    fun saveUserEcgRecordToDB(bean: HistoryRecordBean, ecgJson: String) {
//        saveEcgRecordToDB(History.EcgRecord.USER_ID, bean, ecgJson)
//    }
//
//    private fun saveEcgRecordToDB(type: Int, bean: HistoryRecordBean, ecgJson: String) {
//        try {
//            val contentResolver = mContext.contentResolver
//            val values = ContentValues()
//            values.put(History.EcgRecord.DATA, ecgJson)
//            values.put(History.EcgRecord.STAMP, bean.stamp)
//            values.put(History.EcgRecord.DESC, bean.desc)
//            values.put(History.EcgRecord.AVGHR, bean.avgHR)
//            values.put(History.EcgRecord.HAS_UPLOAD, type)
//            contentResolver.insert(History.EcgRecord.CONTENT_URI, values)
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//
//    }
//
//    fun getUserEcgRecords(): ArrayList<HistoryRecordBean> {
//        val list = ArrayList<HistoryRecordBean>()
//
//        var cursor: Cursor? = null
//        try {
//            val selectQuery = "SELECT * FROM " + History.EcgRecord.ECG_TABLE_NAME + " WHERE " + History.EcgRecord.HAS_UPLOAD + "=?"
//            val db = dbHelper.writableDatabase
//            cursor = db.rawQuery(selectQuery, arrayOf(String.valueOf(History.EcgRecord.USER_ID)))
//            if (cursor!!.moveToFirst()) {
//                do {
//                    val bean = HistoryRecordBean()
//                    bean.stamp = cursor.getString(cursor.getColumnIndex(History.EcgRecord.STAMP))
//                    bean.avgHR = cursor.getString(cursor.getColumnIndex(History.EcgRecord.AVGHR))
//                    bean.desc = cursor.getString(cursor.getColumnIndex(History.EcgRecord.DESC))
//                    list.add(bean)
//                } while (cursor.moveToNext())
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//            if (cursor != null) {
//                cursor.close()
//            }
//        } finally {
//            if (cursor != null) {
//                cursor.close()
//            }
//        }
//        return list
//    }
//
//    fun getAllEcgRecords(): ArrayList<BPRecordModel> {
//        val list = ArrayList<BPRecordModel>()
//
//        var cursor: Cursor? = null
//        try {
//            val selectQuery = "SELECT * FROM " + History.EcgRecord.ECG_TABLE_NAME + " WHERE " + History.EcgRecord.HAS_UPLOAD + "=?"
//            val db = dbHelper.writableDatabase
//            cursor = db.rawQuery(selectQuery, arrayOf(String.valueOf(History.EcgRecord.USER_ID)))
//            if (cursor!!.moveToFirst()) {
//                do {
//                    val jsonByStamp = cursor.getString(cursor.getColumnIndex(History.EcgRecord.DATA))
//                    val bpRecordModel = JSON.parseObject(jsonByStamp, BPRecordModel::class.java)
//                    list.add(bpRecordModel)
//                } while (cursor.moveToNext())
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//            if (cursor != null) {
//                cursor.close()
//            }
//        } finally {
//            if (cursor != null) {
//                cursor.close()
//            }
//        }
//        return list
//    }
//
//
//    fun getAutoEcgRecords(): ArrayList<HistoryRecordBean> {
//        val list = ArrayList<HistoryRecordBean>()
//
//        var cursor: Cursor? = null
//        try {
//            val selectQuery = "SELECT * FROM " + History.EcgRecord.ECG_TABLE_NAME + " WHERE " + History.EcgRecord.HAS_UPLOAD + "=?"
//            val db = dbHelper.writableDatabase
//            cursor = db.rawQuery(selectQuery, arrayOf(String.valueOf(History.EcgRecord.TYPE_AUTO)))
//            if (cursor!!.moveToFirst()) {
//                do {
//                    val bean = HistoryRecordBean()
//                    bean.stamp = cursor.getString(cursor.getColumnIndex(History.EcgRecord.STAMP))
//                    bean.avgHR = cursor.getString(cursor.getColumnIndex(History.EcgRecord.AVGHR))
//                    bean.desc = cursor.getString(cursor.getColumnIndex(History.EcgRecord.DESC))
//                    list.add(bean)
//                } while (cursor.moveToNext())
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//            if (cursor != null) {
//                cursor.close()
//            }
//        } finally {
//            if (cursor != null) {
//                cursor.close()
//            }
//        }
//        return list
//    }

    fun getUserMonitorRecordJsonByStamp(stamp: String): String? {
        var json: String? = null
        var cursor: Cursor? = null
        try {
            val selectQuery = "SELECT * FROM " + History.MonitorRecord.MONITOR_TABLE_NAME + " WHERE " + History.MonitorRecord.USER_ID + "=?"
            val db = dbHelper.writableDatabase
            cursor = db.rawQuery(selectQuery, arrayOf(stamp))
            if (cursor!!.moveToFirst()) {
                json = cursor.getString(cursor.getColumnIndex(History.MonitorRecord.DATE))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            if (cursor != null) {
                cursor.close()
            }
        } finally {
            if (cursor != null) {
                cursor.close()
            }
        }
        return json
    }

    fun deleteAllMonitorRecord() {
        try {
            val db = dbHelper.writableDatabase
            db.execSQL("delete from " + History.MonitorRecord.MONITOR_TABLE_NAME)
            db.beginTransaction()
            db.endTransaction()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun deleteHistoryRecord() {
        try {
            val db = dbHelper.writableDatabase
            db.execSQL("delete from " + History.HistoryReport.HISTORY_TABLE_NAME)
            db.beginTransaction()
            db.endTransaction()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

}