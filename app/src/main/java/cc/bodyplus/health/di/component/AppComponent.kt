package cc.bodyplus.health.di.component

import android.content.Context
import cc.bodyplus.health.di.module.ApiModule
import cc.bodyplus.health.di.module.AppModule
import cc.bodyplus.health.net.helper.RetrofitHelper
import cc.bodyplus.health.net.service.DataApi
import cc.bodyplus.health.net.service.LoginApi
import dagger.Component
import javax.inject.Singleton


/**
 * Created by shihoo.wang on 2018/4/19.
 * Email shihu.wang@bodyplus.cc
 */

@Singleton
@Component(modules = arrayOf(AppModule::class, ApiModule::class))
interface AppComponent {

    fun getContext() : Context

    fun getRetrofitHelper() : RetrofitHelper

    fun getLoginApi() : LoginApi
    fun getDataApi() : DataApi
}
