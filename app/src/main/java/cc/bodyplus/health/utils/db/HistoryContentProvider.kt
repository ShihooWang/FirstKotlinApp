package cc.bodyplus.health.utils.db

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.SQLException
import android.net.Uri
import android.text.TextUtils

/**
 * Created by shihoo.wang on 2018/5/15.
 * Email shihu.wang@bodyplus.cc
 */
class HistoryContentProvider : ContentProvider() {


    companion object {
        var uriMatcher = UriMatcher(UriMatcher.NO_MATCH)

    }
    private var dbHelper: HistoryDataBaseHelper? = null
    private var orderBy: String? = null
//    var uriMatcher: UriMatcher? = null
    private var tableName: String ? = null

    override fun onCreate(): Boolean {
        dbHelper = HistoryDataBaseHelper.getDataHelperInstance(context)
        uriMatcher.addURI(History.AUTHORITY, History.HistoryReport.HISTORY_TABLE_NAME, History.HISTORY_ITEM)
        uriMatcher.addURI(History.AUTHORITY, History.HistoryReport.HISTORY_TABLE_NAME + "/#", History.HISTORY_REPORT_ID)
        uriMatcher.addURI(History.AUTHORITY, History.MonitorRecord.MONITOR_TABLE_NAME, History.MONITOR_ITEM)
        uriMatcher.addURI(History.AUTHORITY, History.MonitorRecord.MONITOR_TABLE_NAME + "/#", History.MONITOR_ID)

        return true
    }


    override fun insert(uri: Uri?, values: ContentValues?): Uri {
        val url_v = uriMatcher.match(uri)
        if ((url_v == History.HISTORY_ITEM) or (url_v == History.HISTORY_REPORT_ID)) {
            tableName = History.HistoryReport.HISTORY_TABLE_NAME
        } else if ((url_v == History.MONITOR_ITEM) or (url_v == History.MONITOR_ID)) {
            tableName = History.MonitorRecord.MONITOR_TABLE_NAME
        }

        val db = dbHelper?.getWritableDatabase()
        val rowId = db?.insert(tableName, null, values)
        rowId?.let {
            if (it.toInt() > 0) {
                // 发出通知给监听器，说明数据已经改变
                val insertedUserUri = ContentUris.withAppendedId(uri, it)
                context.contentResolver.notifyChange(insertedUserUri, null)
                return insertedUserUri
            }
        }

        throw SQLException("Failed to insert row into" + uri)
    }

    override fun query(uri: Uri?, projection: Array<out String>?, selection: String?, selectionArgs: Array<out String>?, sortOrder: String?): Cursor? {
        val ul = uriMatcher.match(uri)
        if ((ul == History.HISTORY_ITEM) or (ul == History.HISTORY_REPORT_ID)) {
            if (TextUtils.isEmpty(sortOrder)) {
                orderBy = History.HistoryReport.DEFAULT_ORDER// 传入的排序参数为空的时候采用默认的排序
            } else {
                orderBy = sortOrder// 不为空时用指定的排序方法进行排序
            }
            tableName = History.HistoryReport.HISTORY_TABLE_NAME
        } else if ((ul == History.MONITOR_ITEM) or (ul == History.MONITOR_ID)) {
            if (TextUtils.isEmpty(sortOrder)) {
                orderBy = History.MonitorRecord.DEFAULT_ORDER// 传入的排序参数为空的时候采用默认的排序
            } else {
                orderBy = sortOrder// 不为空时用指定的排序方法进行排序
            }
            tableName = History.MonitorRecord.MONITOR_TABLE_NAME
        }
        val db = dbHelper?.getWritableDatabase()
        // 采用传入的参数进行查询
        return db?.query(tableName, projection, selection, selectionArgs, null, null, orderBy)?:null
    }



    override fun update(uri: Uri?, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int {
        val ul = uriMatcher.match(uri)
        if ((ul == History.HISTORY_ITEM) or (ul == History.HISTORY_REPORT_ID)) {
            tableName = History.HistoryReport.HISTORY_TABLE_NAME
        } else if ((ul == History.MONITOR_ITEM) or (ul == History.MONITOR_ID)) {
            tableName = History.MonitorRecord.MONITOR_TABLE_NAME
        }
        val db = dbHelper?.getWritableDatabase()
        val rt = db?.update(tableName, values, selection, selectionArgs)
        this.context.contentResolver.notifyChange(uri, null)
        return rt ?:-1
    }

    override fun delete(uri: Uri?, selection: String, selectionArgs: Array<out String>): Int {
        val type = uriMatcher.match(uri)
        if ((type == History.HISTORY_ITEM) or (type == History.HISTORY_REPORT_ID)) {
            tableName = History.HistoryReport.HISTORY_TABLE_NAME
        } else if ((type == History.MONITOR_ITEM) or (type == History.MONITOR_ID)) {
            tableName = History.MonitorRecord.MONITOR_TABLE_NAME
        }
        val db = dbHelper?.getWritableDatabase()
        //		if(!tableName.equals(History.Outdoor.OUTDOOR_TABLE_NAME)){ // 通知内容提供者数据有变化
//			this.getContext().getContentResolver().notifyChange(uri,null);
//		}

        return db?.delete(tableName, selection, selectionArgs) ?: -1
    }

    override fun getType(uri: Uri?): String {
        uriMatcher.let {
            when (it.match(uri)) {
            // matcher满足URL的前2项（即协议+路径）为第1种情况时，switch语句的值为URL的第3项
                History.HISTORY_ITEM -> return History.HistoryReport.CONTENT_TYPE
                History.HISTORY_REPORT_ID -> return History.HistoryReport.CONTENT_ITEM_TYPE
                History.MONITOR_ITEM -> return History.MonitorRecord.CONTENT_TYPE
                History.MONITOR_ITEM -> return History.MonitorRecord.CONTENT_ITEM_TYPE
                else -> throw IllegalArgumentException("Unknown URI" + uri)
            }
        }
    }
}