package cc.bodyplus.health.di.module

import android.content.Context

import dagger.Module
import dagger.Provides


/**
 * Created by shihoo.wang on 2018/4/19.
 * Email shihu.wang@bodyplus.cc
 */

@Module
class AppModule(val context: Context) {

    @Provides
    fun provideContext(): Context = context

}
