package cc.bodyplus.health.utils.db

import android.net.Uri
import android.provider.BaseColumns

/**
 * Created by rui.gao on 2018-04-24.
 */
class History {

    companion object {
        // 数据库名称
        val AUTHORITY = "cc.bodyplus.health.provider"
        val DATABASE_NAME = "bodyplus.health.db"
        // 数据库版本
        val DATABASE_VERSION = 1
        val HISTORY_ITEM = 0x10
        val HISTORY_REPORT_ID = 0x11
        val MONITOR_ITEM = 0x20
        val MONITOR_ID = 0x21
    }


    /**
     * 检测总记录表
     */
    class HistoryReport : BaseColumns {
        
        companion object {
            val HISTORY_TABLE_NAME = "HistoryReport" // 数据库表名
            val CONTENT_URI: Uri get() = Uri.parse("content://"+ AUTHORITY+"/"+ HISTORY_TABLE_NAME)
            val CONTENT_TYPE = "vnd.android.cursor.dir/" + HISTORY_TABLE_NAME
            val CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + HISTORY_TABLE_NAME
            val DEFAULT_ORDER = "_id asc"

            val ID = "_id"
            val DETECTID = "detectId" //ID
            val USER_ID = "user_id" // 用户ID
            val REPORT_DETAILS_MODEL = "report_details_model" //详情数据

            val createSportReportTBSQL = ("create table " + HISTORY_TABLE_NAME + "("
                    + ID + " INTEGER primary key autoincrement,"
                    + DETECTID + " text,"
                    + USER_ID + " text,"
                    + REPORT_DETAILS_MODEL + " text)")
        }
    }

    /**
     * 手动记录的ECG
     */
    class MonitorRecord : BaseColumns {
       
        companion object {
            val MONITOR_TABLE_NAME = "MonitorRecord" // 数据库表名
            val CONTENT_URI = Uri.parse("content://"+ AUTHORITY+"/"+ MONITOR_TABLE_NAME)
            val CONTENT_TYPE = "vnd.android.cursor.dir/" + MONITOR_TABLE_NAME
            val CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + MONITOR_TABLE_NAME
            val DEFAULT_ORDER = "_id asc"

            /**
             *  public int avgHR;    // 平均心率
                public long stamp;    // 该段心电数据的起始时间戳
                public ArrayList<Integer> desc = new ArrayList<>();     // 诊断状态 20长度数组
                //    public ArrayList<BPECGOriginal> ecgOriginals = new ArrayList<>(); // Ecg数据集合
                public float gain;     // 增益
                public float speed;    // 走速
                public int[] dealArr;  // 滤波后的数据
                public int[] tag;  // 状况描述（选择标签）
                public String edit;  // 状况描述（手动输入）
             */
            val ID = "_id"
            val HAS_UPLOAD = "has_upload"
            val DATE = "date"  //天
            val USER_ID = "user_id"
            val STAMP = "stamp"
            val TYPE = "type" // 数据来源(app产生或者后台截取的片段)

            val RECORD_MODEL = "record_model" // 数据JSON

            val createMonitorRecordTBSQL = ("create table " + MONITOR_TABLE_NAME + "("
                    + ID + " INTEGER primary key autoincrement,"
                    + HAS_UPLOAD + " int default 0,"
                    + TYPE + " int default 0,"
                    + DATE + " text,"
                    + USER_ID + " text,"
                    + STAMP + " text,"
                    + RECORD_MODEL + " text)")
        }
    }

    /**
     * 手动记录的ECG
     */
    class ReportListRecord : BaseColumns {

        companion object {
            val REPORT_TABLE_NAME = "ReportListRecord" // 数据库表名
            val CONTENT_URI = Uri.parse("content://"+ AUTHORITY+"/"+ REPORT_TABLE_NAME)
            val CONTENT_TYPE = "vnd.android.cursor.dir/" + REPORT_TABLE_NAME
            val CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + REPORT_TABLE_NAME
            val DEFAULT_ORDER = "_id asc"

            val ID = "_id"
            val USER_ID = "user_id"
            val MONTH = "month"
            val REPORT_RECORD_MODEL = "report_month_model" // 数据JSON

            val createReportListTBSQL = ("create table " + REPORT_TABLE_NAME + "("
                    + ID + " INTEGER primary key autoincrement,"
                    + USER_ID + " text,"
                    + MONTH + " text,"
                    + REPORT_RECORD_MODEL + " text)")
        }
    }
}