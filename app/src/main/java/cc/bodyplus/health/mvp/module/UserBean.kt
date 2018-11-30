package cc.bodyplus.health.mvp.module

/**
 * Created by rui.gao on 2018-05-09.
 */
data class UserBean(var mobile:String,var patientId :String,var isBeta :String = "0",var isPerfect: String= "0") {
    var realname: String = ""
    var avatar :String = ""
    var gender :String= ""
    var age: String = ""
}