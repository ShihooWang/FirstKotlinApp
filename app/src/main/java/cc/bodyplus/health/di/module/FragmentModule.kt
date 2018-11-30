package cc.bodyplus.health.di.module

import android.app.Activity
import android.app.Fragment
import cc.bodyplus.health.di.scope.ForFragment


import dagger.Module
import dagger.Provides

/**
 * Created by shihoo.wang on 2018/4/19.
 * Email shihu.wang@bodyplus.cc
 */

@Module
 class FragmentModule(val fragment: Fragment) {

    @Provides
    @ForFragment
    fun provideActivity(): Activity = fragment.activity

}
