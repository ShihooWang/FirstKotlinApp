package cc.bodyplus.health.net.helper

import cc.bodyplus.health.mvp.module.UserBean
import cc.bodyplus.health.net.service.LoginApi
import io.reactivex.Observable
import retrofit2.http.FieldMap

/**
 * Created by shihoo.wang on 2018/4/19.
 * Email shihu.wang@bodyplus.cc
 */

class RetrofitHelper (private val loginApi: LoginApi){

    fun login (url : String ,
              @FieldMap params : Map<String, String> ) : Observable<UserBean> = loginApi.login(url,params)

}