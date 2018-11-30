package cc.bodyplus.health.utils.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * Created by rui.gao on 2018-04-24.
 */
class HistoryDataBaseHelper(context: Context?, name: String?, factory: SQLiteDatabase.CursorFactory?, version: Int) : SQLiteOpenHelper(context, name, factory, version) {

    companion object {
        var dbHelper: HistoryDataBaseHelper? = null
        public fun getDataHelperInstance(context: Context): HistoryDataBaseHelper {
            if (dbHelper == null) {
                dbHelper = HistoryDataBaseHelper(context, History.DATABASE_NAME, null, History.DATABASE_VERSION)
            }
            return dbHelper as HistoryDataBaseHelper
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(History.HistoryReport.createSportReportTBSQL)
        db.execSQL(History.MonitorRecord.createMonitorRecordTBSQL)
        db.execSQL(History.ReportListRecord.createReportListTBSQL)
    }

    /**
     * 系统最低版本从5开始
     */
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        //假如当前版本为7
        //		if (oldVersion < 7) { // 旧的数据库版本为5 和 6的需要增加数据库 同时对运动数据库修改
        //			db.execSQL(History.HistoryReport.createSportReportTBSQL);
        //		}else {
        //			deleteDB(db);
        //			onCreate(db);
        //		}
        deleteDB(db)
        onCreate(db)
    }

    //delete 表
    private fun deleteDB(db: SQLiteDatabase) {
        db.execSQL("drop table if exists " + History.HistoryReport.HISTORY_TABLE_NAME)
        db.execSQL("drop table if exists " + History.MonitorRecord.MONITOR_TABLE_NAME)
        db.execSQL("drop table if exists " + History.ReportListRecord.REPORT_TABLE_NAME)
    }

    fun deleteDB(context: Context): Boolean {
        return context.deleteDatabase(History.DATABASE_NAME)
    }
}