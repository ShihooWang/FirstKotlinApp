package cc.bodyplus.health.net.helper

import android.os.Environment
import cc.bodyplus.health.net.Interceptor.ApplicationInterceptor
import okhttp3.Cache
import okhttp3.OkHttpClient
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Created by shihoo.wang on 2018/4/19.
 * Email shihu.wang@bodyplus.cc
 */
object OkHttpHelper{

    @Volatile var mOkHttpClient: OkHttpClient

    init {

        val file = File(Environment.getExternalStorageDirectory().toString(), "OkHttpCache")
        if (!file.exists()) {
            try {
                file.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        val cache = Cache(file, (1024 * 1024 * 100).toLong())
        System.out.println("....................222222222222")

        mOkHttpClient = OkHttpClient.Builder()
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .connectTimeout(10, TimeUnit.SECONDS)
                .cache(cache)//设置缓存
                // 失败重发
                .retryOnConnectionFailure(true)
                //设置缓存
                .addInterceptor( ApplicationInterceptor())
                .build()
    }

    fun getOkHttpClient(): OkHttpClient = mOkHttpClient

}