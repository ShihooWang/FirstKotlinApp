package cc.bodyplus.health.di.component

import android.app.Activity
import cc.bodyplus.health.di.module.ActivityModule
import cc.bodyplus.health.di.scope.ForActivity
import cc.bodyplus.health.mvp.view.login.*
import cc.bodyplus.health.mvp.view.main.MainActivity
import cc.bodyplus.health.mvp.view.main.ReportFragment
import cc.bodyplus.health.mvp.view.monitor.activity.EcgListActivity2
import cc.bodyplus.health.mvp.view.monitor.activity.MonitorEcgTagActivity
import cc.bodyplus.health.mvp.view.report.activity.ReportActivity
import cc.bodyplus.health.mvp.view.report.fragment.ReportMonitorFragment
import dagger.Component


/**
 * Created by shihoo.wang on 2018/4/19.
 * Email shihu.wang@bodyplus.cc
 */

@ForActivity
@Component(dependencies = [AppComponent::class], modules = [ActivityModule::class])
interface ActivityComponent {

    fun getActivity(): Activity

    fun inject(loginActivity: LoginActivity)
    fun inject(activity: SplashActivity)
    fun inject(mainActivity: MainActivity)
    fun inject(activity: EnterSmsCodeActivity)
    fun inject(activity: EnterPhoneNumberActivity)
    fun inject(activity: RegisterGenderActivity)
    fun inject(activity: ProfileActivity)
    fun inject(activity: AboutActivity)
    fun inject(activity: MyDoctorActivity)
    fun inject(activity: MonitorEcgTagActivity)
    fun inject(activity: ReportFragment)
    fun inject(activity: ReportActivity)
    fun inject(activity: ReportMonitorFragment)
    fun inject(activity: EcgListActivity2)

}
