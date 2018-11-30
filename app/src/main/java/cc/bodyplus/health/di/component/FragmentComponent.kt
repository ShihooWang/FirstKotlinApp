package cc.bodyplus.health.di.component

import android.app.Activity
import cc.bodyplus.health.di.module.FragmentModule
import cc.bodyplus.health.di.scope.ForFragment
import cc.bodyplus.health.mvp.view.login.LoginFragment
import dagger.Component

/**
 * Created by shihoo.wang on 2018/4/19.
 * Email shihu.wang@bodyplus.cc
 */

@ForFragment
@Component(dependencies = [AppComponent::class], modules = [FragmentModule::class])
interface FragmentComponent {

    fun getActivity(): Activity

    fun inject(loginFragment: LoginFragment)


}
