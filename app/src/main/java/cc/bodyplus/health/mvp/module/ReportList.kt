package cc.bodyplus.health.mvp.module

import org.json.JSONObject

/**
 * Created by rui.gao on 2018-05-17.
 */
data class ReportList (var name:String = ""){
    var monthList :ArrayList<String> ?= null
    var detectList: JSONObject?= null

}