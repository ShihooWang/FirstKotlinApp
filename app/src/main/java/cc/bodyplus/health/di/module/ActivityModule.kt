package cc.bodyplus.health.di.module

import android.app.Activity
import cc.bodyplus.health.di.scope.ForActivity

import dagger.Module
import dagger.Provides

/**
 * Created by shihoo.wang on 2018/4/19.
 * Email shihu.wang@bodyplus.cc
 */

@Module
class ActivityModule(val activity: Activity) {

    @Provides
    @ForActivity
    fun provideActivity(): Activity = activity

}
