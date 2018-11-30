package cc.bodyplus.health.di.module

import cc.bodyplus.health.net.NetBaseConfig
import cc.bodyplus.health.net.converter.JsonConverterFactory
import cc.bodyplus.health.net.helper.OkHttpHelper
import cc.bodyplus.health.net.helper.RetrofitHelper
import cc.bodyplus.health.net.service.DataApi
import cc.bodyplus.health.net.service.LoginApi
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Singleton

/**
 * Created by shihoo.wang on 2018/4/19.
 * Email shihu.wang@bodyplus.cc
 */

@Module
class ApiModule {

    fun createRetrofit(builder: Retrofit.Builder, client: OkHttpClient, url: String): Retrofit
            = builder
            .baseUrl(url)
            .client(client)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//            .addConverterFactory(GsonConverterFactory.create())
            .addConverterFactory(JsonConverterFactory.create())
            .build()


    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient = OkHttpHelper.getOkHttpClient()

    @Singleton
    @Provides
    fun provideRetrofitBuilder(): Retrofit.Builder = Retrofit.Builder()

    /**
     * 写入需要的请求网络的对象加入到构造函数内
     */
    @Singleton
    @Provides
    fun provideRetrofitHelper(loginApi: LoginApi): RetrofitHelper
            = RetrofitHelper(loginApi)


    @Singleton
    @Provides
    fun provideBaseRetrofit(builder: Retrofit.Builder, client: OkHttpClient): Retrofit {

        var url = NetBaseConfig.ALI_OFFICIAL
        when(NetBaseConfig.NET_TYPE){
            NetBaseConfig.NET_TYPE_TEST -> url = NetBaseConfig.ALI_TEST
            NetBaseConfig.NET_TYPE_DEV -> url = NetBaseConfig.DEV_TEST
        }
        System.out.println("url............."+url)
        return createRetrofit(builder, client, url)
    }

    @Singleton
    @Provides
    fun provideLoginApi(retrofit: Retrofit): LoginApi
            = retrofit.create(LoginApi::class.java)

    @Singleton
    @Provides
    fun provideDataApi(retrofit: Retrofit): DataApi
            = retrofit.create(DataApi::class.java)

}
