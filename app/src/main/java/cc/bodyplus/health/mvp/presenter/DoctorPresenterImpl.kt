package cc.bodyplus.health.mvp.presenter

import cc.bodyplus.health.App
import cc.bodyplus.health.mvp.module.DoctorBean
import cc.bodyplus.health.net.util.NetLoginConfig
import cc.bodyplus.health.net.util.RxjavaHelperUtil
import io.reactivex.Observable
import io.reactivex.ObservableTransformer

/**
 * Created by rui.gao on 2018-05-17.
 */
class DoctorPresenterImpl {
    fun doDoctor(accountInfo: Map<String, String>): Observable<ArrayList<DoctorBean>> {
        return  App.instance.appComponent.getLoginApi().doDoctor(NetLoginConfig.GET_MY_DOCTOR, accountInfo).compose(RxjavaHelperUtil.schedulersTransformer() as ObservableTransformer<ArrayList<DoctorBean>, ArrayList<DoctorBean>>)
    }
}